const functions = require('firebase-functions');
const firebase = require('firebase-admin');
const express = require('express');


const app = express();

app.engine('ejs', require("ejs"));
app.set('views', './ejs');
app.set('view engine', 'ejs');


const cors = require('cors')({origin: true});
const firebaseApp = firebase.initializeApp(
    {
      credential: firebase.credential.applicationDefault()
    }
//   // functions.config().firebase
);



app.get('/', (request, response) => {
  response.set('Cash-control', 'public', 'max-age=300, s-maxage=600');
  // var facts = ['Капуста', 'Репа', 'Редиска', 'Морковка'];
  //response.render('index', {body:'<b>Hello</b>'}); // {facts});
  response.send('Hello');
//   // const snapshot = await db.collection('users').get();
//   // snapshot.forEach((doc) => {
//   //   docRef.set({row: doc.id});
//   //   console.log(doc.id, '=>', doc.data());
//   // });
//
//
//   // getUsers().then(facts => {
//   //   docRef.set({row1: facts[1], row2: facts[2], row3: facts[3]});
//   //   response.render('index', {facts});
//   //   docRef.set({step: '4'});
//   // });
//
});

exports.app = functions.https.onRequest(app);
//
// const db = firebase.firestore();
// const docRef = db.collection('users').doc('nodejs');
//
// // async function getUsers(){
// //   docRef.set({step: '0'});
// //   // const snapshot = await db.collection('users').get();
// //   // docRef.set({step: '2'});
// //   var arr = [1,2];
// //   var num = 1;
// //   snapshot.forEach((doc) => {
// //     arr.push(num++);
// //     // arr.push(doc.id);
// //     // console.log(doc.id, '=>', doc.data());
// //   });
// //   docRef.set({step: '1'});
// //
// //   return arr;
// // }
//
//
//
// app.use(cors);
//
//
//
// app.get('/', (request, response) => {
//   response.set('Cash-control', 'public', 'max-age=300, s-maxage=600');
//
//   // const snapshot = await db.collection('users').get();
//   // snapshot.forEach((doc) => {
//   //   docRef.set({row: doc.id});
//   //   console.log(doc.id, '=>', doc.data());
//   // });
//
//
//   // getUsers().then(facts => {
//   //   docRef.set({row1: facts[1], row2: facts[2], row3: facts[3]});
//   //   response.render('index', {facts});
//   //   docRef.set({step: '4'});
//   // });
//
// });
//
//
//
//
//
//
//
//
//
//
// app.get('/timestamp', (request, response) => {
//   response.send(`${Date.now()}`);
// });
//
// app.get('/timestamp-cashed', (request, response) => {
//   response.set('Cash-control', 'public', 'max-age=300, s-maxage=600')
//   response.send(`${Date.now()}`);
// });
//