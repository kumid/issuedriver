const mongoose = require('mongoose');

const TokenSchema = new mongoose.Schema({
    id: {
        type: String,
        required: true
    },
    accessToken: {
        type: String,
        required: true
    }
});

module.exports = mongoose.model('Token', TokenSchema);