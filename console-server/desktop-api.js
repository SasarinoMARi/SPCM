/*
 * 데스크탑 제어 api
 */

require("dotenv").config();
const request = require('request');
baseUrl = `${process.env.REMOTE_COMPUTER}/`;
const log = require("./logger");
const log_header = "desktop-api.js";

class remoteServer {
    establishment(request_ip, callback) {
        const key = require("../common/secret").cr_key
        const options = {
            uri: baseUrl + "establishment",
            headers: {
              'key': key
            }
        };

        request.get(options, function (error, response, body) {
            if (error) {
                log.info(log_header, `[establishment] failed : ${error}`, request_ip);
            }
            else {
                const statusCode = response && response.statusCode;

                if (statusCode == 200) {
                    let json = JSON.parse(body);
                    callback.success(json.token);
                }
                else {
                    log.error(log_header, `[establishment] status code ${statusCode} returned.`, request_ip);
                    callback.error();
                }
            }
        });
    };

    async lookup(request_ip) {
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
            // 꺼져있을 경우 당연히 실패함
            // log.error(log_header, `[lookup] failed : ${err}`, request_ip);
        }

        return result;
    }

    /**
     * establishment 후 api 실행하는 함수
     * @param {string} path : api endpoint
     * @param {string} headerPair : 헤더 쌍
     * @param {function} callback : 콜백 함수
     */
    __generalCall(request_ip, path, headerPair, callback) {
        this.establishment(request_ip, {success : function(token) {
            const options = {
                uri: baseUrl + path,
                headers: {
                  'token': token
                }
            };
            Object.assign(options.headers, headerPair);
    
            request.get(options, function (error, response, body) {
                if (error) {
                    log.info(log_header, `[${path}] failed : ${error}`, request_ip);
                }
                else {
                    const statusCode = response && response.statusCode;
    
                    if (statusCode == 200) {
                        if(body == "OK") callback?.success("OK");
                        else callback?.error(body);
                    }
                    else {
                        log.error(log_header, `[${path}] status code ${statusCode} returned.`, request_ip);
                        callback?.error(body);
                    }
                }
            });
        }, error: function () {
            console.log(error);
            //callback.error();
        }});
    }

    reboot(request_ip) { this.__generalCall(request_ip, "power/reboot"); }
    shutdown(request_ip) { this.__generalCall(request_ip, "power/shutdown"); }
    startFileServer(request_ip) { this.__generalCall(request_ip, "file_server/start"); }
    stopFileServer(request_ip) { this.__generalCall(request_ip, "file_server/stop"); }
    startRdpServer(request_ip) { this.__generalCall(request_ip, "rdp_server/start"); }
    volume(request_ip, amount) { this.__generalCall(request_ip, "media/volume", {"amount": amount} ); }
    mute(request_ip, option) { this.__generalCall(request_ip, "media/mute", {"option": option} ); }
    play(request_ip, src) { if(!src) return; this.__generalCall(request_ip, "media/play", {"src": src}); }
};

module.exports = new remoteServer();