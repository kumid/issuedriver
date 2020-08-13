const express = require('express')
const passport = require('passport')
const router = express.Router()

const Cookies = require('cookies');

// @desc    Auth with Google
// @route   GET /auth/google
router.get('/google', passport.authenticate('google', { scope: ['profile'] }))

// @desc    Google auth callback
// @route   GET /auth/google/callback
router.get(
    '/google/callback',
    passport.authenticate('google', { failureRedirect: '/' }),
    (req, res) => {
        let newCookie = new Cookies(req, res);
        // console.log(req.user);
        res.setHeader('Cache-Control', 'private');
        newCookie.set('__session', req.user.googleId, {
            maxAge: 8 * 3600 * 1000 , // срок в мс
        });
        console.log(`............................../google/callback= ${req.user.googleId}.........................`)
        res.redirect('/dashboard')
    }
)

// @desc    Logout user
// @route   /auth/logout
router.get('/logout', (req, res) => {
    req.logout()
    res.redirect('/')
})

module.exports = router
