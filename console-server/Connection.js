const blacklist = require('./AutoBlock');

class Connection {
    request; resposne; ip; token;
    headers; body;

    constructor(request, response) {
        this.request = request;
        this.response = response;
        this.ip = this.getIpAddress();
        this.token = request.headers.token;
        this.headers = request.headers;
        this.body = request.body;
    }

    getIpAddress() {
        var ip = this.request.connection.remoteAddress;
        ip.replace("::ffff:192.168.0.", "localhost ");
        if (ip.startsWith("::ffff:")) ip = ip.slice(7);
        return ip;
    }

    send(object) {
        this.response.json(object);
    }

    internalError() {
        this.response.statusCode = 500;
        this.response.send("INTERNAL ERROR");
    }

    unauthorize() {
        this.response.statusCode = 403;
        this.response.send("UNAUTHORIZED");

        // 인증 실패시 블랙리스트 등록할 경우
        // 앱 오래 켜놓아서 토큰 만료되는 경우에 밴 당함..
        if (false) {
            // 블랙리스트 처리
            var ip = this.getIpAddress();
            blacklist.addIntoBlacklist(ip);
        }
    }
}

module.exports = Connection;