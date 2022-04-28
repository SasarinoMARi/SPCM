const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');

const Gateway = require("./GatewayBase");
class ScheduleGateway extends Gateway {
    constructor() { super() }

    reload(conn) {
        modules.log.verbose(LOG_SUBJECT, `스케줄 새로고침 요청됨`, conn.ip);
        Gateway.authentication(conn, () => {
            conn.send("OK");
            modules.scheduler.load();
        });
    }
    get(conn) {
        Gateway.authentication(conn, () => {
            const query = 'SELECT * FROM schedule';
            Gateway.query(query, conn, (result) => { conn.send(result); });
        });
    }

    set(conn) {
        modules.log.verbose(LOG_SUBJECT, `스케줄 재설정 요청됨`, conn.ip);
        Gateway.authentication(conn, () => {
            const id = conn.body.idx;
            var active = conn.body.active;

            if (!id) {
                conn.internalError();
                return;
            }
            if (!active) active = false;

            const query = `UPDATE schedule SET \`active\`=${active} WHERE idx=${id}`;
            Gateway.query(query, conn, (result) => { conn.send("OK"); });
        });
    }
}

module.exports = new ScheduleGateway();