let carsLocalCollection = []
let carsLocalCollectionMode = true;
let listeners = []    // list of listeners
let start = null      // start position of listener
let end = null        // end position of listener
const rowsInPage = 3;

module.exports.getCars = async function getCars(db) {
    const snapshot = await db.collection('cars').get();
    return snapshot;
}

module.exports.getDataFromCarsCollection = function getDataFromCarsCollection(cars) {

    const lst = [];

    cars.forEach(function(childSnapshot) {

        let obj = getObjectFromCarSnapshot(childSnapshot);

        lst.push(obj);

    });

    return lst;
}
module.exports.getCar = async function getCar(db, id) {
    const snapshot = await db.collection('cars').doc(id).get();
    return snapshot;
}

function getObjectFromCarSnapshot(childSnapshot) {
    let obj;
    try {
        var item = childSnapshot.data();
        obj = {
            'key': childSnapshot.id,
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
            'key': childSnapshot.id,
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

module.exports.createCarData = function createCarData(body) {
    if(body)
        return {
            'marka': body.marka.trim(),
            'model': body.model.trim(),
            'vin': body.vin.trim(),
            'gos_number': body.gos_number.trim(),
            'osago_number': body.osago_number.trim(),
            'osago_start_date': body.osago_start_date,
            'osago_expire_date': body.osago_expire_date,
            'texservice_start_date': body.texservice_start_date,
            'texservice_expire_date': body.texservice_expire_date
        };

    return {
        'key':'',
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

module.exports.addNewCar = async function addNewCar(db, body) {
    await db.collection('cars').doc(body.gos_number.trim()).set(createCarData(body));
}
module.exports.updateCar =async function updateCar(db, body) {
    await db.collection('cars').doc(body.gos_number).update('marka', body.marka,
        'model', body.model,
        'vin', body.vin,
        'gos_number', body.gos_number,
        'osago_number', body.osago_number,
        'osago_start_date', body.osago_start_date,
        'osago_expire_date', body.osago_expire_date,
        'texservice_start_date', body.texservice_start_date,
        'texservice_expire_date', body.texservice_expire_date);
}
module.exports.deleteCar = async function deleteCar(db, id) {
    await db.collection('cars').doc(id).delete();
}

module.exports.getObjectFromCarSnapshot = getObjectFromCarSnapshot;