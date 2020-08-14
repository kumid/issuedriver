const express = require('express')
const router = express.Router()


const userUtils = require('../utils/userUtils');

router.get('/', function(req, res) {
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
router.get('/unconfirmed', function(req, res){
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
router.post('/', urlencodedParser, function (req, res) {
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


module.exports = router
