
let shinasLocalCollection = []
let shinasLocalCollectionMode;
let start = null      // start position of listener
const rowsInPage = 3;


async function getshinas(db, accept, next) {
    if(next && !start)
        next = null;

    if(shinasLocalCollectionMode != accept) {
        shinasLocalCollection = [];
        start = null;
        next = null;
    }

    shinasLocalCollectionMode = accept;
    let query = db.collection('shina').where('accept', '==', accept).orderBy('open_timestamp');
    let index = 0;
    if(next) {
        query = query.startAt(start);
    }
     else {
        shinasLocalCollection = [];
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
            let obj = getObjectFromshinaSnapshot(childSnapshot);
            shinasLocalCollection.push(obj);
        }
        index++;
    });

    return shinasLocalCollection;
    // return snapshots;
}

function getObjectFromshinaSnapshot(childSnapshot) {
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

module.exports.getshina = async function getshina(db, id) {
    const snapshot = await db.collection('shina').doc(id).get();
    return snapshot;
}
module.exports.setshinaReturnAccept = async function setshinaReturnAccept(db, body) {
    // let is_performer = body.is_performer === 'on' ? true : false;
    let accept = body.accept === 'on' ? true : false;

    await db.collection('shina').doc(body.email).update(
        'accept', accept);
}

module.exports.deleteshina = async function deleteshina(db, id) {
    await db.collection('shina').doc(id).delete();
}

module.exports.getDataFromshinasCollection = function getDataFromshinasCollection(emails) {

    const lst = [];

    emails.forEach(function(childSnapshot) {

        let obj = getObjectFromshinaSnapshot(childSnapshot);

        lst.push(obj);

    });

    return lst;
}

    module.exports.getObjectFromshinaSnapshot = getObjectFromshinaSnapshot;
    module.exports.getshinas = getshinas;