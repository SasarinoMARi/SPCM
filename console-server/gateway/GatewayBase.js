const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');
const blacklist = require('../AutoBlock');

class GatewayBase {
    static #sql = require('../database/sql.js');

    constructor() { }

    static authentication(conn, callback) {
        let token = conn.token;
        if (!modules.token_manager.contains(token)) {
            conn.unauthorize();
        } else {
            blacklist.checkIsBlocked(conn.getIpAddress(), {
                success: function() {
                    callback();
                },
                banned: function() {
                    conn.unauthorize();
                }
            });
        }
    }

    static query(query, connection, callback) {
        let safeQuery = query.replace(';', '');
        this.#sql.query(safeQuery, (error, result, fields) => {
            if (error) {
                const content = `SQL 에러가 발생했습니다.\n${error.sqlMessage}\n\n쿼리: ${query}`;
                modules.log.sqlError(LOG_SUBJECT, content);
                connection.internalError();
                return;
            }

            callback(result);
        });
    }
}

module.exports = GatewayBase;