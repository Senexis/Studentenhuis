/* Voor lokaal testen, updaten met lokale database gegevens 
module.exports = {
    "host": 'localhost',
    "database": 'mydb',
    "user": 'root',
    "password": '',
    "dbport": 3306,
    "port": process.env.PORT || 3000,
};
*/
/* Modules voor heroku */
module.exports = {
    "host": process.env.JAWSDB_URL || 'mysql://root:root@localhost:3306/mydb'
};