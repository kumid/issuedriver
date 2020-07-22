
let feedbacksLocalCollection = []
let feedbacksLocalCollectionMode = true;
let listeners = []    // list of listeners
let start = null      // start position of listener
let end = null        // end position of listener
const rowsInPage = 3;


module.exports.getfeedbacks = async function getfeedbacks(db, accept, next) {
    if(feedbacksLocalCollectionMode != accept) {
        feedbacksLocalCollection = [];
        start = null;
    }

    feedbacksLocalCollectionMode = accept;
    let query = db.collection('feedbacks').where('accept', '==', accept);
    let index = 0;
    if(next) {
        query = query.startAt(start);
    } else {
        feedbacksLocalCollection = [];
        index = 1;
    }


    const snapshots = await query.limit(rowsInPage).get();
    // // save startAt snapshot
    let newPagesEnd = snapshots.docs[snapshots.docs.length - 1]
    if(newPagesEnd == start)
        return null;

    start = newPagesEnd;
    snapshots.forEach(function(childSnapshot) {
        if(index != 0) {
            let obj = getObjectFromFeedbackSnapshot(childSnapshot);
            feedbacksLocalCollection.push(obj);
        }
        index++;
    });

    return feedbacksLocalCollection;
    // return snapshots;
}

function getObjectFromFeedbackSnapshot(childSnapshot) {
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

module.exports.getfeedback = async function getfeedback(db, id) {
    const snapshot = await db.collection('feedbacks').doc(id).get();
    return snapshot;
}
module.exports.setfeedbackReturnAccept = async function setfeedbackReturnAccept(db, body) {
    // let is_performer = body.is_performer === 'on' ? true : false;
    let accept = body.accept === 'on' ? true : false;

    await db.collection('feedbacks').doc(body.email).update(
        'close_timestamp', item.close_timestamp,
        'accept', accept);
}

module.exports.deletefeedback = async function deletefeedback(db, id) {
    await db.collection('feedbacks').doc(id).delete();
}

module.exports.getObjectFromfeedbackSnapshot = getObjectFromFeedbackSnapshot;

module.exports.getDataFromfeedbacksCollection = function getDataFromfeedbacksCollection(emails) {

    const lst = [];

    emails.forEach(function(childSnapshot) {

        let obj = getObjectFromfeedbackSnapshot(childSnapshot);

        lst.push(obj);

    });

    return lst;
}
