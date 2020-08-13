const Cookies = require('cookies');

module.exports = {
    ensureAuth: function (req, res, next) {

        let newCookie = new Cookies(req, res);
        let userSession = newCookie.get('__session');
        if(userSession) { // if (req.isAuthenticated()) {
            console.log(`..............................COOKIE = ${userSession}.........................`)
            return next()
        } else {
            console.log('..............................isAuthenticated = FALSE.........................')
            res.redirect('/')
        }
    },
    ensureGuest: function (req, res, next) {
        if (!req.isAuthenticated()) {
            return next();
        } else {
            res.redirect('/dashboard');
        }
    },
}