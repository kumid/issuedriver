const functions = require('firebase-functions');
const firebase = require('firebase-admin');
const express = require('express');
const bodyParser = require('body-parser')
const cors = require('cors')({origin: true});


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


async function getFeedbacks(accepted) {
    const snapshot = await db.collection('feedbacks').where('accept', '==', accepted).get();
    return snapshot;
}
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




let usersLocalCollection = []
let usersLocalCollectionMode = true;
let listeners = []    // list of listeners
let start = null      // start position of listener
let end = null        // end position of listener
const rowsInPage = 3;

async function getUsers(accept, next) {
    if(usersLocalCollectionMode != accept) {
        usersLocalCollection = [];
        start = null;
    }

    usersLocalCollectionMode = accept;
    let query = db.collection('users').where('accept', '==', accept);
    let index = 0;
    if(next) {
        query = query.startAt(start);
    } else {
        usersLocalCollection = [];
        index = 1;
    }


    const snapshots = await query.limit(rowsInPage).get();
    // // save startAt snapshot
    let newPagesEnd = snapshots.docs[snapshots.docs.length - 1]
    if(newPagesEnd == start)
        return null;

    start = newPagesEnd;
    snapshots.forEach(function(childSnapshot) {
        if(index != 0) {
            let obj = getObjectFromUserSnapshot(childSnapshot);
            usersLocalCollection.push(obj);
        }
        index++;
    });

    return usersLocalCollection;
    // return snapshots;
}




async function getUser(id) {
    const snapshot = await db.collection('users').doc(id).get();
    return snapshot;
}
async function setUserReturnAccept(body) {
    let is_performer = body.is_performer === 'on' ? true : false;
    let accept = body.accept === 'on' ? true : false;

    await db.collection('users').doc(body.email).update(
        'fio', body.fio,
        'staff', body.staff,
        'corp', body.corp,
        'is_performer', is_performer,
        'tel', body.tel,
        'accept', accept);
}
async function deleteUser(id) {
    await db.collection('users').doc(id).delete();
}
function getObjectFromUserSnapshot(childSnapshot) {
    let obj;
    try {
        var item = childSnapshot.data();

        if (item.photoPath.length == 0)
            item.photoPath = 'https://firebasestorage.googleapis.com/v0/b/fleet-management-8dfc9.appspot.com/o/placeholder-man.png?alt=media&token=04920483-9eb3-4f69-8637-0543d75e5aec';

        let accepted = item.accept ? 'ДА' : 'НЕТ';
        let isperformer = item.is_performer ? 'ДА' : 'НЕТ';

        obj = {
            'key': childSnapshot.id,
            'fio': item.fio,
            'staff': item.staff,
            'email': childSnapshot.id,
            'corp': item.corp,
            'tel': item.tel,
            'accept': accepted,
            'is_performer': isperformer,
            'UUID': item.UUID,
            'photoPath': item.photoPath
        };

    } catch (err) {

        obj = {
            'key': childSnapshot.id,
            'fio': 'Ошибка в данных',
            'staff': '',
            'email': childSnapshot.id,
            'corp': '',
            'tel': '',
            'accept': '',
            'is_performer': '',
            'UUID': '',
            'photoPath': 'https://firebasestorage.googleapis.com/v0/b/fleet-management-8dfc9.appspot.com/o/placeholder-man.png?alt=media&token=04920483-9eb3-4f69-8637-0543d75e5aec'
        };
    }
    return obj;
}
function getDataFromUsersCollection(emails) {

    const lst = [];

    emails.forEach(function(childSnapshot) {

        let obj = getObjectFromUserSnapshot(childSnapshot);

        lst.push(obj);

    });

    return lst;
}







app.get('/', function(req, res){
    res.render('index', {someinfo: 'hello'});
})


app.get('/users', function(req, res) {
    const id = req.query.id;
    const paging = req.query.paging;
    if(id) {
        getUser(id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('user', {current_user: getObjectFromUserSnapshot(doc)});
            }
        });
    } if(paging){
        if(paging == 'next') {
            getUsers(true, paging).then((emails) => {
                if(emails)
                    res.render('users', {emails: emails, 'accept': true});
                // res.render('users', {emails: getDataFromUsersCollection(emails), 'accept': true});
            });
        } else {

        }
    }
    else { // нет GET аргумента - выдать весь список
        getUsers(true, null).then((emails) => {
            res.render('users', {emails: emails, 'accept': true});
            // res.render('users', {emails: getDataFromUsersCollection(emails), 'accept': true});
        });
    }
});


app.get('/users/unconfirmed', function(req, res){
    const id = req.query.id;
    const paging = req.query.paging;
    if(id) {
        getUser(id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('user', {current_user: getObjectFromUserSnapshot(doc)});
            }
        });
    }if(paging) {
        if (paging == 'next') {
            getUsers(false, paging).then((emails) => {
                if (emails)
                    res.render('users', {emails: emails, 'accept': false});
                // res.render('users', {emails: getDataFromUsersCollection(emails), 'accept': true});
            });
        } else {

        }
    }
    else { // нет GET аргумента - выдать весь список
        getUsers(false, null).then((emails) => {
            // res.render('users', {emails: emails}); //, id: req.params.id
            res.render('users', {emails: emails, 'accept': false}); //, id: req.params.id
        });
    }
});

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
        getCars().then((cars) => {
            res.render('cars', {cars: getDataFromCarsCollection(cars)});
        });
    }
});

app.get('/cars/newcar', function(req, res){
     res.render('car', {current_car: createCarData()});
});




app.get('/feedbacks/accept/:id', async function(req, res){
    setFeedbackAccept(req.params.id).then(r => {
        getFeedbacks(false).then((messages) =>{
            res.redirect('/feedbacks');
        });
    });
});
app.get('/feedbacks', async function(req, res){
    getFeedbacks(false).then((messages) =>{
        res.render('feedbacks', {feedbacks: messages, mode: 'new'}); //, id: req.params.id
    });
});
app.get('/feedbacks/archive', async function(req, res){
    getFeedbacks(true).then((messages) =>{
        res.render('feedbacks', {feedbacks: messages, mode: 'archive'}); //, id: req.params.id
    });
});



app.post('/users', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

    if(req.body.action == 'Update') {
        setUserReturnAccept(req.body).then(r => {
            if(req.body.accept === 'on')
                res.redirect('/users');
            else
                res.redirect('/users/unconfirmed');
        });
    } else {
        deleteUser(req.body.email).then(r => {
            res.redirect('/users');
        });
    }
})



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