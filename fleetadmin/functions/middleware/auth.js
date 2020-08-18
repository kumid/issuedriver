const Cookies = require('cookies');

const jwt = require('jsonwebtoken');
const mongoose = require('mongoose')
const Token = require('../models/Token')
module.exports = {
    ensureAuth: async function (req, res, next) {

        let newCookie = new Cookies(req, res);
        let userSession = newCookie.get('__session');
        if(!userSession) {
            res.redirect('/login');
            return;
        }
        // const newToken = {accessToken: userSession};
        try{
            console.log('.................Token.findOne...............accessToken: ' + userSession)
            let token = await Token.findOne({ accessToken: userSession })
            if (token) {
                jwt.verify(userSession, process.env.ACCESS_TOKEN_SECRET, async (err, verify_token) => {
                    if (err) {
                        await Token.remove({id: token.id});
                        return res.redirect('/login')
                    }
                        // console.log('.................jwt.verify..........................' + verify_token.name)

                    return next();
                })
            } else {
                res.redirect('/login')
            }
        } catch (err) {
            console.error('.................Token.findOne..........................' + err)
            res.redirect('/login')
        }

        // if(userSession) { // if (req.isAuthenticated()) {
        //     console.log(`..............................COOKIE = ${userSession}.........................`)
        //     return next()
        // } else {
        //     console.log('..............................isAuthenticated = FALSE.........................')
        //     res.redirect('/login')
        // }
    },
    ensureGuest: function (req, res, next) {
        if (!req.isAuthenticated()) {
            return next();
        } else {
            res.redirect('/login');
        }
    },
}