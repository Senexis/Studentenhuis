var students = require('../models/student');

module.exports = {
    getStudents(req, res, next) {
        console.log('Student getAll');

        students.getStudents(function (err, result) {
            if (err) {
                next(err);
            } else {
                if (result.length < 1) {
                    res.sendStatus(404);
                } else {
                    res.status(200).json({
                        status: 'OK',
                        result: result
                    }).end();
                }
            }
        });
    },

    getStudentById(req, res, next) {
        console.log('Student getStudentById');

        students.getStudentById(req.params.id, function (err, result) {
            if (err) {
                next(err);
            } else {
                if (result.length < 1) {
                    res.sendStatus(404);
                } else {
                    res.status(200).json({
                        status: 'OK',
                        result: result[0]
                    }).end();
                }
            }
        });
    },

    addStudent(req, res, next) {
        console.log('Student addStudent');

        if (!req.body.naamStudent || !req.body.email || !req.body.wachtwoord) {
            res.sendStatus(400);
        } else {
            students.addStudent([req.body.naamStudent, req.body.email, req.body.wachtwoord], function (err, result) {
                if (err) {
                    next(err);
                } else {
                    res.status(200).json({
                        status: 'OK',
                        result: result
                    }).end();
                }
            });
        }
    },

    updateStudent(req, res, next) {
        console.log('Student updateStudent');

        if (!req.body.naamStudent || !req.body.email || !req.body.wachtwoord) {
            res.sendStatus(400);
        } else {
            students.updateStudent(req.params.id, [req.body.naamStudent, req.body.email, req.body.wachtwoord], function (err, result) {
                if (err) {
                    next(err);
                } else {
                    res.status(200).json({
                        status: 'OK',
                        result: result
                    }).end();
                }
            });
        }
    },
    
    deleteStudent(req, res, next) {
        console.log('Student deleteStudent');

        students.deleteStudent(req.params.id, function (err, result) {
            if (err) {
                next(err);
            } else {
                res.status(200).json({
                    status: 'OK',
                    result: result
                }).end();
            }
        });
    },
}