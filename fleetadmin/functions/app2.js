const express = require('express'),
    app = express(),
    bodyParser = require('body-parser'),
    session = require('express-session'),
    passport = require('passport'),
    GoogleStrategy = require('passport-google-oauth').OAuth2Strategy,
    flash = require('connect-flash');

const host = '127.0.0.1';
const port = 7000;

passport.serializeUser((user, done) => done(null, user));
passport.deserializeUser((user, done) => done(null, user));

function checkAuth() {
    return app.use((req, res, next) => {
        if (req.user)
            next();
        else
            res.redirect('/login');
    });
}


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use(session({secret: 'you secret key'}));
app.use(flash());
app.use(passport.initialize());
app.use(passport.session());

passport.use(new GoogleStrategy({
        clientID: '940126965514-94dlavgqmgvuh7rb29chq03mm949e3uf.apps.googleusercontent.com', //YOUR GOOGLE_CLIENT_ID
        clientSecret: 'vPwSqwELOcpj6j9QRdEqWMCF', //YOUR GOOGLE_CLIENT_SECRET
        callbackURL: "/auth/google/callback"
    },
    (accessToken, refreshToken, profile, done) => {
        return done(null, profile);
    })
);

app.get('/login', (req, res) => {
    res.send('Login page. Please, authorize. <a class="btn btn-outline-primary" href="/auth/google" >Sign in</a>');
});

app.get('/auth/google', passport.authenticate('google', {scope: ['email', 'profile']
}));

app.get('/auth/google/callback', passport.authenticate('google', {
    failureRedirect: '/login',
    successRedirect: '/home'
}));

app.get('/home', checkAuth(), (req, res) => {
    res.send('Home page. You\'re authorized.');
});

app.listen(port, host, function () {
    console.log(`Server listens http://${host}:${port}`);
});