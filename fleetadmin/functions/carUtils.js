let carsLocalCollection = []
let listeners = []    // list of listeners
let start = null      // start position of listener
let end = null        // end position of listener
const rowsInPage = 3;

module.exports.getCars = async function getCars(db, next) {
    // const snapshot = await db.collection('cars').get();
    // return snapshot;

    let query = db.collection('cars').orderBy('gos_number');
    let index = 0;
    if(next) {
        query = query.startAt(start);
    } else {
        carsLocalCollection = [];
        index = 1;
    }


    const snapshots = await query.limit(rowsInPage).get();
    // // save startAt snapshot
    let newPagesEnd = snapshots.docs[snapshots.docs.length - 1]
    if(newPagesEnd == start)
        return [];

    start = newPagesEnd;
    snapshots.forEach(function(childSnapshot) {
        if(index != 0) {
            let obj = getObjectFromCarSnapshot(childSnapshot);
            carsLocalCollection.push(obj);
        }
        index++;
    });

    return carsLocalCollection;
    // return snapshots;
}

module.exports.getDataFromCarsCollection = function getDataFromCarsCollection(cars) {

    const lst = [];
    let iii = 0;
    cars.forEach(function(childSnapshot) {

        if(iii == 0) {
            let obj = getObjectFromCarSnapshot(childSnapshot);
            lst.push(obj);
        }
        iii++;
    });

    return lst;
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
            'pts': item.pts,
            'sts': item.sts,
            'gos_number': childSnapshot.id,
            'osago_number': item.osago_number,
            'osago_start_date': item.osago_start_date,
            'osago_expire_date': item.osago_expire_date,
            'texservice_start_date': item.texservice_start_date,
            'texservice_expire_date': item.texservice_expire_date
        };

    } catch (err) {
        obj = {
            'key': childSnapshot.id,
            'marka': childSnapshot.id + ': Ошибка данных',
            'model': '',
            'vin': '',
            'pts': '',
            'sts': '',
            'gos_number': childSnapshot.id,
            'osago_number': '',
            'osago_start_date': '',
            'osago_expire_date':  '',
            'texservice_start_date':'',
            'texservice_expire_date': ''
        };
    }
    return obj;
}


module.exports.getCar = async function getCar(db, id) {
    const snapshot = await db.collection('cars').doc(id).get();
    return snapshot;
}
function createCarData(body) {
    if(body)
        return {
            'marka': body.marka.trim(),
            'model': body.model.trim(),
            'vin': body.vin.trim(),
            'pts': body.pts.trim(),
            'sts': body.sts.trim(),
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
        'pts': '',
        'sts': '',
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
        'pts', body.pts,
        'sts', body.sts,
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
module.exports.createCarData = createCarData;