const redis = require('redis');
const bcrypt = require('bcrypt');
const db = redis.createClient();
//<-------- Создает долгосрочное подключение Redis.

class User {
    constructor(obj) {
        for (let key in obj) {
            // <-------- Перебирает свойства переданного объекта.
            this[key] = obj[key];
// <-------- Задает каждое свойство в текущем классе.
        }
    }

    save(cb) {
        if (this.id) { //<-------- Если идентификатор определен, то пользователь уже существует.
            this.update(cb);
        } else {
            db.incr('user:ids', (err, id) => { //<-------- Создает уникальный идентификатор.
                if (err) return cb(err);
                this.id = id; //<-------- Задает идентификатор для сохранения.
                this.hashPassword((err) => { //<-------- Хеширует пароль.
                    if (err) return cb(err);
                    this.update(cb);
                });
 //               Сохраняет свойства пользователей.
            });
        }
    }

    update(cb) {
        const id = this.id;
        db.set(`user:id:${this.name}`, id, (err) => {
            if (err) return cb(err);
            db.hmset(`user:${id}`,
                this, (err) => {
                    cb(err);
                });
        });
    }


    hashPassword(cb) {
        bcrypt.genSalt(12, (err, salt) => { //-<-------- Генерирует 12-символьную затравку.
            if (err) return cb(err);
            this.salt = salt; //<-------- Задает затравку для сохранения.
            bcrypt.hash(this.pass, salt, (err, hash) => { //<-------- Генерирует хеш.
                if (err) return cb(err);
                this.pass = hash; //<-------- Присваивает хеш для сохранения update().
                cb();
            });
        });
    }

}

module.exports = User;
// <-------- Экспортирует класс User.

//const User = require('./models/user');
const user = new User({ name: 'Example', pass: 'test' }); //<---- Создает нового пользователя.
 user.save((err) => { //<-------- Сохраняет пользователя.
     if (err) console.error(err);
     console.log('user id %d', user.id);
 });
