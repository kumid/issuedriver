const functions = require('firebase-functions');
const firebase = require('firebase-admin');
const express = require('express');


const app = express();

app.engine('ejs', require("ejs"));
app.set('views', './views');
app.set('view engine', 'ejs');


const cors = require('cors')({origin: true});
const firebaseApp = firebase.initializeApp(
    {
      credential: firebase.credential.applicationDefault()
    }
//   // functions.config().firebase
);

app.get('/users/:id', function(req, res){
    getUser(req.params.id).then((doc) =>{
        if (!doc.exists) {
            res.send('404');
        } else {
            res.render('user', {current_user: doc.data()});
        }
    });
})

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