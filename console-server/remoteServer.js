
const request = require('request');
const logger = require("./../common/logger")
baseUrl = "http://sasarinomar1.iptime.org:8080/";

class remoteServer {
    establishment(callback) {
        logger.v("api : establishment()");

        const key = require("../common/sha256").SHA256(require("../common/secret").key)
        const options = {
            uri: baseUrl + "establishment",
            headers: {
              'key': key
            }
        };

        request.get(options, function (error, response, body) {
            if (error) {
                logger.e("error : " + error);
            }
            else {
                const statusCode = response && response.statusCode;
                logger.v("statusCode : " + statusCode);

                if (statusCode == 200) {
                    let json = JSON.parse(body);
                    callback.success(json.token);
                }
                else {
                    callback.error();
                }
            }
        });
    };

    async lookup(callback) {
        logger.d("api : lookup()");

        const options = {
            uri: baseUrl + "lookup",
            timeout: 1000 * 5
        };

        let request = require("./await-request")
        
        var result = null;
        try {
            result = await request(options);
            console.log(result);
        }
        catch (err) {
            console.error(err);
        }

        return result;

        /*
        request.get(options), function (error, response, body) {
            if (error) {
                logger.e("error : " + error);
            }
            else {
                const statusCode = response && response.statusCode;
                logger.d("statusCode : " + statusCode);

                if (statusCode == 200) {
                    let json = JSON.parse(body);
                    logger.d("json result : " + json);
                    callback.success(json);
                }
                else {
                    callback.error();
                }
            }
        });
        */
    }

    __generalCall(path, callback) {
        this.establishment({success : function(token) {
            logger.d("api : " + path);

            const options = {
                uri: baseUrl + path,
                headers: {
                  'token': token
                }
            };
    
            request.get(options, function (error, response, body) {
                if (error) {
                    logger.e("error : " + error);
                }
                else {
                    const statusCode = response && response.statusCode;
                    logger.v("statusCode : " + statusCode);
    
                    if (statusCode == 200) {
                        let json = JSON.parse(body);
                        callback.success(json.token);
                    }
                    else {
                        callback.error();
                    }
                }
            });
        }, failed: function () {
            callback.error();
        }});
    }

    sleep(callback) {
        this.__generalCall("sleep", callback);
    }
    reboot(callback) {
        this.__generalCall("reboot", callback);
    }
    shutdown(callback) {
        this.__generalCall("shutdown", callback);
    }
    do(callback) {
        this.__generalCall("do", callback);
    }
    logs(callback) {
        this.__generalCall("logs", callback);
    }
};

module.exports = new remoteServer();