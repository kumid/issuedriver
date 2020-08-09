const functions = require('firebase-functions');
const firebase = require('firebase-admin');
const express = require('express');
const bodyParser = require('body-parser')
const cors = require('cors')({origin: true});
const jwt = require('jsonwebtoken');
const Cookies = require('cookies');

var cookieParser = require('cookie-parser');

const feedback = require('./feedbackUtils');
const texservice = require('./texserviceUtils');
const shina = require('./shinaUtils');
const userUtils = require('./userUtils');
const carUtils = require('./carUtils');
const myutils = require('./utils');

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
    const filter = req.query.filter;
    if(id) {
        userUtils.getUser(db, id).then((doc) =>{
            if (!doc.exists) {
                res.sendStatus(404);
            } else {
                res.render('user', {current_user: userUtils.getObjectFromUserSnapshot(doc)});
            }
        });
    } else if(paging){
        if(paging == 'next') {
            userUtils.getUsers(db,true, paging, filter).then((emails) => {
                if(emails)
                    res.render('user_collection', {emails: emails, 'accept': true, searchMode: null});
            });
        } else {

        }
    } else if(filter){
        userUtils.getFilteredUsers(db, filter).then((emails) => {
                if(emails)
                    res.render('user_collection', {emails: emails, 'accept': true, searchMode: true});
                else
                    res.send('user_collection');
            });

    }
    else { // нет GET аргумента - выдать весь список
        userUtils.getUsers(db, true, null, null).then((emails) => {
            res.render('user_collection', {emails: emails, 'accept': true, searchMode: null});
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
                    res.render('user_collection', {emails: emails, 'accept': false, searchMode: null});
            });
        } else {

        }
    }
    else { // нет GET аргумента - выдать весь список
        userUtils.getUsers(db,false, null).then((emails) => {
            res.render('user_collection', {emails: emails, 'accept': false, searchMode: null}); //, id: req.params.id
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
            res.redirect('/users?aaaa=aaaa');
        });
    }
})

app.get('/maps', function(req, res) {
        // carUtils.getCars(db, null).then((cars) => {
        //     res.render('maps', {cars: cars});
        // });

        userUtils.getUsersWithPosition(db).then((users) => {
            res.render('maps', { users4map: users });
        });
});


app.get('/mapsupdate', function(req, res) {
    // carUtils.getCars(db, null).then((cars) => {
    //     res.render('maps', {cars: cars});
    // });

    userUtils.getUsersWithPosition(db).then((users) => {
        res.send(users);
    });
});


app.get('/userorders', function(req, res) {
    const id = req.query.id;
    const dateStart = req.query.start;
    const dateEnd = req.query.end;

    // if(!id || !dateStart || !dateEnd)
    //     return [];

    userUtils.getUserOrders(firebase, db, id, dateStart, dateEnd).then((orders) => {
        // userUtils.getUserOrders(db, id, null, null).then((orders) => {
        res.send(orders);
    });
});



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
        // npm install dotenv --save

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


// let exp = false;

exports.checkCarDocsLiquid = functions.pubsub
    // .schedule('every 1 minutes') //
    .schedule('every day 09:00')
    .timeZone('Europe/Moscow')
    .onRun(async _ctx => {
        // try {
        //     const topStories = await getTopStories()
        //
        //     await sendNewsletter(topStories, [
        //         EMAILS.daily,
        //         EMAILS.two_daily,
        //         EMAILS.three_daily,
        //     ])
        // } catch (error) {
        //     console.error(error)
        //     res.status(500).send(error)
        // }

        // if(exp)
        //     return ;
        // exp = true;

        myutils.checkCarDocLiquide(db);
        return null;

    });


