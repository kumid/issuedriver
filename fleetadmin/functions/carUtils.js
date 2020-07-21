
const bodyParser = require('body-parser');

// create application/x-www-form-urlencoded parser
const urlencodedParser = bodyParser.urlencoded({ extended: false });


module.exports.createCarData = function createCarData(body) {
    if(body)
        return {
            'marka': body.marka,
            'model': body.model,
            'vin': body.vin,
            'gos_number': body.gos_number,
            'osago_number': body.osago_number,
            'osago_start_date': body.osago_start_date,
            'osago_expire_date': body.osago_expire_date,
            'texservice_start_date': body.texservice_start_date,
            'texservice_expire_date': body.texservice_expire_date
        };

    return {
        'key': '',
        'marka': '',
        'model': '',
        'vin': '',
        'gos_number': '',
        'osago_number': '',
        'osago_start_date': '',
        'osago_expire_date': '',
        'texservice_start_date': '',
        'texservice_expire_date': ''
    };
}


module.exports.getObjectFromCarSnapshot = function getObjectFromCarSnapshot(id, item) {
    let obj;
    try {
        obj = {
            'key': id,
            'marka': item.marka,
            'model': item.model,
            'vin': item.vin,
            'gos_number': item.gos_number,
            'osago_number': item.osago_number,
            'osago_start_date': item.osago_start_date,
            'osago_expire_date': item.osago_expire_date,
            'texservice_start_date': item.texservice_start_date,
            'texservice_expire_date': item.texservice_expire_date
        };

    } catch (err) {

        obj = {
            'key': id,
            'marka': item.marka ? item.marka : '',
            'model': item.model ? item.model : '',
            'vin': item.vin ? item.vin : '',
            'gos_number': item.gos_number ? item.gos_number : '',
            'osago_number': item.osago_number ? item.osago_number : '',
            'osago_start_date': item.osago_start_date ? item.osago_start_date : '',
            'osago_expire_date': item.osago_expire_date ? item.osago_expire_date : '',
            'texservice_start_date': item.texservice_start_date ? item.texservice_start_date : '',
            'texservice_expire_date': item.texservice_expire_date ? item.texservice_expire_date : ''
        };
    }
    return obj;
}