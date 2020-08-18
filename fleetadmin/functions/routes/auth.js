const express = require('express');
const passport = require('passport');
const router = express.Router();

const Cookies = require('cookies');

const jwt = require('jsonwebtoken');
const mongoose = require('mongoose')
const Token = require('../models/Token')

// @desc    Auth with Google
// @route   GET /auth/google
router.get('/google', passport.authenticate('google', { scope: ['profile', 'email'] }))

// @desc    Google auth callback
// @route   GET /auth/google/callback
router.get(
    '/google/callback',
    passport.authenticate('google', { failureRedirect: '/' }),
    async (req, res) => {
        console.log(`functions - Auth1  profile === ${req.user.email}` );
        // console.log('functions - Auth1:', profile.email);
        // console.log('functions - Auth2:', profile.json);
        //
        let documentSnapshot  = await db.collection('admins').doc(req.user.email).get()
            .catch(function (exception) {
                console.error('functions - Auth:', exception.toString());
                res.redirect('/login')
            });

        if (!documentSnapshot.exists) {
            console.log('functions - Auth:', `${req.user.email} - no doc email`);
            res.redirect('/login')
        } else {

            const user = { name: req.user.email }
            const accessToken = jwt.sign(user, process.env.ACCESS_TOKEN_SECRET, { expiresIn: '36000s' });
            // const refreshToken = jwt.sign(user, process.env.REFRESH_TOKEN_SECRET)

            const newToken = {id: req.user.email.trim(),  accessToken: accessToken};

            await Token.remove({id: newToken.id});

            try {
                token = await Token.create(newToken);
            } catch (err) {
                console.error('.................Token.create..........................'+err)
            }

            let newCookie = new Cookies(req, res);
            // console.log(req.user);
            res.setHeader('Cache-Control', 'private');
            newCookie.set('__session', accessToken, {
                maxAge: 10 * 3600 * 1000 , // срок в мс
            });
            // console.log(`............................../google/callback= ${req.user.email}.........................`)
            res.redirect('/')
        }
    }
)

// @desc    Logout user
// @route   /auth/logout
router.get('/logout', async (req, res) => {
    req.logout();
//    await Token.remove({id: newToken.id});
    let currentCookie = new Cookies(req, res);
    let userSession = currentCookie.get('__session');
    // console.log(req.user);
    await Token.remove({ accessToken: userSession });
    res.setHeader('Cache-Control', 'private');
    currentCookie.set('__session', '', {
        maxAge: 100 , // срок в мс
    });
    res.redirect('/login')
})

module.exports = router
