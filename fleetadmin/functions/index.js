const functions = require('firebase-functions');
const firebase = require('firebase-admin');
const express = require('express');


const app = express();
app.set('view engine', 'ejs');
app.set('views', './ejs');

const cors = require('cors')({origin: true});
const firebaseApp = firebase.initializeApp(
    {
      credential: firebase.credential.applicationDefault()
    }
//   // functions.config().firebase
);



const db = firebase.firestore();
const docRef = db.collection('users').doc('nodejs');

async function getUsers(){
    const snapshot = await db.collection('users').get();
    return snapshot;
}
app.use(cors);


app.get('/', function(req, res){
        // res.render('index', {someinfo: 'hello'});
    res.send('<h1>Hello</h1>');
})



app.get('/news/:id', function(req, res){
   getUsers().then((emails) =>{
        res.render('index', {emails: emails}); //, id: req.params.id
    });
})

exports.app = functions.https.onRequest(app);