const express = require('express')
const router = express.Router()


const feedback = require('../utils/feedbackUtils');

router.get('/', function(req, res) {
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

router.get('/archive', function(req, res){
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

router.post('/', urlencodedParser, function (req, res) {
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

module.exports = router
