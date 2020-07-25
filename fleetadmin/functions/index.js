const functions = require('firebase-functions');
const firebase = require('firebase-admin');
const express = require('express');
const bodyParser = require('body-parser')
const cors = require('cors')({origin: true});


const feedback = require('./feedbackUtils');
const texservice = require('./texserviceUtils');
const shina = require('./shinaUtils');
const userUtils = require('./userUtils');
const carUtils = require('./carUtils');

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
                    res.render('user_collection', {emails: emails, 'accept': true});
            });
        } else {

        }
    }
    else { // нет GET аргумента - выдать весь список
        userUtils.getUsers(db, true, null).then((emails) => {
            res.render('user_collection', {emails: emails, 'accept': true});
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
                    res.render('user_collection', {emails: emails, 'accept': false});
            });
        } else {

        }
    }
    else { // нет GET аргумента - выдать весь список
        userUtils.getUsers(db,false, null).then((emails) => {
            res.render('user_collection', {emails: emails, 'accept': false}); //, id: req.params.id
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
    } else if(paging){
        if(paging == 'next') {
            feedback.getfeedbacks(db,false, paging).then((emails) => {
                if(emails)
                    res.render('feedback_collection', {emails: emails, 'accept': false});
                // res.render('users', {emails: getDataFromUsersCollection(emails), 'accept': true});
            });
        } else {
            res.send('/feedback - ' + paging);
        }
    }
    else { // нет GET аргумента - выдать весь список
        feedback.getfeedbacks(db, false, null).then((emails) => {
            if(emails)
                res.render('feedback_collection', {emails: emails, 'accept': false});
            else
                res.redirect('/');
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
            feedback.getfeedbacks(db,true, paging).then((emails) => {
                if (emails)
                    res.render('feedback_collection', {emails: emails, 'accept': true});
                else
                    res.redirect('/');
            });
        } else {
            res.send('/feedback/archive - ' + paging);
        }
    }
    else { // нет GET аргумента - выдать весь список
        feedback.getfeedbacks(db,true, null).then((emails) => {
            if(emails)
                res.render('feedback_collection', {emails: emails, 'accept': true}); //, id: req.params.id
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


app.get('/shina', function(req, res) {
    const id = req.query.id;
    const paging = req.query.paging;
    if(id) {
        shina.getshina(db, id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('shina', {current_shina: shina.getObjectFromshinaSnapshot(doc)});
            }
        });
    } else if(paging){
        if(paging == 'next') {
            shina.getshinas(db,false, paging).then((emails) => {
                if(emails)
                    res.render('shina_collection', {emails: emails, 'accept': false});
                // res.render('users', {emails: getDataFromUsersCollection(emails), 'accept': true});
            });
        } else {
            res.send('/shina - ' + paging);
        }
    }
    else { // нет GET аргумента - выдать весь список
        shina.getshinas(db, false, null).then((emails) => {
            if(emails)
                res.render('shina_collection', {emails: emails, 'accept': false});
            else
                res.redirect('/');
        });
    }
});
app.get('/shina/archive', function(req, res){
    const id = req.query.id;
    const paging = req.query.paging;
    if(id) {
        shina.getshina(db, id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('shina', {current_shina: shina.getObjectFromshinaSnapshot(doc)});
            }
        });
    } else if(paging) {
        if (paging == 'next') {
            shina.getshinas(db,true, paging).then((emails) => {
                if (emails)
                    res.render('shina_collection', {emails: emails, 'accept': true});
                else
                    res.redirect('/');
            });
        } else {
            res.send('/shina/archive - ' + paging);
        }
    }
    else { // нет GET аргумента - выдать весь список
        shina.getshinas(db,true, null).then((emails) => {
            if(emails)
                res.render('shina_collection', {emails: emails, 'accept': true}); //, id: req.params.id
        });
    }
});
app.post('/shina', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

    if(req.body.action == 'Update') {
        shina.setshinaReturnAccept(db, req.body).then(r => {
            if(req.body.accept === 'on')
                res.redirect('/shina');
            else
                res.redirect('/shina/archive');
        });
    } else {
        shina.deleteshina(db, req.body.email).then(r => {
            res.redirect('/shina');
        });
    }
})




app.get('/texservice', function(req, res) {
    const id = req.query.id;
    const paging = req.query.paging;
    if(id) {
        texservice.gettexservice(db, id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('texservice', {current_texservice: texservice.getObjectFromtexserviceSnapshot(doc)});
            }
        });
    } else if(paging){
        if(paging == 'next') {
            texservice.gettexservices(db,false, paging).then((emails) => {
                if(emails)
                    res.render('texservice_collection', {emails: emails, 'accept': false});
                // res.render('users', {emails: getDataFromUsersCollection(emails), 'accept': true});
            });
        } else {
            res.send('/texservice - ' + paging);
        }
    }
    else { // нет GET аргумента - выдать весь список
        texservice.gettexservices(db, false, null).then((emails) => {
            if(emails)
                res.render('texservice_collection', {emails: emails, 'accept': false});
            else
                res.redirect('/');
        });
    }
});
app.get('/texservice/archive', function(req, res){
    const id = req.query.id;
    const paging = req.query.paging;
    if(id) {
        texservice.gettexservice(db, id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('texservice', {current_texservice: shina.getObjectFromtexserviceSnapshot(doc)});
            }
        });
    } else if(paging) {
        if (paging == 'next') {
            texservice.gettexservices(db,true, paging).then((emails) => {
                if (emails)
                    res.render('texservice_collection', {emails: emails, 'accept': true});
                else
                    res.redirect('/');
            });
        } else {
            res.send('/texservice/archive - ' + paging);
        }
    }
    else { // нет GET аргумента - выдать весь список
        texservice.gettexservices(db,true, null).then((emails) => {
            if(emails)
                res.render('texservice_collection', {emails: emails, 'accept': true}); //, id: req.params.id
        });
    }
});
app.post('/texservice', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

    if(req.body.action == 'Update') {
        texservice.settexserviceReturnAccept(db, req.body).then(r => {
            if(req.body.accept === 'on')
                res.redirect('/texservice');
            else
                res.redirect('/texservice/archive');
        });
    } else {
        texservice.deletetexservice(db, req.body.email).then(r => {
            res.redirect('/texservice');
        });
    }
})



