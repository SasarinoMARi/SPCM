/*
 * 목적지 서버 API 정의 파일
 */

const request = require('request');
baseUrl = "http://sasarinomar1.iptime.org:8080/";
const logger = require("../common/logger")

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
        const options = {
            uri: baseUrl + "lookup",
            timeout: 1000 * 5
        };

        let request = require("./await-request")
        
        var result = null;
        try {
            result = await request(options);
        }
        catch (err) {
            logger.e(err);
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
                        //callback.success(json.token);
                    }
                    else {
                        //callback.error();
                    }
                }
            });
        }, failed: function () {
            //callback.error();
        }});
    }

    sleep() {
        this.__generalCall("sleep");
    }
    reboot() {
        this.__generalCall("reboot");
    }
    shutdown() {
        this.__generalCall("shutdown");
    }
    do() {
        this.__generalCall("do");
    }
    logs() {
        this.__generalCall("logs");
    }
    start_fs(calback) {
        this.__generalCall("start-fs");
    }
    stop_fs() {
        this.__generalCall("stop-fs");
    }
};

module.exports = new remoteServer();