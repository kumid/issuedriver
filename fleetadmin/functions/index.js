const functions = require('firebase-functions');
const firebase = require('firebase-admin');
const express = require('express');
const bodyParser = require('body-parser')
const cors = require('cors')({origin: true});

const feedback = require('./feedback');
const userUtils = require('./userUtils');

const app = express();
app.engine('ejs', require('ejs-locals'))
app.set('view engine', 'ejs');
app.set('views', __dirname + '/views');
app.use(cors);


// create application/x-www-form-urlencoded parser
const urlencodedParser = bodyParser.urlencoded({ extended: false })


const firebaseApp = firebase.initializeApp(
    {
        credential: firebase.credential.applicationDefault()
    }
//   // functions.config().firebase
);

const db = firebase.firestore();


async function setFeedbackAccept(id) {
    await db.collection('feedbacks').doc(id).update('accept', true);
}


async function getCars() {
    const snapshot = await db.collection('cars').get();
    return snapshot;
}
async function getCar(id) {
    const snapshot = await db.collection('cars').doc(id).get();
    return snapshot;
}
function getObjectFromCarSnapshot(childSnapshot) {
    let obj;
    try {
        var item = childSnapshot.data();
        obj = {
            'key': childSnapshot.id,
            'marka': item.marka,
            'model': item.model,
            'vin': item.vin,
            'gos_number': item.gos_number,
            'osago_number': item.osago_number,
            'osago_start_date': item.osago_start_date,
            'osago_expire_date': item.osago_expire_date,
            'texservice_start_date': item.texservice_start_date,
            'texservice_expire_date': item.texservice_expire_date
        };

    } catch (err) {

        obj = {
            'key': childSnapshot.id,
            'marka': item.marka ? item.marka : '',
            'model': item.model ? item.model : '',
            'vin': item.vin ? item.vin : '',
            'gos_number': item.gos_number ? item.gos_number : '',
            'osago_number': item.osago_number ? item.osago_number : '',
            'osago_start_date': item.osago_start_date ? item.osago_start_date : '',
            'osago_expire_date': item.osago_expire_date ? item.osago_expire_date : '',
            'texservice_start_date': item.texservice_start_date ? item.texservice_start_date : '',
            'texservice_expire_date': item.texservice_expire_date ? item.texservice_expire_date : ''
        };
    }
    return obj;
}
function getDataFromCarsCollection(cars) {

    const lst = [];

    cars.forEach(function(childSnapshot) {

        let obj = getObjectFromCarSnapshot(childSnapshot);

        lst.push(obj);

    });

    return lst;
}
function createCarData(body) {
    if(body)
        return {
            'marka': body.marka.trim(),
            'model': body.model.trim(),
            'vin': body.vin.trim(),
            'gos_number': body.gos_number.trim(),
            'osago_number': body.osago_number.trim(),
            'osago_start_date': body.osago_start_date,
            'osago_expire_date': body.osago_expire_date,
            'texservice_start_date': body.texservice_start_date,
            'texservice_expire_date': body.texservice_expire_date
        };

    return {
        'key':'',
        'marka': '',
        'model': '',
        'vin': '',
        'gos_number': '',
        'osago_number': '',
        'osago_start_date': '',
        'osago_expire_date': '',
        'texservice_start_date': '',
        'texservice_expire_date': ''
    };
}
async function addNewCar(body) {
    await db.collection('cars').doc(body.gos_number.trim()).set(createCarData(body));
}
async function updateCar(body) {
    await db.collection('cars').doc(body.gos_number).update('marka', body.marka,
        'model', body.model,
        'vin', body.vin,
        'gos_number', body.gos_number,
        'osago_number', body.osago_number,
        'osago_start_date', body.osago_start_date,
        'osago_expire_date', body.osago_expire_date,
        'texservice_start_date', body.texservice_start_date,
        'texservice_expire_date', body.texservice_expire_date);
}




app.get('/', function(req, res){
    res.render('index', {someinfo: 'hello'});
})


