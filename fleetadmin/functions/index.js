const express = require('express');
const dotenv = require('dotenv');
const morgan = require('morgan');
const passport = require('passport')
const session = require('express-session')
const path = require('path');
const cors = require('cors')({origin: true});


const {ensureAuth, ensureGuest} =  require('./middleware/auth')

dotenv.config({path: './config/.env'});
require('./config/passport')(passport);

const connectDb = require('./config/db')
connectDb();


const app = express();

app.engine('ejs', require('ejs-locals'))
app.set('view engine', 'ejs');
app.set('views', __dirname + '/views');
app.use(express.static(path.join(__dirname, 'public')));

app.use(cors);

// Logging
if(process.env.NODE_ENV === 'development'){
    app.use(morgan('dev'));
}

// sessions
app.use(session({
    secret: 'keyboard cat',
    resave: false,
    saveUninitialized: false
}));

// pasport middleware
app.use(passport.initialize());
app.use(passport.session());


const functions = require('firebase-functions');
const firebase = require('firebase-admin');
const firebaseApp = firebase.initializeApp(
    {
        credential: firebase.credential.applicationDefault()
    }
//   // functions.config().firebase
);

global.db = firebase.firestore();

const bodyParser = require('body-parser')
// create application/x-www-form-urlencoded parser
global.urlencodedParser = bodyParser.urlencoded({ extended: false })



// Routers
app.use('/', require('./routes/root'));
app.use('/auth', require('./routes/auth'));
app.use('/users', require('./routes/users'));
app.use('/cars', require('./routes/cars'));

app.use('/feedback', require('./routes/feedback'));
app.use('/shina', require('./routes/shina'));
app.use('/texservice', require('./routes/texservice'));

const myutils = require('./utils/otherUtils');


// const jwt = require('jsonwebtoken');

//
// async function setFeedbackAccept(id) {
//     await db.collection('feedbacks').doc(id).update('accept', true);
// }






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












// passport.serializeUser((user, done) => done(null, user));
// passport.deserializeUser((user, done) => done(null, user));

// function checkAuth() {
//     return app.use((req, res, next) => {
//         console.log('FLEET -> AUTH 2-> user -> Start..................................', req.user);
//         if (req.user)
//             next();
//         else
//             res.redirect('/login');
//     });
// }

// const cookieSession = require('cookie-session');

// app.use(bodyParser.json());
// app.use(bodyParser.urlencoded({extended: true}));
// app.use(session({secret: 'you secret key'}));
// set up session cookies
// app.use(cookieSession({
//     // maxAge: 24 * 60 * 60 * 1000,
//     maxAge: 1 * 60 * 1000,
//     name: 'fleet-session',
//     keys: [process.env.COOKIE_KEY, 'key2']
// }));
//
// app.use(
//     session({
//         store: new FirebaseSession({  // <== connect-session-firebase
//             database: firebase.database(),
//         }),
//         name: 'ws_auth',
//         secret: 'mysecret',
//         secure: true,
//         httpOnly: true,
//         resave: false,
//         rolling: true,
//         cookie: { maxAge: 604800000 }, // week
//         saveUninitialized: false,
//         signed: true,
//     })
// );

// app.use(flash());
// app.use(cookieParser(process.env.COOKIE_KEY));
// app.use(cookieParser());

// app.use(passport.initialize());
// app.use(passport.session());

// passport.use(new GoogleStrategy({
//         clientID: '940126965514-94dlavgqmgvuh7rb29chq03mm949e3uf.apps.googleusercontent.com', //YOUR GOOGLE_CLIENT_ID
//         clientSecret: 'vPwSqwELOcpj6j9QRdEqWMCF', //YOUR GOOGLE_CLIENT_SECRET
//         callbackURL: "https://fleet-management-8dfc9.firebaseapp.com/auth/google/callback"
//     },
//     (accessToken, refreshToken, profile, done) => {
//         return done(null, profile);
//     })
// );



// app.get('/logout', (req, res)=>{
//    req.logout();
//    // req.session = null;
//    req.redirect('/');
// });


// const authCheck = (req, res, next) => {
//     console.log('FLEET -> AUTH 2-> cookies -> Start..................................', req.headers['cookie']);
//     // // console.log('FLEET -> AUTH 2-> cookies..................................', req.cookies['token']);
//     //
//     // // var tok = req.cookies['token'];
//     // if(!req.session || !req.session.user_id){ //if (!tok) {//    if(!req.isAuthenticated()){//
//     //     res.send('Not Authenticated')
//     //     //res.sendStatus(401);
//     // } else {
//     next();
//     // }
// };
//
// app.get('/failed', (req, res)=> res.send('You filed login'));
app.get('/dashboard', ensureAuth, (req, res)=> {
    res.send('Welcome mr...')
}); //res.send(`Welcome mr.${req.user.email}`));  //
//
// app.get('/google', passport.authenticate('google', { scope: ['email', 'profile']}));
//
// app.get('/auth/google/callback', passport.authenticate('google', { failureRedirect: '/failed' }),
//     function(req, res) {
//         // Successful authentication, redirect home.
//         // console.log(`FLEET -> AUTH 1-> Start...............................user..................................`);
//         // console.log('FLEET -> AUTH 1-> session.id -> Start..................................', req.session.id);
//         // console.log(`FLEET -> AUTH 1-> Start...............................user - ${req.user._json.email}..................................`);
//         // res.cookie('session', 'test5', {
//         //     maxAge: 5*60*1000,
//         //     httpOnly: true,
//         //     secure: true
//         // })
//         res.setHeader('Set-Cookie', 'session=test7');
//         res.redirect('/good');
//     });
