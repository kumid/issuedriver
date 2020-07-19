const functions = require('firebase-functions');
const firebase = require('firebase-admin');
const express = require('express');
const bodyParser = require('body-parser')
// const userUtils = require('./userUtils')
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


app.get('/', function(req, res){
   res.render('index', {someinfo: 'hello'});
})


async function getUsers() {
    const snapshot = await db.collection('users').get();
    return snapshot;
}

async function getUser(id) {
    const snapshot = await db.collection('users').doc(id).get();
    // const snapshot = await db.collection('users').where('UUID', '==', uuid).get();
    return snapshot;
}
async function setUserAccept(body) {
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


app.get('/users', function(req, res){
    const id = req.query.id;
    if(id) {
        getUser(id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('user', {current_user: getObjectFromUserSnapshot(doc)});
            }
        });
    }
    else { // нет GET аргумента - выдать весь список
        getUsers().then((emails) => {
                // res.render('users', {emails: emails}); //, id: req.params.id
                res.render('users', {emails: getDataFromUsersCollection(emails)}); //, id: req.params.id
            });
    }
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



// POST /login gets urlencoded bodies
app.post('/users', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

    if(req.body.action == 'Update') {
        setUserAccept(req.body).then(r => {
            res.redirect('/users');
        });
    } else {
        deleteUser(req.body.email).then(r => {
            res.redirect('/users');
        });
    }
})



exports.app = functions.https.onRequest(app);