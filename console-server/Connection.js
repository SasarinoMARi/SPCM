class Connection {
    request; resposne; ip; token;
    headers; body;

    constructor(request, response) {
        this.request = request;
        this.response = response;
        this.ip = this.#getIpAddress(request);
        this.token = request.headers.token;
        this.headers = request.headers;
        this.body = request.body;
    }

    #getIpAddress(req) {
        var ip = req.connection.remoteAddress;
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
    }
}

module.exports = Connection;