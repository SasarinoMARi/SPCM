const log_header = 'AutoBlock.js';
const log = require('./logger');
const sql = require('./database/sql');
const time = require('./time')

class AutoBlock {
    constructor() { }

    checkIsBlocked(ip, callback) {
        ip = ip.replace('\'', '\\\'');
        if (ip.startsWith("192.168.0.")) {
            callback.success();
            return;
        }

        sql.query(`SELECT * FROM blacklist where address='${ip}'`, function (err, results, fields) {
            if (err) {
                log.error(log_header, `error fetching blacklist: ${err.sqlMessage}`);
                return;
            }

            if (results.length > 0) {
                callback.banned();
            } else {
                callback.success();
            }
        });
    }

    addIntoBlacklist(ip) {
        ip = ip.replace('\'', '\\\'');

        var query = `INSERT INTO \`blacklist\` (created_at, \`address\`) \
            VALUES ('${time().format("YYYY-MM-DD HH:mm:ss")}','${ip}')`;

        sql.query(query, function(err, results, fields) {
            if (err) {
                if (err.errno==1062) 
                    return;

                console.log(err);
            }
        });
    }
}

module.exports = new AutoBlock();