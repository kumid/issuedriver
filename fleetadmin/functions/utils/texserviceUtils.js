
let texservicesLocalCollection = []
let texservicesLocalCollectionMode;
let start = null      // start position of listener
const rowsInPage = 3;


async function gettexservices(db, accept, next) {
    if(next && !start)
        next = null;

    if(texservicesLocalCollectionMode != accept) {
        texservicesLocalCollection = [];
        start = null;
        next = null;
    }

    texservicesLocalCollectionMode = accept;
    let query = db.collection('texservice').where('accept', '==', accept).orderBy('open_timestamp');
    let index = 0;
    if(next) {
        query = query.startAt(start);
    }
     else {
        texservicesLocalCollection = [];
        index = 1;
        start = null;
    }

    const snapshots = await query.limit(rowsInPage).get();
    // // save startAt snapshot
    let newPagesEnd = snapshots.docs[snapshots.docs.length - 1]
    if(newPagesEnd == start)
        return [];

    start = newPagesEnd;
    snapshots.forEach(function(childSnapshot) {
        if(index != 0) {
            let obj = getObjectFromtexserviceSnapshot(childSnapshot);
            texservicesLocalCollection.push(obj);
        }
        index++;
    });

    return texservicesLocalCollection;
    // return snapshots;
}

function getObjectFromtexserviceSnapshot(childSnapshot) {
    let obj;
    try {
        var item = childSnapshot.data();

        if (item.owner_photo.length == 0)
            item.owner_photo = 'https://firebasestorage.googleapis.com/v0/b/fleet-management-8dfc9.appspot.com/o/placeholder-man.png?alt=media&token=04920483-9eb3-4f69-8637-0543d75e5aec';

        let accepted = item.accept ? 'ДА' : 'НЕТ';
        let open_time  = '';
        if(item.open_timestamp)
            open_time = item.open_timestamp.toDate().toLocaleString('ru-RU');

        obj = {
            'key': childSnapshot.id,
            'owner_fio': item.owner_fio,
            'phone': item.phone,
            'message': item.message,
            'close_timestamp': item.close_timestamp,
            'open_timestamp': open_time,
            'accept': accepted,
            'reciever_id': item.reciever_id,
            'owner_email': item.owner_email,
            'owner_photo': item.owner_photo
        };

    } catch (err) {

        obj = {
            'key': childSnapshot.id,
            'owner_fio': 'Ошибка в данных',
            'phone': '',
            'message': childSnapshot.id,
            'close_timestamp': '',
            'open_timestamp': '',
            'accept': '',
            'reciever_id': '',
            'owner_email': '',
            'owner_photo': 'https://firebasestorage.googleapis.com/v0/b/fleet-management-8dfc9.appspot.com/o/placeholder-man.png?alt=media&token=04920483-9eb3-4f69-8637-0543d75e5aec'
        };
    }
    return obj;
}

module.exports.gettexservice = async function gettexservice(db, id) {
    const snapshot = await db.collection('texservice').doc(id).get();
    return snapshot;
}
module.exports.settexserviceReturnAccept = async function settexserviceReturnAccept(db, body) {
    // let is_performer = body.is_performer === 'on' ? true : false;
    let accept = body.accept === 'on' ? true : false;

    await db.collection('texservice').doc(body.email).update(
        'accept', accept);
}

module.exports.deletetexservice = async function deletetexservice(db, id) {
    await db.collection('texservice').doc(id).delete();
}

module.exports.getDataFromtexservicesCollection = function getDataFromtexservicesCollection(emails) {

    const lst = [];

    emails.forEach(function(childSnapshot) {

        let obj = getObjectFromtexserviceSnapshot(childSnapshot);

        lst.push(obj);

    });

    return lst;
}

    module.exports.getObjectFromtexserviceSnapshot = getObjectFromtexserviceSnapshot;
    module.exports.gettexservices = gettexservices;