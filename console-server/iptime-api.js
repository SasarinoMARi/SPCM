/*
 * IpTime 공유기 API 파일
 */

require("dotenv").config();
const request = require('request');
const log = require("../GenericDataHelper/Logger").instance();
const log_header = 'iptime-api.js';

let host = process.env.IPTIME_CONSOLE;
let baseUrl = `http://${host}/`;

function checkup(request_ip) {
    const options = {
        uri: baseUrl + "checkup",
        headers: {
            'Host' : host,
            'Connection': 'Keep-Alive',
            'User-Agent': 'Apache-HttpClient/UNAVAILABLE (java 1.4)'
        }
    };

    request.get(options, function (error, response, body) {
        if (error) {
            log.error(log_header, `[/checkup failed] ${error}`, request_ip);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                version(body);
            }
            else {
                log.error(log_header, `[/checkup failed] status code ${statusCode} returned.`, request_ip);
            }
        }
    });
}

function version(request_ip) {
    const options = {
        uri: baseUrl + "version",
        headers: {
            'Referer': baseUrl,
            'Host' : host,
            'Connection': 'Keep-Alive',
            'User-Agent': 'Apache-HttpClient/UNAVAILABLE (java 1.4)'
        }
    };

    request.get(options, function (error, response, body) {
        if (error) {
            log.error(log_header, `[/version failed] ${error}`, request_ip);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                hostinfo(body);
            }
            else {
                log.error(log_header, `[/version failed] status code ${statusCode} returned.`, request_ip);
            }
        }
    });
}

function hostinfo(request_ip) {
    const options = {
        uri: baseUrl + "login/hostinfo.cgi",
        headers: {
            'Referer': baseUrl,
            'Host' : host,
            'Connection': 'Keep-Alive',
            'User-Agent': 'Apache-HttpClient/UNAVAILABLE (java 1.4)'
        },
        qs: {
            'act':'auth'
        }
    };

    request.get(options, function (error, response, body) {
        if (error) {
            log.error(log_header, `[/hostinfo failed] ${error}`, request_ip);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                login_handler(body);
            }
            else {
                log.error(log_header, `[/hostinfo failed] status code ${statusCode} returned.`, request_ip);
            }
        }
    });
}

function login_handler(request_ip) {
    const postData = {
        'username': 'MARi',
        'passwd': 'dntkQyd132!',
        'act': 'session_id'
    };

    const options = {
        uri: baseUrl + "sess-bin/login_handler.cgi",
        headers: {
            'Referer': baseUrl,
            //'Content-Length': Buffer.byteLength(postData),
            //'Content-Type': 'application/x-www-form-urlencoded',
            'Host' : host,
            'Connection': 'Keep-Alive',
            'User-Agent': 'Apache-HttpClient/UNAVAILABLE (java 1.4)'
        },
        form: postData,
        json: false
    };

    request.post(options, function (error, response, body) {
        if (error) {
            log.error(log_header, `[/login_handler failed] ${error}`, request_ip);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                info(request_ip, body);
            }
            else {
                log.error(log_header, `[/login_handler failed] status code ${statusCode} returned.`, request_ip);
            }
        }
    });
}

function info(request_ip, session) {
    const options = {
        uri: baseUrl + "sess-bin/info.cgi",
        headers: {
            'Cookie': `efm_session_id=${session}`,
            'Referer': baseUrl,
            'Host' : host,
            'Connection': 'Keep-Alive',
            'User-Agent': 'Apache-HttpClient/UNAVAILABLE (java 1.4)'
        },
        qs: {
            'act':'wol_list'
        }
    };

    request.get(options, function (error, response, body) {
        if (error) {
            log.error(log_header, `[/info failed] ${error}`, request_ip);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                mac = body.split(";")[0];
                wol_apply(request_ip, session, mac);
            }
            else {
                log.error(log_header, `[/info failed] status code ${statusCode} returned.`, request_ip);
            }           
        }
    });
}

function wol_apply(request_ip, session, mac) {
    const options = {
        uri: baseUrl + "sess-bin/wol_apply.cgi",
        headers: {
            'Cookie': `efm_session_id=${session}`,
            'Referer': baseUrl,
            'Host' : host,
            'Connection': 'Keep-Alive',
            'User-Agent': 'Apache-HttpClient/UNAVAILABLE (java 1.4)'
        },
        qs: {
            'act':'wakeup',
            'mac':mac
        }
    };

    request.get(options, function (error, response, body) {
        if (error) {
            log.error(log_header, `[/wol_apply failed] ${error}`, request_ip);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                if(body==="Ok") done(request_ip);
                else log.error(log_header, "body: " + body, request_ip);
            }
            else {
                log.error(log_header, `[/wol_apply failed] status code ${statusCode} returned.`, request_ip);
            }           
        }
    });
}

function done(request_ip) {
    // log.verbose(log_header, `Wake On Lan Success.`, request_ip);
}

module.exports = {
    wakeup : function (request_ip) {
        checkup(request_ip);
    }
}