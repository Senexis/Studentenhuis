let crypto = require('crypto');
let bcrypt = require('bcrypt');
let tokenModule = require('jsonwebtoken');
const ALGORITHM = 'aes-256-cbc'
const IV_LENGTH = 16;
const PASSWORD = process.env.ENCRYPT_PASS || 'devPassPass';
let iv = "";

module.exports = {
    encrypt: function (text, cb) {
        for (let i = 0; i < IV_LENGTH; i++) {
            let charCode = Math.floor(Math.random() * (127 - 33)) + 33;
            if (charCode == 46) {
                i--;
            } else {
                iv += String.fromCharCode(charCode);
            }
        }

        let cipher = crypto.createCipheriv(ALGORITHM, PASSWORD, iv);
        let crypted = cipher.update(text, 'utf8', 'hex');
        crypted += cipher.final('hex');
        //Append "hidden-in-plain-sight" iv to keep compatibility through server restarts
        crypted += "." + this.getCharCodesFromIv(iv);
        console.log(crypted);
        cb(crypted);
    },

    decrypt: function (text, cb) {
        if (text == null || text == undefined) {
            return null;
        }
        //Take the IV from the hash and use it to decrypt;
        let split = text.split(".");
        let ivBuf = getIvFromCharCodes(split[1]);
        let decipher = crypto.createDecipheriv(ALGORITHM, PASSWORD, ivBuf);
        let clear = decipher.update(split[0], 'hex', 'utf8');
        clear += decipher.final('utf8');
        cb(clear);
    },

    getCharCodesFromIv: function (iv) {
        let charCodes = '';

        for (let i = 0; i < iv.length; i++) {
            charCodes += this.intToHex(iv.charCodeAt(i));
        }

        return charCodes;
    },

    intToHex: function (int) {
        int = Math.round(int);
        return int.toString(16);
    },

    hash: function (text, cb) {
        bcrypt.genSalt(10, function (err, salt) {
            if (err)
                cb(null, err);

            bcrypt.hash(text, salt, function (err, hash) {
                if (err)
                    cb(null, err);

                cb(hash, null);
            });
        });
    },

    verifyHash: function (text, hash, cb) {
        bcrypt.compare(text, hash, function (err, bool) {
            if (err)
                cb(err, null);

            cb(null, bool);
        });
    },

    getPayload: function (token) {
        return tokenModule.decode(token);
    }
}