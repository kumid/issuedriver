module.exports.getOptions = async function getOptions(db) {
    console.log('functions - getOptions:', '1');
    let sender = await db.collection('options').doc('sender').get()
        .catch(function (exception) {
            console.log('functions - getOptions:', exception.toString());
        });
    // console.log('functions - getOptions:', '2');

    let login, pass, zavgar;

    if (!sender.exists) {
        login = '';
            pass = '';
        console.log('functions - getOptions:', 'No docs');

    } else {
        login = sender.data().login;
        pass = sender.data().pass;
        // console.log('functions - getOptions:', '3');
    }


    let reciever = await db.collection('options').doc('reciever').get();
    // console.log('functions - getOptions:', '4');

    if (!reciever.exists) {
        zavgar = '';
        console.log('functions - getOptions:', 'no doc zavgar');

    } else {
        zavgar = reciever.data().zavgar;
        // console.log('functions - getOptions:', '5');
    }

    // console.log('functions - getOptions:', login + '-' + pass +'-'+zavgar);
    // console.log('functions - getOptions:', 'OK');

    return  {
            sender_login: login,
            sender_pass: pass,
            zavgar_login: zavgar
        };

}

module.exports.updateOptions = async function updateOptions(db, body) {
    await db.collection('options').doc('sender')
        .update('login', body.login,
        'pass', body.pass);
    await db.collection('options').doc('reciever')
        .update('zavgar', body.loginzavgar );
}


var nodemailer = require('nodemailer');
var moment = require('moment');
moment.lang('ru');

function getTable4Osago(snapshots, theDate) {
    let result = '<table cellspacing="0">'+
    '<thead>'+
    '<tr>'+
    '   <th width="50px" style="border: black 1px solid;">#</th>'+
    '   <th width="100px"  style="border: black 1px solid;">Госномер</th>' +
    '   <th width="100px"  style="border: black 1px solid;">Осаго №</th>' +
    '   <th width="100px"  style="border: black 1px solid;">Осаго срок</th>' +
    '</tr>' +
    '</thead>' +
    '<tbody>';

    let num = 1;
    let itemDate;
    let datStr;
    snapshots.forEach(function(childSnapshot) {
        let item = childSnapshot.data();

        itemDate = moment(item.osago_expire_date);
        if(itemDate.isValid())
            datStr = itemDate.format('DD.MM.YYYY') + ' г.';
        else
            datStr = 'Ошибка';

        if(datStr === 'Ошибка' || theDate > itemDate) {
            result += '<tr>' +
                '<td  style="border: black 1px solid;">' + num++ + '</td>' +
                '<td  style="border: black 1px solid;">' + item.gos_number + '</td>' +
                '<td  style="border: black 1px solid;">' + item.osago_number + '</td>' +
                '<td  style="border: black 1px solid;">' + datStr + '</td>' +
                '</tr>';
        }
    });

    result += '</tbody>' +
        '</table>';

    return result;
}


function getTable4TO(snapshots, theDate) {
    let result = '<table cellspacing="0">'+
        '<thead>'+
        '<tr>'+
        '   <th width="50px" style="border: black 1px solid;">#</th>'+
        '   <th width="100px"  style="border: black 1px solid;">Госномер</th>' +
        '   <th width="100px"  style="border: black 1px solid;">Срок ТО</th>' +
        '</tr>' +
        '</thead>' +
        '<tbody>';

    let num = 1;
    let itemDate;
    let datStr;
    snapshots.forEach(function(childSnapshot) {
        let item = childSnapshot.data();

        itemDate = moment(item.texservice_expire_date);
        if(itemDate.isValid())
            datStr = itemDate.format('DD.MM.YYYY') + ' г.';
        else
            datStr = 'Ошибка';

        if(datStr === 'Ошибка' || theDate > itemDate) {
            result += '<tr>' +
                '<td  style="border: black 1px solid;">' + num++ + '</td>' +
                '<td  style="border: black 1px solid;">' + item.gos_number + '</td>' +
                '<td  style="border: black 1px solid;">' + datStr + '</td>' +
                '</tr>';
        }
    });

    result += '</tbody>' +
        '</table>';

    return result;
}


module.exports.checkCarDocLiquide = async function checkCarDocLiquide(db) {
    let snapshots = await db.collection('cars').get();
    let theDate = moment();
    theDate.add(14, 'days');

    let result = '<h2>Автомашины. Срок ОСАГО</h2><br>';
    result += getTable4Osago(snapshots, theDate);
    result += '<br> <h2>Автомашины. Срок ТО</h2>';
    result += getTable4TO(snapshots, theDate);

    console.log('generate - OK');

    sendMail(null, null, null, result, db);

}

async function sendMail(destinationEmail, subj, msg, htmlResult, db){
    let sender = await db.collection('options').doc('sender').get();
    let reciever = await db.collection('options').doc('reciever').get();


    destinationEmail = reciever.data().zavgar; // 'umidkurbanov75@gmail.com';
    msg = 'That was easy - CAR!';
    subj = 'dayly check car';

    require('dotenv').config()
    // npm install dotenv --save

    const {SENDER_EMAIL,SENDER_PASSWORD} = process.env;

    let authData = nodemailer.createTransport({
        host:'smtp.gmail.com',
        port: 465,
        security: true,

        auth: {
            user: sender.data().login,
            pass: sender.data().pass
            // user: SENDER_EMAIL,
            // pass: SENDER_PASSWORD
        }
    });



    authData.sendMail({
        from: sender.data().login, //'kazanokcentre@gmail.com',
        to: destinationEmail,
        subject: subj,
        text: msg,
        html: htmlResult
    }).then(res=>console.log('successfully sent email'))
        .catch(err=>console.log(err));


}
//https://it-like.ru/bezopasnost-gmail-dvoynaya-autentifikaciya-google/
//https://devanswers.co/create-application-specific-password-gmail/