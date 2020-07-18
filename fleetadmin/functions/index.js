const functions = require('firebase-functions');
const firebase = require('firebase-admin');
const express = require('express');
var bodyParser = require('body-parser')

const app = express();
app.engine('ejs', require('ejs-locals'))
app.set('view engine', 'ejs');
app.set('views', __dirname + '/views');


// create application/x-www-form-urlencoded parser
var urlencodedParser = bodyParser.urlencoded({ extended: false })


const cors = require('cors')({origin: true});
const firebaseApp = firebase.initializeApp(
    {
      credential: firebase.credential.applicationDefault()
    }
//   // functions.config().firebase
);

const db = firebase.firestore();
const docRef = db.collection('users').doc('nodejs');

app.use(cors);

async function getUsers() {
    const snapshot = await db.collection('users').get();
    return snapshot;
}

async function getFeedbacks(accepted) {
    const snapshot = await db.collection('feedbacks').where('accept', '==', accepted).get();
    return snapshot;
}
async function setFeedbackAccept(id) {
    await db.collection('feedbacks').doc(id).update('accept', true);
}

function getLocalUsers(){
    var o1 = {fio: 'Курбанов Умид', email: 'kumid@inbox.ru', UUID: '1234567890',
        photoPath: "https://firebasestorage.googleapis.com/v0/b/fleet-management-8dfc9.appspot.com/o/20200714%2FJPEG_20200714_104138833.jpg?alt=media&token=56ff1cdd-7bce-46c5-8e6a-27b89d49e1f4"};
    var o2 = {fio: 'Кадиров Рафик', email: 'kazanokcentre@mail.ru', UUID: '789789789',
        photoPath: 'https://firebasestorage.googleapis.com/v0/b/fleet-management-8dfc9.appspot.com/o/1.jpg?alt=media&token=e6341e13-9b80-4b53-98cf-7bbc5dff5a8a'};
    var o3 = {fio: 'Кадиров Шох', email: 'kumid@inbox.ru', UUID: '123123123',
        photoPath: 'https://firebasestorage.googleapis.com/v0/b/fleet-management-8dfc9.appspot.com/o/image1.jpg?alt=media&token=a72d5186-4bb4-42dc-828e-a47bc4174dc3'};

    var lst = {o1, o2, o3};

    return lst;
}



app.get('/', function(req, res){
   res.render('index', {someinfo: 'hello'});
})


async function getUser(id) {
    const snapshot = await db.collection('users').doc(id).get();
    // const snapshot = await db.collection('users').where('UUID', '==', uuid).get();
    return snapshot;
}

app.get('/users/:id', function(req, res){
    getUser(req.params.id).then((doc) =>{
        if (!doc.exists) {
            res.send('404');
        } else {
            res.render('user', {current_user: doc.data()});
        }
   });
})


app.get('/users', function(req, res){
    const id = req.query.id;
    if(id) {
        getUser(id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('user', {current_user: doc.data()});
            }
        });
    }
    else { // нет GET аргумента - выдать весь список
            getUsers().then((emails) => {
                res.render('users2', {emails: emails}); //, id: req.params.id
            });
    }
});


app.get('/feedbacks/accept/:id', async function(req, res){
    setFeedbackAccept(req.params.id).then(r => {
        getFeedbacks(false).then((messages) =>{
            res.redirect('/feedbacks');
            // res.render('feedbacks2', {feedbacks: messages, mode: 'new'}); //, id: req.params.id
        });
    });
});
app.get('/feedbacks', async function(req, res){
    getFeedbacks(false).then((messages) =>{
        res.render('feedbacks2', {feedbacks: messages, mode: 'new'}); //, id: req.params.id
    });
});


app.get('/feedbacks/archive', async function(req, res){
    getFeedbacks(true).then((messages) =>{
        res.render('feedbacks', {feedbacks: messages, mode: 'archive'}); //, id: req.params.id
    });
});







app.get('/localusers', function(req, res){
    res.render('localusers', {emails: getLocalUsers()}); //, id: req.params.id
})


async function setUserAccept(email, accept) {
    await db.collection('users').doc(email).update('accept', accept);
}

// POST /login gets urlencoded bodies
app.post('/users', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

    setUserAccept(req.body.email, req.body.accept ==='on' ? true:false).then(r => {
        res.redirect('/users');
    });
})



exports.app = functions.https.onRequest(app);