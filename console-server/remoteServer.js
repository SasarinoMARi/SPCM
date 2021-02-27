function log(str) {
    console.log(`[${Date()}] ${str}`)
}

class remoteServer {
    request = require('request');
    baseUrl = "http://sasarinomar1.iptime.org:8080/";


    establishment(callback) {
        log("api : establishment()");

        const key = require("./../sha256").SHA256(require("./../secret").key)
        const options = {
            uri: this.baseUrl + "establishment",
            qs: {
                key: key
            }
        };

        this.request.get(options, function (error, response, body) {
            if (error) {
                log("error : " + error);
            }
            else {
                const statusCode = response && response.statusCode;
                log("statusCode : " + statusCode);

                if (statusCode == 200) {
                    callback.success();
                }
                else {
                    callback.error();
                }
            }
        });
    };
};

var rs = new remoteServer();
rs.establishment({
    success: function () { 
        console.log("success!"); 
    }, error: function () {
        console.log("error!");
    }
});

module.exports = new remoteServer();