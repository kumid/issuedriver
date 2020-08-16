const express = require('express')
const router = express.Router()


const {ensureAuth, ensureGuest} =  require('../middleware/auth')
const shina = require('../utils/shinaUtils');

router.get('/', ensureAuth,function(req, res) {
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
router.get('/archive',ensureAuth, function(req, res){
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
router.post('/', ensureAuth, urlencodedParser, function (req, res) {
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



module.exports = router
