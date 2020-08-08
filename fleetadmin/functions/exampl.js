
const express = require('express');
const app = express();

app.use('/', (req, res)=>{
    console.log('Cookies client', req.headers['cookie']);
})

app.listen(3000, ()=>{
    console.log('server started');
})