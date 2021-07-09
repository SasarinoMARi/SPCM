const express = require('express');
const app = express();
const sha256 = require('./../common/sha256').SHA256
const logger = require('./../common/logger')
const tokenManager = require('./../common/token-manager')

/*
 * EJS 렌더링 설정 및 공용 디렉터리 호스팅
 */
app.engine('html', require('ejs').renderFile);
app.set('view engine', 'html');
app.use(express.static(__dirname + '/public'));

const remoteServer = require('./remoteServer')

function getIp(req) {
    // return req.headers['x-forwarded-for'] ||  req.connection.remoteAddress; // 프록시 중첩 헤더
    return req.connection.remoteAddress;
}

function getUTC9Date() {
    const curr = new Date();
    const utc = 
        curr.getTime() + 
        (curr.getTimezoneOffset() * 60 * 1000);

    const KR_TIME_DIFF = 9 * 60 * 60 * 1000;
    const kr_curr = 
        new Date(utc + (KR_TIME_DIFF));

    return kr_curr;
}

function getKey() {
    var d = getUTC9Date();
    var h = d.getHours();
    var m = d.getMinutes();
    var k = `s${(m+h)}`;
    return sha256(k);
}

function checkKey(key) {
    var k = getKey();

    logger.v(`input :\t${key}`)
    logger.v(`comp :\t${k}`)
    return key === k;
}

function unauthorized(res) {
    res.statusCode = 403
    res.message = "인증 정보가 잘못되었습니다."
    res.json();
}

function checkLoggedIn(req, res) {
    let token = req.headers.token;
    logger.v("token: " + token);
    if(!tokenManager.contains(token)) {
        unauthorized(res);
        return false;
    }
    else return true;
}

app.get('/establishment', function (req, res, next) {
    const ip = getIp(req);

    var result = { error : 0, message : ""}
    var key = req.headers.key
    if(key === undefined || !checkKey(key)) {
        logger.v(`${ip} : establishment failed`);
        unauthorized(res);
    }
    else {
        logger.v(`${ip} : establishment successed`);
        result.token = tokenManager.new();
        res.json(result);
    }
});

var lookup_ips = [];
app.get('/lookup', async function(req, res, next) {
    var ip = getIp(req);
    if(!lookup_ips.includes(ip)) {
        logger.v(`/lookup from ${ip}`);
        lookup_ips.push(ip);
    }

    var json = { error : 0, message : ""};

    var result = await remoteServer.lookup();
    if(result === null) json.error = 1
    res.json(json);
});

app.get('/wakeup', function (req, res, next) {
    logger.v(`/wakeup from ${getIp(req)}`);

    if(!checkLoggedIn(req, res)) return;

    var result = { error : 0, message : ""}
    require("./iptime-wol").wakeup();
    res.json(result);
});

app.get('/sleep', function (req, res, next) {
    logger.v(`/sleep from ${getIp(req)}`);

    if(!checkLoggedIn(req, res)) return;
    
    remoteServer.sleep({
        success : function () {

        },
        error : function () {

        }
    });
    res.json({ error : 0, message : ""});
});

app.get('/reboot', function (req, res, next) {
    logger.v(`/reboot from ${getIp(req)}`);

    if(!checkLoggedIn(req, res)) return;

    remoteServer.reboot({
        success : function () {

        },
        error : function () {

        }
    });
    res.json({ error : 0, message : ""});
});

app.get('/shutdown', function (req, res, next) {
    logger.v(`/shutdown from ${getIp(req)}`);

    if(!checkLoggedIn(req, res)) return;

    remoteServer.shutdown({
        success : function () {

        },
        error : function () {

        }
    });
    res.json({ error : 0, message : ""});
});

app.get('/do', function (req, res, next) {
    logger.v(`/do from ${getIp(req)}`);

    if(!checkLoggedIn(req, res)) return;

    remoteServer.do({
        success : function () {

        },
        error : function () {

        }
    });
    res.json({ error : 0, message : ""});
});

app.get('/logs', function (req, res, next) {
    logger.v(`/logs from ${getIp(req)}`);

    if(!checkLoggedIn(req, res)) return;
    
    remoteServer.logs({
        success : function () {

        },
        error : function () {

        }
    });
    res.json({ error : 0, message : ""});
});



app.get('/start-fs', function (req, res, next) {
    logger.v(`/start-fs from ${getIp(req)}`);

    if(!checkLoggedIn(req, res)) return;

    remoteServer.start_fs({
        success : function () {

        },
        error : function () {

        }
    });
    res.json({ error : 0, message : ""});
});


app.get('/stop-fs', function (req, res, next) {
    logger.v(`/stop-fs from ${getIp(req)}`);
    
    if(!checkLoggedIn(req, res)) return;

    remoteServer.stop_fs({
        success : function () {

        },
        error : function () {

        }
    });
    res.json({ error : 0, message : ""});
});


app.get('/', function (req, res, next) {
    res.render("main.ejs");
});

var port = process.env.PORT || 4424;
var server = app.listen(port, function () {
    console.log(`Server has started on port ${port}`);
});