const LOG_SUBJECT = require('path').basename(__filename);
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
                log.error(LOG_SUBJECT, `error fetching blacklist: ${err.sqlMessage}`);
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
        const addr = ip.replace('\'', '\\\'');
        const date = time().format("YYYY-MM-DD HH:mm:ss");
        var query = `INSERT INTO \`blacklist\` (last_connected, \`address\`) \
            VALUES ('${date}','${addr}')
            ON DUPLICATE KEY UPDATE last_connected='${date}', \`address\`='${addr}'`;

        sql.query(query, function(err, results, fields) {
            if (err) {
                // DUPLICATE 에러
                if (err.errno == 1062) 
                    return;

                console.log(err);
                return;
            }

            log.critical(LOG_SUBJECT, "사용자가 블랙리스트에 추가됨", addr);
        });
    }
}

module.exports = new AutoBlock();