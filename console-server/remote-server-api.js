/*
 * 목적지 서버 API 정의 파일
 */

require("dotenv").config();
const request = require('request');
baseUrl = `${process.env.REMOTE_COMPUTER}/`;
const logger = require("../common/logger")

class remoteServer {
    establishment(callback) {
        logger.v("api : establishment()");

        const key = require("../common/secret").cr_key
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
            timeout: 1000 * 2
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

    /**
     * establishment 후 api 실행하는 함수
     * @param {string} path : api endpoint
     * @param {string} headerPair : 헤더 쌍
     * @param {function} callback : 콜백 함수
     */
    __generalCall(path, headerPair, callback) {
        this.establishment({success : function(token) {
            logger.d("api : " + path);

            const options = {
                uri: baseUrl + path,
                headers: {
                  'token': token
                }
            };
            Object.assign(options.headers, headerPair);
    
            request.get(options, function (error, response, body) {
                if (error) {
                    logger.e("error : " + error);
                }
                else {
                    const statusCode = response && response.statusCode;
                    logger.v("statusCode : " + statusCode);
    
                    if (statusCode == 200) {
                        if(body == "OK") callback?.success("OK");
                        else callback?.error(body);
                    }
                    else {
                        callback?.error(body);
                    }
                }
            });
        }, error: function () {
            console.log(error);
            //callback.error();
        }});
    }

    reboot() { this.__generalCall("power/reboot"); }
    shutdown() { this.__generalCall("power/shutdown"); }
    startFileServer() { this.__generalCall("file_server/start"); }
    stopFileServer() { this.__generalCall("file_server/stop"); }
    startRdpServer() { this.__generalCall("rdp_server/start"); }
    volume(amount) { this.__generalCall("media/volume", {"amount": amount} ); }
    mute(option) { this.__generalCall("media/mute", {"option": option} ); }
    play(src) { this.__generalCall("media/play", {"src": src}); }
};

module.exports = new remoteServer();