const request = require('request');
const logger = require("./../common/logger")

let host = "sasarinomar1.iptime.org:80";
let baseUrl = `http://${host}/`;

function checkup() {
    logger.d("api : checkup");

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
            logger.e("error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                version(body);
            }
            else {
                logger.e("statusCode : " + statusCode);
            }
        }
    });
}

function version() {
    logger.d("api : version");

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
            logger.e("error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                hostinfo(body);
            }
            else {
                logger.e("statusCode : " + statusCode);
            }
        }
    });
}

function hostinfo() {
    logger.d("api : hostinfo");

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
            logger.e("error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                login_handler(body);
            }
            else {
                logger.e("statusCode : " + statusCode);
            }
        }
    });
}

function login_handler() {
    logger.d("api : login_handler");

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
            logger.e("error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                info(body);
            }
            else {
                logger.e("statusCode : " + statusCode);
            }
        }
    });
}

function info(session) {
    logger.d("api : info");

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
            logger.e("error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                mac = body.split(";")[0];
                wol_apply(session, mac);
            }
            else {
                logger.e("statusCode : " + statusCode);
            }           
        }
    });
}

function wol_apply(session, mac) {
    logger.d("api : wol_apply");

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
            'mac':'mac'
        }
    };

    request.get(options, function (error, response, body) {
        if (error) {
            logger.e("error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;
            if (statusCode == 200) {
                if(body==="Ok") done(body);
                else logger.e("body: " + body);
            }
            else {
                logger.e("statusCode : " + statusCode);
            }           
        }
    });
}

function done() {
    logger.v('done!!');
}

module.exports = {
    wakeup : function () {
        checkup();
    }
}