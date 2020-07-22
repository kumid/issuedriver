
let usersLocalCollection = []
let usersLocalCollectionMode = true;
let listeners = []    // list of listeners
let start = null      // start position of listener
let end = null        // end position of listener
const rowsInPage = 3;


module.exports.getUsers = async function getUsers(db, accept, next) {
    if(usersLocalCollectionMode != accept) {
        usersLocalCollection = [];
        start = null;
    }

    usersLocalCollectionMode = accept;
    let query = db.collection('users').where('accept', '==', accept);
    let index = 0;
    if(next) {
        query = query.startAt(start);
    } else {
        usersLocalCollection = [];
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
            let obj = getObjectFromUserSnapshot2(childSnapshot);
            usersLocalCollection.push(obj);
        }
        index++;
    });

    return usersLocalCollection;
    // return snapshots;
}

function getObjectFromUserSnapshot2(childSnapshot) {
    let obj;
    try {
        var item = childSnapshot.data();

        if (item.photoPath.length == 0)
            item.photoPath = 'https://firebasestorage.googleapis.com/v0/b/fleet-management-8dfc9.appspot.com/o/placeholder-man.png?alt=media&token=04920483-9eb3-4f69-8637-0543d75e5aec';

        let accepted = item.accept ? 'ДА' : 'НЕТ';
        let isperformer = item.is_performer ? 'ДА' : 'НЕТ';

        obj = {
            'key': childSnapshot.id,
            'fio': item.fio,
            'staff': item.staff,
            'email': childSnapshot.id,
            'corp': item.corp,
            'tel': item.tel,
            'accept': accepted,
            'is_performer': isperformer,
            'UUID': item.UUID,
            'photoPath': item.photoPath
        };

    } catch (err) {

        obj = {
            'key': childSnapshot.id,
            'fio': 'Ошибка в данных',
            'staff': '',
            'email': childSnapshot.id,
            'corp': '',
            'tel': '',
            'accept': '',
            'is_performer': '',
            'UUID': '',
            'photoPath': 'https://firebasestorage.googleapis.com/v0/b/fleet-management-8dfc9.appspot.com/o/placeholder-man.png?alt=media&token=04920483-9eb3-4f69-8637-0543d75e5aec'
        };
    }
    return obj;
}

module.exports.getUser = async function getUser(db, id) {
    const snapshot = await db.collection('users').doc(id).get();
    return snapshot;
}
module.exports.setUserReturnAccept = async function setUserReturnAccept(db, body) {
    let is_performer = body.is_performer === 'on' ? true : false;
    let accept = body.accept === 'on' ? true : false;

    await db.collection('users').doc(body.email).update(
        'fio', body.fio,
        'staff', body.staff,
        'corp', body.corp,
        'is_performer', is_performer,
        'tel', body.tel,
        'accept', accept);
}

module.exports.deleteUser = async function deleteUser(db, id) {
    await db.collection('users').doc(id).delete();
}

module.exports.getObjectFromUserSnapshot = getObjectFromUserSnapshot2;
/*
function getObjectFromUserSnapshot(childSnapshot) {
    let obj;
    try {
        var item = childSnapshot.data();

        if (item.photoPath.length == 0)
            item.photoPath = 'https://firebasestorage.googleapis.com/v0/b/fleet-management-8dfc9.appspot.com/o/placeholder-man.png?alt=media&token=04920483-9eb3-4f69-8637-0543d75e5aec';

        let accepted = item.accept ? 'ДА' : 'НЕТ';
        let isperformer = item.is_performer ? 'ДА' : 'НЕТ';

        obj = {
            'key': childSnapshot.id,
            'fio': item.fio,
            'staff': item.staff,
            'email': childSnapshot.id,
            'corp': item.corp,
            'tel': item.tel,
            'accept': accepted,
            'is_performer': isperformer,
            'UUID': item.UUID,
            'photoPath': item.photoPath
        };

    } catch (err) {

        obj = {
            'key': childSnapshot.id,
            'fio': 'Ошибка в данных',
            'staff': '',
            'email': childSnapshot.id,
            'corp': '',
            'tel': '',
            'accept': '',
            'is_performer': '',
            'UUID': '',
            'photoPath': 'https://firebasestorage.googleapis.com/v0/b/fleet-management-8dfc9.appspot.com/o/placeholder-man.png?alt=media&token=04920483-9eb3-4f69-8637-0543d75e5aec'
        };
    }
    return obj;
}
*/
module.exports.getDataFromUsersCollection = function getDataFromUsersCollection(emails) {

    const lst = [];

    emails.forEach(function(childSnapshot) {

        let obj = getObjectFromUserSnapshot(childSnapshot);

        lst.push(obj);

    });

    return lst;
}
