// var functions = require('firebase-functions');
// var admin = require('firebase-admin');
//
// admin.initializeApp(functions.config().firebase);
//
// // Since this code will be running in the Cloud Functions environment
// // we call initialize Firestore without any arguments because it
// // detects authentication from the environment.
// const firestore = admin.firestore();
//
// // Create a new function which is triggered on changes to /status/{uid}
// // Note: This is a Realtime Database trigger, *not* Cloud Firestore.
// exports.onUserStatusChanged = functions.firestore
//     .document('/users/{uid}')
//     .onUpdate((change, context) => {
//
//         // Retrieve the current and previous value
//         const data = change.after.data();
//         const previousData = change.before.data();
//
//         if(data.email != "kazanokcentre@gmail.com")
//             return 0;
//
//         // We'll only update if the name has changed.
//         // This is crucial to prevent infinite loops.
//         if (data.state == previousData.state) {
//             return 0;
//         }
//
//
//         // Then return a promise of a set operation to update the count
//         data.staff = "водитель";
//
//         return  change.after.ref.set(data);
//
//         // Retrieve the current count of name changes
//         // let count = data.name_change_count;
//         // if (!count) {
//         //   count = 0;
//         // }
//         //
//         // // Then return a promise of a set operation to update the count
//         // return change.after.ref.set({
//         //   name_change_count: count + 1
//         // }, {merge: true});
//
//     });
//
// // Create a new function which is triggered on changes to /status/{uid}
// // Note: This is a Realtime Database trigger, *not* Cloud Firestore.
// exports.onOrderStateChanged = functions.firestore
//     .document('/orders/{uid}')
//     .onUpdate((change, context) => {
//
//         // Retrieve the current and previous value
//         const newdata = change.after.data();
//         const previousData = change.before.data();
//
//         if(newdata.customer_email != "progmaservice@gmail.com"
//             || newdata.customer_email != "progmaservice@gmail.com"
//             || newdata.performer_email != "kazanokcentre@gmail.com"
//             || newdata.performer_email != "kazanokcentre@gmail.com")
//             return 0;
//
//         // новое состояние заявки - заявка закрыта
//         // или старое состояние - принята водителем
//         if(newdata.completed == true
//             || previousData.accept == true)
//             return 0;
//
//
//         const docRef = firestore.collection('waybill').doc();
//
//         docRef.set({
//             fio: newdata.performer_fio,
//             address_from: newdata.from,
//             address_to: newdata.to,
//             car: newdata.car
//         });
//
//
//         require('dotenv').config()
//
//         const {SENDER_EMAIL,SENDER_PASSWORD} = process.env;
//
//         var nodemailer = require('nodemailer');
//
//         let authData = nodemailer.createTransport({
//             host:'smtp.gmail.com',
//             port: 465,
//             security: true,
//
//             auth: {
//                 user: SENDER_EMAIL,
//                 pass: SENDER_PASSWORD
//             }
//         });
//
//
//
//         authData.sendMail({
//             from: 'kazanokcentre@gmail.com',
//             to: 'umidkurbanov75@gmail.com',
//             subject: 'Sending Email using Node.js',
//             text: 'That was easy!',
//             html: '<h1>Welcome</h1><p>That was easy!</p>'
//         }).then(res=>console.log('successfully sent email'))
//             .catch(err=>console.log(err));
//
//
//
// // tyofzzclkrhqucfe
//         //  const send = require('gmail-send')({
//         //    user: 'kazanokcentre@gmail.com',
//         //    pass: '1qasw23ed',
//         //    to:   'umidkurbanov75@gmail.com',
//         //    subject: 'test subject',
//         //  });
//         //
//         //  send({
//         //   text:    'gmail-send example 1',
//         // }, (error, result, fullResult) => {
//         //   if (error) console.error(error);
//         //   console.log(result);
//         // });
//
//         return  0;
//
//     });
//
//
//
//
//
//







// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });


// exports.sendNotification = functions.database.ref('/articles/{articleId}')
//          .onWrite(event => {
//
//          // Grab the current value of what was written to the Realtime Database.
//          var eventSnapshot = event.data;
//          var str1 = "Author is ";
//          // var str = str1.concat(eventSnapshot.child("author").val());
//          // console.log(str);
//
//          var topic = "toall";
//          var payload = {
//              data: {
//                 title: "title", // title: eventSnapshot.child("title").val(),
//                 author: "author"// author: eventSnapshot.child("author").val()
//              }
//          };
//
//          // Send a message to devices subscribed to the provided topic.
//          return admin.messaging().sendToTopic(topic, payload)
//              .then(function (response) {
//                  // See the MessagingTopicResponse reference documentation for the
//                  // contents of response.
//                  console.log("Successfully sent message:", response);
//                  return 0;
//              })
//              .catch(function (error) {
//                  console.log("Error sending message:", error);
//                  return 0;
//              });
//          });



