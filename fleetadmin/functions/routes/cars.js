const express = require('express')
const router = express.Router()

const carUtils = require('../utils/carUtils');

router.get('/', function(req, res){
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
router.get('/newcar', function(req, res){
    res.render('car', {current_car: carUtils.createCarData(null)});
});
router.post('/newcar', urlencodedParser, function (req, res) {
    if(!req.body) return res.sendStatus(400);

    carUtils.addNewCar(db, req.body).then(r => {
        res.redirect('/cars');
    });
})
router.post('/', urlencodedParser, function (req, res) {
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




module.exports = router
