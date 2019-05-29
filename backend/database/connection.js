var mysql = require('mysql');
var config = require('./config');


/* 
    Gebruik van de module node-mysql, een wrapper
    Zorgt ervoor dat de connectie niet constant open-staat.
*/
var connection = function(cb) {
    cb(null, mysql.createConnection(process.env.JAWSDB_URL || "mysql://root:root@localhost:3306/mydb"));
}

exports.connection = connection;