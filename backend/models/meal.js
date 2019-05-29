var db = require('../Helper/database');

module.exports = {
    getMeals: function (callback) {
        let query = "SELECT * FROM `Maaltijd` LEFT JOIN `student` ON maaltijd.idKok = student.idStudenten";

        db.executeQuery(query, callback);
    },

    getMealById: function (id, callback) {
        let query = "SELECT * FROM `Maaltijd` LEFT JOIN `student` ON maaltijd.idKok = student.idStudenten WHERE maaltijd.idMaaltijd = ?";

        db.executeQueryParameterized(query, id, callback);
    },

    addMeal: function (meal, callback) {
        let query = "INSERT INTO `maaltijd` (`idKok`, `naamMaaltijd`, `maaltijdAfbeelding`, `maxEters`, `maaltijdBeginTijd`, `kosten`, `beschrijving`) VALUES (?, ?, ?, ?, ?, ?, ?)";

        db.executeQueryParameterized(query, meal, callback);
    },

    updateMeal: function (id, meal, callback) {
        let query = "UPDATE `Maaltijd` SET `idKok` = ?, `naamMaaltijd` = ?, `maaltijdAfbeelding` = ?, `maxEters` = ?, `maaltijdBeginTijd` = ?, `kosten` = ?, `beschrijving` = ? WHERE `maaltijd`.`idMaaltijd` = " + id;

        db.executeQueryParameterized(query, meal, callback);
    },

    deleteMeal: function (id, callback) {
        let query = "DELETE FROM `Maaltijd` WHERE `idMaaltijd` = ?";

        db.executeQueryParameterized(query, id, callback);
    },

    getParticipants: function (id, callback) {
        let query = "SELECT * FROM `studentmaaltijd` WHERE `idMaaltijd` = ?";

        db.executeQueryParameterized(query, id, callback);
    },

    getParticipantsCount: function (id, callback) {
        let query = "SELECT SUM(`aantalMeeEters` + 1) AS `totaalAantalMeeEters` FROM `studentmaaltijd` WHERE `idMaaltijd` = ?";

        db.executeQueryParameterized(query, id, callback);
    },

    getMaxParticipantsCount: function (id, callback) {
        let query = "SELECT `maxEters` FROM `Maaltijd` WHERE `idMaaltijd` = ?";

        db.executeQueryParameterized(query, id, callback);
    },

    addStudent: function (id, student, callback) {
        let query = "INSERT INTO `studentmaaltijd` (`idStudent`, `idMaaltijd`, `aantalMeeEters`) VALUES (?, " + id + ", ?);";

        db.executeQueryParameterized(query, student, callback);
    }
}
