const express = require('express')
const passport = require('passport')
const router = express.Router()

const Cookies = require('cookies');

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
                console.log('functions - Auth:', exception.toString());
                res.redirect('/login')
            });

        if (!documentSnapshot.exists) {
            console.log('functions - Auth:', 'no doc email');
            res.redirect('/login')
        } else {

            let newCookie = new Cookies(req, res);
            // console.log(req.user);
            res.setHeader('Cache-Control', 'private');
            newCookie.set('__session', req.user.googleId, {
                maxAge: 8 * 3600 * 1000 , // срок в мс
            });
            console.log(`............................../google/callback= ${req.user.email}.........................`)
            res.redirect('/')
        }
    }
)

// @desc    Logout user
// @route   /auth/logout
router.get('/logout', (req, res) => {
    req.logout();
    let newCookie = new Cookies(req, res);
    // console.log(req.user);
    res.setHeader('Cache-Control', 'private');
    newCookie.set('__session', '', {
        maxAge: 100 , // срок в мс
    });
    res.redirect('/login')
})

module.exports = router