app.get('/options', async function(req, res){
    // let current_options = myutils.getOptions(db);
    console.log('functions - getOptions:', '1');
    let sender = await db.collection('options').doc('sender').get()
        .catch(function (exception) {
            console.log('functions - getOptions:', exception.toString());
        });
    console.log('functions - getOptions:', '2');

    let login, pass, zavgar;

    if (!sender.exists) {
        login = '';
        pass = '';
        console.log('functions - getOptions:', 'No docs');

    } else {
        login = sender.data().login;
        pass = sender.data().pass;
        console.log('functions - getOptions:', '3');
    }


    let reciever = await db.collection('options').doc('reciever').get();
    console.log('functions - getOptions:', '4');

    if (!reciever.exists) {
        zavgar = '';
        console.log('functions - getOptions:', 'no doc zavgar');

    } else {
        zavgar = reciever.data().zavgar;
        console.log('functions - getOptions:', '5');
    }

    console.log('functions - getOptions:', login + '-' + pass +'-'+zavgar);
    console.log('functions - getOptions:', 'OK');

    let current_options =  {
        sender_login: login,
        sender_pass: pass,
        zavgar_login: zavgar
    };

    res.render('options', {current_options: current_options});
});

app.post('/options', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);
    myutils.updateOptions(db, req.body).then(r => {
        res.redirect('/');
    });
});










const passport = require('passport'),
    session = require('express-session'),
    GoogleStrategy = require('passport-google-oauth').OAuth2Strategy,
    flash = require('connect-flash'
    );


passport.serializeUser((user, done) => done(null, user));
passport.deserializeUser((user, done) => done(null, user));

// function checkAuth() {
//     return app.use((req, res, next) => {
//         console.log('FLEET -> AUTH 2-> user -> Start..................................', req.user);
//         if (req.user)
//             next();
//         else
//             res.redirect('/login');
//     });
// }

const cookieSession = require('cookie-session');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
// app.use(session({secret: 'you secret key'}));
// set up session cookies
app.use(cookieSession({
    // maxAge: 24 * 60 * 60 * 1000,
    maxAge: 1 * 60 * 1000,
    name: 'fleet-session',
    keys: [process.env.COOKIE_KEY, 'key2']
}));

// app.use(flash());
// app.use(cookieParser(process.env.COOKIE_KEY));
app.use(cookieParser());

app.use(passport.initialize());
app.use(passport.session());

passport.use(new GoogleStrategy({
        clientID: '940126965514-94dlavgqmgvuh7rb29chq03mm949e3uf.apps.googleusercontent.com', //YOUR GOOGLE_CLIENT_ID
        clientSecret: 'vPwSqwELOcpj6j9QRdEqWMCF', //YOUR GOOGLE_CLIENT_SECRET
        callbackURL: "https://fleet-management-8dfc9.firebaseapp.com/auth/google/callback"
    },
    (accessToken, refreshToken, profile, done) => {
        return done(null, profile);
    })
);



app.get('/logout', (req, res)=>{
   req.logout();
   // req.session = null;
   req.redirect('/');
});


const authCheck = (req, res, next) => {
    console.log('FLEET -> AUTH 2-> cookies -> Start..................................', req.headers['cookie']);
    // // console.log('FLEET -> AUTH 2-> cookies..................................', req.cookies['token']);
    //
    // // var tok = req.cookies['token'];
    // if(!req.session || !req.session.user_id){ //if (!tok) {//    if(!req.isAuthenticated()){//
    //     res.send('Not Authenticated')
    //     //res.sendStatus(401);
    // } else {
    next();
    // }
};

app.get('/failed', (req, res)=> res.send('You filed login'));
app.get('/good', authCheck, (req, res)=> {
    res.send('Welcome mr...')
}); //res.send(`Welcome mr.${req.user.email}`));  //

app.get('/google', passport.authenticate('google', { scope: ['email', 'profile']}));

app.get('/auth/google/callback', passport.authenticate('google', { failureRedirect: '/failed' }),
    function(req, res) {
        // Successful authentication, redirect home.
        // console.log(`FLEET -> AUTH 1-> Start...............................user..................................`);
        // console.log('FLEET -> AUTH 1-> session.id -> Start..................................', req.session.id);
        // console.log(`FLEET -> AUTH 1-> Start...............................user - ${req.user._json.email}..................................`);
        // res.cookie('session', 'test5', {
        //     maxAge: 5*60*1000,
        //     httpOnly: true,
        //     secure: true
        // })
        res.setHeader('Set-Cookie', 'session=test7');
        res.redirect('/good');
    });