//
// function triggerSpecificDocument() {
//   // [START trigger_specific_document]
//   // Listen for any change on document `marie` in collection `users`
//   exports.myFunctionName = functions.firestore
//       .document('users/marie').onWrite((change, context) => {
//         // ... Your code here
//       });
//   // [END trigger_specific_document]
// }
//
// function triggerNewDocument() {
//   // [START trigger_new_document]
//   exports.createUser = functions.firestore
//       .document('users/{userId}')
//       .onCreate((snap, context) => {
//         // Get an object representing the document
//         // e.g. {'name': 'Marie', 'age': 66}
//         const newValue = snap.data();
//
//         // access a particular field as you would any JS property
//         const name = newValue.name;
//
//         // perform desired operations ...
//       });
//   // [END trigger_new_document]
// }
//
// function triggerDocumentUpdated() {
//   // [START trigger_document_updated]
//   exports.updateUser = functions.firestore
//       .document('users/{userId}')
//       .onUpdate((change, context) => {
//         // Get an object representing the document
//         // e.g. {'name': 'Marie', 'age': 66}
//         const newValue = change.after.data();
//
//         // ...or the previous value before this update
//         const previousValue = change.before.data();
//
//         // access a particular field as you would any JS property
//         const name = newValue.name;
//
//         // perform desired operations ...
//       });
//   // [END trigger_document_updated]
// }
//
// function triggerDocumentDeleted() {
//   // [START trigger_document_deleted]
//   exports.deleteUser = functions.firestore
//       .document('users/{userID}')
//       .onDelete((snap, context) => {
//         // Get an object representing the document prior to deletion
//         // e.g. {'name': 'Marie', 'age': 66}
//         const deletedValue = snap.data();
//
//         // perform desired operations ...
//       });
//   // [END trigger_document_deleted]
// }
//
// function triggerDocumentAnyChange() {
//   // [START trigger_document_any_change]
//   exports.modifyUser = functions.firestore
//       .document('users/{userID}')
//       .onWrite((change, context) => {
//         // Get an object with the current document value.
//         // If the document does not exist, it has been deleted.
//         const document = change.after.exists ? change.after.data() : null;
//
//         // Get an object with the previous document value (for update or delete)
//         const oldDocument = change.before.data();
//
//         // perform desired operations ...
//       });
//   // [END trigger_document_any_change]
// }
//
// function readingData() {
//   // [START reading_data]
//   exports.updateUser2 = functions.firestore
//       .document('users/{userId}')
//       .onUpdate((change, context) => {
//         // Get an object representing the current document
//         const newValue = change.after.data();
//
//         // ...or the previous value before this update
//         const previousValue = change.before.data();
//       });
//   // [END reading_data]
// }
//
// function readingDataWithGet(snap) {
//   // [START reading_data_with_get]
//   // Fetch data using standard accessors
//   const age = snap.data().age;
//   const name = snap.data()['name'];
//
//   // Fetch data using built in accessor
//   const experience = snap.get('experience');
//   // [END reading_data_with_get]
// }
//
// function writingData() {
//   // [START writing_data]
//   // Listen for updates to any `user` document.
//   exports.countNameChanges = functions.firestore
//       .document('users/{userId}')
//       .onUpdate((change, context) => {
//         // Retrieve the current and previous value
//         const data = change.after.data();
//         const previousData = change.before.data();
//
//         // We'll only update if the name has changed.
//         // This is crucial to prevent infinite loops.
//         if (data.name == previousData.name) {
//           return null;
//         }
//
//         // Retrieve the current count of name changes
//         let count = data.name_change_count;
//         if (!count) {
//           count = 0;
//         }
//
//         // Then return a promise of a set operation to update the count
//         return change.after.ref.set({
//           name_change_count: count + 1
//         }, {merge: true});
//       });
//   // [END writing_data]
// }
//
// function basicWildcard() {
//   // [START basic_wildcard]
//   // Listen for changes in all documents in the 'users' collection
//   exports.useWildcard = functions.firestore
//       .document('users/{userId}')
//       .onWrite((change, context) => {
//         // If we set `/users/marie` to {name: "Marie"} then
//         // context.params.userId == "marie"
//         // ... and ...
//         // change.after.data() == {name: "Marie"}
//       });
//   // [END basic_wildcard]
// }
//
// function multiWildcard() {
//   // [START multi_wildcard]
//   // Listen for changes in all documents in the 'users' collection and all subcollections
//   exports.useMultipleWildcards = functions.firestore
//       .document('users/{userId}/{messageCollectionId}/{messageId}')
//       .onWrite((change, context) => {
//         // If we set `/users/marie/incoming_messages/134` to {body: "Hello"} then
//         // context.params.userId == "marie";
//         // context.params.messageCollectionId == "incoming_messages";
//         // context.params.messageId == "134";
//         // ... and ...
//         // change.after.data() == {body: "Hello"}
//       });
//   // [END multi_wildcard]
// }