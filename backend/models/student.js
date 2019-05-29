var db = require('../Helper/database');

module.exports = {
    getStudents: function (callback) {
        let query = "SELECT * FROM `Student`";

        db.executeQuery(query, callback);
    },

    getStudentById: function (id, callback) {
        let query = "SELECT * FROM `Student` WHERE `idStudenten` = ?";

        db.executeQueryParameterized(query, id, callback);
    },

    addStudent: function (student, callback) {
        let query = "INSERT INTO `student` (`naamStudent`, `email`, `wachtwoord`) VALUES (?, ?, ?);";

        db.executeQueryParameterized(query, student, callback);
    },

    updateStudent: function (id, student, callback) {
        let query = "UPDATE `Student` SET ? WHERE `Student`.`idStudenten` = ?";

        db.executeQueryParameterized(query, [student, id], callback);
    },

    deleteStudent: function (id, callback) {
        let query = "DELETE FROM `Student` WHERE `idStudenten` = ?";

        db.executeQueryParameterized(query, id, callback);
    },
}