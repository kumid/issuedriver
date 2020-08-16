const express = require('express')
const router = express.Router()


const {ensureAuth, ensureGuest} =  require('../middleware/auth')
const texservice = require('../utils/texserviceUtils');

router.get('/', ensureAuth,function(req, res) {
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
router.get('/archive',ensureAuth, function(req, res){
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
router.post('/', ensureAuth, urlencodedParser, function (req, res) {
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



module.exports = router
