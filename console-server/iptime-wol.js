/*
 * IpTime 공유기 API 파일
 */

const request = require('request');
const logger = require("./../common/logger")

let host = "sasarinomar1.iptime.org:80";
let baseUrl = `http://${host}/`;

function checkup() {
    logger.d("wol: checkup");

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
            logger.e("checkup error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                version(body);
            }
            else {
                logger.e("checkup statusCode : " + statusCode);
            }
        }
    });
}

function version() {
    logger.d("wol : version");

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
            logger.e("version error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                hostinfo(body);
            }
            else {
                logger.e("version statusCode : " + statusCode);
            }
        }
    });
}

function hostinfo() {
    logger.d("wol : hostinfo");

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
            logger.e("hostinfo error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                login_handler(body);
            }
            else {
                logger.e("hostinfo statusCode : " + statusCode);
            }
        }
    });
}

function login_handler() {
    logger.d("wol : login_handler");

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
            logger.e("login_handler error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                info(body);
            }
            else {
                logger.e("login_handler statusCode : " + statusCode);
            }
        }
    });
}

function info(session) {
    logger.d("wol : info");

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
            logger.e("info error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                mac = body.split(";")[0];
                wol_apply(session, mac);
            }
            else {
                logger.e("info statusCode : " + statusCode);
            }           
        }
    });
}

function wol_apply(session, mac) {
    logger.d("wol : wol_apply : " + mac);

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
            logger.e("wol_apply error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                if(body==="Ok") done(body);
                else logger.e("body: " + body);
            }
            else {
                logger.e("wol_apply statusCode : " + statusCode);
            }           
        }
    });
}

function done() {
    logger.v('Wake On Lan Success.');
}

module.exports = {
    wakeup : function () {
        checkup();
    }
}