app.get('/cars', function(req, res){
    const id = req.query.id;
    const paging = req.query.paging;
    if(id) {
        carUtils.getCar(db, id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('car', {current_car: carUtils.getObjectFromCarSnapshot(doc)});
            }
        });
    } else  if (paging){
        if(paging == 'next') {
            carUtils.getCars(db, paging).then((cars) => {
                    res.render('cars', {cars: cars});
            });
        } else {
            res.send('/cars - ' + paging);
        }
    }
    else { // нет GET аргумента - выдать весь список
        // userUtils.
        carUtils.getCars(db, null).then((cars) => {
                res.render('cars', {cars: cars});
        });
    }
});
app.get('/cars/newcar', function(req, res){
     res.render('car', {current_car: carUtils.createCarData(null)});
});
app.post('/cars/newcar', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

    carUtils.addNewCar(db, req.body).then(r => {
         res.redirect('/cars');
     });
})
app.post('/cars', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

    if(req.body.action == 'Update') {
        carUtils.updateCar(db, req.body).then(r => {
            res.redirect('/cars');
        });
    } else {
        carUtils.deleteCar(db, req.body.gos_number).then(r => {
            res.redirect('/cars');
        });
    }
})

exports.app = functions.https.onRequest(app);


// Create a new function which is triggered on changes to /status/{uid}
// Note: This is a Realtime Database trigger, *not* Cloud Firestore.
exports.onUserStatusChanged = functions.firestore
    .document('/users/{uid}')
    .onUpdate((change, context) => {

        // Retrieve the current and previous value
        const data = change.after.data();
        const previousData = change.before.data();

        if(data.email != "kazanokcentre@gmail.com")
            return 0;

        // We'll only update if the name has changed.
        // This is crucial to prevent infinite loops.
        if (data.state == previousData.state) {
            return 0;
        }


        // Then return a promise of a set operation to update the count
        data.staff = "водитель";

        return  change.after.ref.set(data);

        // Retrieve the current count of name changes
        // let count = data.name_change_count;
        // if (!count) {
        //   count = 0;
        // }
        //
        // // Then return a promise of a set operation to update the count
        // return change.after.ref.set({
        //   name_change_count: count + 1
        // }, {merge: true});

    });

// Create a new function which is triggered on changes to /status/{uid}
// Note: This is a Realtime Database trigger, *not* Cloud Firestore.
exports.onOrderStateChanged = functions.firestore
    .document('/orders/{uid}')
    .onUpdate((change, context) => {

        // Retrieve the current and previous value
        const newdata = change.after.data();
        const previousData = change.before.data();

        if(newdata.customer_email != "progmaservice@gmail.com"
            || newdata.customer_email != "progmaservice@gmail.com"
            || newdata.performer_email != "kazanokcentre@gmail.com"
            || newdata.performer_email != "kazanokcentre@gmail.com")
            return 0;

        // новое состояние заявки - заявка закрыта
        // или старое состояние - принята водителем
        if(newdata.completed == true
            || previousData.accept == true)
            return 0;


        const docRef = db.collection('waybill').doc();

        docRef.set({
            fio: newdata.performer_fio,
            address_from: newdata.from,
            address_to: newdata.to,
            car: newdata.car
        });


        require('dotenv').config()

        const {SENDER_EMAIL,SENDER_PASSWORD} = process.env;

        var nodemailer = require('nodemailer');

        let authData = nodemailer.createTransport({
            host:'smtp.gmail.com',
            port: 465,
            security: true,

            auth: {
                user: SENDER_EMAIL,
                pass: SENDER_PASSWORD
            }
        });



        authData.sendMail({
            from: 'kazanokcentre@gmail.com',
            to: 'umidkurbanov75@gmail.com',
            subject: 'Sending Email using Node.js',
            text: 'That was easy!',
            html: '<h1>Welcome</h1><p>That was easy!</p>'
        }).then(res=>console.log('successfully sent email'))
            .catch(err=>console.log(err));



// tyofzzclkrhqucfe
        //  const send = require('gmail-send')({
        //    user: 'kazanokcentre@gmail.com',
        //    pass: '1qasw23ed',
        //    to:   'umidkurbanov75@gmail.com',
        //    subject: 'test subject',
        //  });
        //
        //  send({
        //   text:    'gmail-send example 1',
        // }, (error, result, fullResult) => {
        //   if (error) console.error(error);
        //   console.log(result);
        // });

        return  0;

    });

