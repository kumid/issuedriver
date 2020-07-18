const firebase = require('firebase-admin');
const express = require('express');


const app = express();

const cors = require('cors')({origin: true});
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