app.get('/users', function(req, res) {
    const id = req.query.id;
    const paging = req.query.paging;
    if(id) {
        userUtils.getUser(db, id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('user', {current_user: userUtils.getObjectFromUserSnapshot(doc)});
            }
        });
    } if(paging){
        if(paging == 'next') {
            userUtils.getUsers(db,true, paging).then((emails) => {
                if(emails)
                    res.render('users', {emails: emails, 'accept': true});
                // res.render('users', {emails: getDataFromUsersCollection(emails), 'accept': true});
            });
        } else {

        }
    }
    else { // нет GET аргумента - выдать весь список
        userUtils.getUsers(db, true, null).then((emails) => {
            res.render('users', {emails: emails, 'accept': true});
            // res.render('users', {emails: getDataFromUsersCollection(emails), 'accept': true});
        });
    }
});
app.get('/users/unconfirmed', function(req, res){
    const id = req.query.id;
    const paging = req.query.paging;
    if(id) {
        userUtils.getUser(db, id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('user', {current_user: userUtils.getObjectFromUserSnapshot(doc)});
            }
        });
    }if(paging) {
        if (paging == 'next') {
            userUtils.getUsers(db,false, paging).then((emails) => {
                if (emails)
                    res.render('users', {emails: emails, 'accept': false});
                // res.render('users', {emails: userUtils.getDataFromUsersCollection(emails), 'accept': true});
            });
        } else {

        }
    }
    else { // нет GET аргумента - выдать весь список
        userUtils.getUsers(db,false, null).then((emails) => {
            // res.render('users', {emails: emails}); //, id: req.params.id
            res.render('users', {emails: emails, 'accept': false}); //, id: req.params.id
        });
    }
});
app.post('/users', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

    if(req.body.action == 'Update') {
        userUtils.setUserReturnAccept(db, req.body).then(r => {
            if(req.body.accept === 'on')
                res.redirect('/users');
            else
                res.redirect('/users/unconfirmed');
        });
    } else {
        userUtils.deleteUser(db, req.body.email).then(r => {
            res.redirect('/users');
        });
    }
})



app.get('/feedback', function(req, res) {
    const id = req.query.id;
    const paging = req.query.paging;
    if(id) {
        feedback.getfeedback(db, id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('feedback', {current_feedback: feedback.getObjectFromfeedbackSnapshot(doc)});
            }
        });
    } if(paging){
        if(paging == 'next') {
            feedback.getfeedbacks(db,true, paging).then((emails) => {
                if(emails)
                    res.render('feedback_collection', {emails: emails, 'accept': true});
                // res.render('users', {emails: getDataFromUsersCollection(emails), 'accept': true});
            });
        } else {

        }
    }
    else { // нет GET аргумента - выдать весь список
        feedback.getfeedbacks(db, true, null).then((emails) => {
            res.render('feedback_collection', {emails: emails, 'accept': true});
            // res.render('users', {emails: getDataFromUsersCollection(emails), 'accept': true});
        });
    }
});
app.get('/feedback/archive', function(req, res){
    const id = req.query.id;
    const paging = req.query.paging;
    if(id) {
        feedback.getfeedback(db, id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('feedback', {current_user: feedback.getObjectFromfeedbackSnapshot(doc)});
            }
        });
    }if(paging) {
        if (paging == 'next') {
            feedback.getfeedbacks(db,false, paging).then((emails) => {
                if (emails)
                    res.render('feedback_collection', {emails: emails, 'accept': false});
            });
        } else {

        }
    }
    else { // нет GET аргумента - выдать весь список
        feedback.getfeedbacks(db,false, null).then((emails) => {
            // res.render('users', {emails: emails}); //, id: req.params.id
            res.render('feedback_collection', {emails: emails, 'accept': false}); //, id: req.params.id
        });
    }
});
app.post('/feedback', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

    if(req.body.action == 'Update') {
        feedback.setfeedbackReturnAccept(db, req.body).then(r => {
            if(req.body.accept === 'on')
                res.redirect('/feedback');
            else
                res.redirect('/feedback/archive');
        });
    } else {
        feedback.deletefeedback(db, req.body.email).then(r => {
            res.redirect('/feedback');
        });
    }
})



app.get('/cars', function(req, res){
    const id = req.query.id;
    if(id) {
        getCar(id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('car', {current_car: getObjectFromCarSnapshot(doc)});
            }
        });
    }
    else { // нет GET аргумента - выдать весь список
        // userUtils.
        getCars().then((cars) => {
            res.render('cars', {cars: getDataFromCarsCollection(cars)});
        });
    }
});
app.get('/cars/newcar', function(req, res){
     res.render('car', {current_car: createCarData()});
});



app.post('/cars/newcar', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

     addNewCar(req.body).then(r => {
         res.redirect('/cars');
     });
})
app.post('/cars', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

    updateCar(req.body).then(r => {
        res.redirect('/cars');
    });

    // if(req.body.action == 'Update') {
    //     setUserReturnAccept(req.body).then(r => {
    //         res.redirect('/users');
    //     });
    // } else {
    //     deleteUser(req.body.email).then(r => {
    //         res.redirect('/users');
    //     });
    // }
})

exports.app = functions.https.onRequest(app);