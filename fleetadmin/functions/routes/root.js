const express = require('express')
const passport = require('passport')
const router = express.Router()


const {ensureAuth, ensureGuest} =  require('../middleware/auth')

const myutils = require('../utils/otherUtils');
const userUtils = require('../utils/userUtils');

router.get('/', ensureGuest, function(req, res){
    res.render('index', {someinfo: 'hello'});
})

// @desc    Set ptions
// @route   GET /options
router.get('/options', async function(req, res){
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

// @desc    Set ptions
// @route   POST /options
router.post('/options', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);
    myutils.updateOptions(db, req.body).then(r => {
        res.redirect('/');
    });
});





router.get('/maps', function(req, res) {
    // carUtils.getCars(db, null).then((cars) => {
    //     res.render('maps', {cars: cars});
    // });

    userUtils.getUsersWithPosition(db).then((users) => {
        res.render('maps', { users4map: users });
    });
});


router.get('/mapsupdate', function(req, res) {
    // carUtils.getCars(db, null).then((cars) => {
    //     res.render('maps', {cars: cars});
    // });

    userUtils.getUsersWithPosition(db).then((users) => {
        res.send(users);
    });
});


router.get('/userorders', function(req, res) {
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






module.exports = router
