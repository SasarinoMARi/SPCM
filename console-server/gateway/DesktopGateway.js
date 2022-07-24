const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');

const Gateway = require("./../../GenericDataHelper/GatewayBase");
class DesktopGateway extends Gateway {
    constructor() { super() }

    wakeup(conn) {
        modules.log.verbose(LOG_SUBJECT, `데스크탑 부팅 요청됨`, conn.ip);
        Gateway.authentication(conn, () => {
            modules.iptime.wakeup(conn.ip);
            conn.send("OK");
        });
    }

    shutdown(conn) {
        modules.log.verbose(LOG_SUBJECT, `데스크탑 종료 요청됨`, conn.ip);
        Gateway.authentication(conn, () => {
            modules.desktop.shutdown(conn.ip);
            conn.send("OK");
        });
    }

    // Deprecated
    sleep(conn) {
        modules.log.verbose(LOG_SUBJECT, `데스크탑 절전 요청됨`, conn.ip);
        Gateway.authentication(conn, () => {
            modules.desktop.sleep(conn.ip);
            conn.send("OK");
        });
    }

    // Deprecated
    reboot(conn) {
        modules.log.verbose(LOG_SUBJECT, `데스크탑 재시작 요청됨`, conn.ip);
        Gateway.authentication(conn, () => {
            modules.desktop.reboot(conn.ip);
            conn.send("OK");
        });
    }

    startFileServer(conn) {
        modules.log.verbose(LOG_SUBJECT, `파일 서버 시작 요청됨`, conn.ip);
        Gateway.authentication(conn, () => {
            modules.desktop.startFileServer(conn.ip);
            conn.send("OK");
        });
    }

    startTeamviewerServer(conn) {
        modules.log.verbose(LOG_SUBJECT, `팀뷰어 서버 시작 요청됨`, conn.ip);
        Gateway.authentication(conn, () => {
            modules.desktop.startRdpServer(conn.ip);
            conn.send("OK");
        });
    }

    setVolume(conn) {
        Gateway.authentication(conn, () => {
            const volume = conn.headers.amount;
            if (!volume) {
                conn.internalError();
                return;
            };
            modules.desktop.volume(conn.ip, volume);
            conn.send("OK");
        });
    }

    mute(conn) {
        Gateway.authentication(conn, () => {
            const option = conn.headers.option;
            if (!option) option = 2;
            modules.desktop.mute(conn.ip, option);
            conn.send("OK");
        });
    }

    play(conn) {
        modules.log.verbose(LOG_SUBJECT, `데스크탑으로 링크 실행이 요청되었습니다.`, conn.ip);
        Gateway.authentication(conn, () => {
            const url = conn.headers.src;
            if (!url) {
                conn.internalError();
                return;
            };
            modules.desktop.play(conn.ip, url);
            conn.send("OK");
        });
    }
}

module.exports = new DesktopGateway();