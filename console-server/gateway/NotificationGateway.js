const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');

const Gateway = require("./../../GenericDataHelper/GatewayBase");
class NotificationGateway extends Gateway {
    constructor() { super() }

    updateFcmToken(conn) {
        Gateway.authentication(conn, () => {
            const token = conn.body.token;

            modules.notifier.update_fcm_id(token);

            conn.send("OK");

            modules.log.verbose(LOG_SUBJECT, `FCM 토큰 업데이트 : ${token}`, conn.ip);
        });
    }

    sendFcm(conn) {
        Gateway.authentication(conn, () => {
            const title = conn.body.title;
            const body = conn.body.body;

            modules.notifier.sendFcm(title, body, {
                success: function () {
                    conn.send("OK");
                    modules.log.verbose(LOG_SUBJECT, `FCM 발송 : ${body}`, conn.ip);
                },
                error: function (msg) {
                    conn.internalError();
                    modules.log.error(LOG_SUBJECT, `FCM 발송 실패 : ${msg}`, conn.ip);
                }
            })
        });
    }

    sendMail(conn) {
        Gateway.authentication(conn, () => {
            const title = conn.body.title;
            const body = conn.body.body;

            modules.notifier.sendEmail(title, body);
            conn.send("OK");

            modules.log.verbose(LOG_SUBJECT, `메일 발송 : ${body}`, conn.ip);
        });
    }

    get(conn) {
        Gateway.authentication(conn, () => {
            const query = `SELECT * FROM notification ORDER BY idx DESC LIMIT 100`;
            Gateway.query(query, conn, (result) => {
                conn.send(result);
            })
        });
    }
}

module.exports = new NotificationGateway();