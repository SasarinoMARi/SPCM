const sql = require('./../sql.js');
module.exports = {
    random: function(callback, errorCallback) {
        sql.query(
            `SELECT * FROM food_list ORDER BY RAND() LIMIT 1`,
            function(error, results, fields) {
                if(error) {
                    errorCallback(error);
                    return;
                }
                if(results.length>0) callback(results[0]);
                else callback(null)
            }
        )
    }
}