const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');

const Gateway = require("./GatewayBase");
class SystemGateway extends Gateway {
    lookup_ips = []; // lookup을 요청한 사용자의 ip 수집

    constructor() { super() }

    default(conn) {
        conn.response.render("wtf.ejs");
        modules.log.critical(LOG_SUBJECT, `잘못된 경로로 접근 요청됨 : ${conn.request.originalUrl}`, conn.ip);
    }

    establishment(conn) {
        if (modules.secret.check(conn.headers.key)) {
            var token = modules.token_manager.new();
            conn.send(token);
        } else {
            modules.log.critical(LOG_SUBJECT, `잘못된 키로 인증 시도됨`, conn.ip);
            conn.unauthorize();
        }
    }

    async lookup(conn) {
        if (!this.lookup_ips.includes(conn.ip)) {
            modules.log.verbose(LOG_SUBJECT, `새로운 아이피로부터 lookup 요청됨`, conn.ip);
            this.lookup_ips.push(conn.ip);
        }

        let result = { server: { status: 1, temp: modules.temperature.getTemp() }, pc: { status: 0 } };

        let lookup_result = await modules.desktop.lookup();
        if (lookup_result) {
            let json = JSON.parse(lookup_result);
            result.pc.status = json.status;
            result.pc.temp = json.temp;
        }

        conn.send(result);
    }

    reboot(conn) {
        modules.log.verbose(LOG_SUBJECT, `서버 재부팅 요청됨`, conn.ip);
        Gateway.authentication(conn, () => {
            conn.send("OK");
            modules.shell.exec('sudo reboot');
        });
    }

    getLogs(conn) {
        Gateway.authentication(conn, () => {
            const level = conn.headers.level ? conn.headers.level : 0;
            const page = conn.headers.page ? conn.headers.page : 0;
            const rpp = 50;
            const offect = rpp * page;

            const query = `SELECT * from \`log\` where \`level\`>=${level} ORDER BY idx desc LIMIT ${rpp} OFFSET ${offect}`;
            Gateway.query(query, conn, (result) => { conn.send(result); });
        });
    }

    writeLog(conn) {
        Gateway.authentication(conn, () => {
            const level = conn.body.level;
            const subject = conn.body.subject;
            const content = conn.body.content;

            if (!level || !subject || !content) {
                conn.unauthorize();
                return;
            }
            
            modules.log.log(level, subject, content, conn.ip);
            conn.send("OK");
        });
    }
    header_image(conn) {
        Gateway.authentication(conn, () => {
            const query = `SELECT * FROM header_image ORDER BY RAND() LIMIT 1`;
            Gateway.query(query, conn, (result) => { conn.send(result); });
        });
    }
}

module.exports = new SystemGateway();