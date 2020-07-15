var express = require('express');
var logger = require('morgan')

var app = express();
app.use(logger('dev'));
app.set('view engine', 'ejs');
app.use(express.static('views'));
app.set('views', __dirname + '/views')
var port = process.env.PORT || 8080

app.get('/', function(rec, resp){
  // resp.send("<h1>My first html</h1>");
  resp.render('home.ejs')
});

app.listen(port, function(){
  console.log('App running on port - ' + port);
});
