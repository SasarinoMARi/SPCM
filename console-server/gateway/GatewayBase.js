const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');

class GatewayBase {
    static #sql = require('../database/sql.js');

    constructor() { }

    static authentication(conn, callback) {
        let token = conn.token;
        if (!modules.token_manager.contains(token)) {
            conn.unauthorize();
            return false;
        } else {
            callback();
            return true;
        }
    }

    static query(query, connection, callback) {
        this.#sql.query(query, (error, result, fields) => {
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