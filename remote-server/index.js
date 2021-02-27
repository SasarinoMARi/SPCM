const app = require('express')();
const sha256 = require('./../common/sha256').SHA256
const logger = require('./../common/logger')
const tokenManager = require('./../common/token-manager')

function run(command) {
    const { exec } = require("child_process");
    exec(command, (error, stdout, stderr) => {
        if (error) {
            console.log(`error: ${error.message}`);
            return;
        }
        if (stderr) {
            console.log(`stderr: ${stderr}`);
            return;
        }
        console.log(`stdout: ${stdout}`);
    });
}

function getIp(req) {
    return req.headers['x-forwarded-for'] ||  req.connection.remoteAddress;
}

function checkKey(key) {
    var k = require("./../common/sha256").SHA256(require("../common/secret").key);

    logger.v(`input :\t${key}`)
    // logger.v(`comp :\t${k}`)
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


app.get('/lookup', function (req, res, next) {
    var result = { error : 0, message : ""}
    res.json(result);
});

app.get('/wakeup', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;

    var result = { error : 0, message : ""}
    res.json(result);
});

app.get('/sleep', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;
    
    var result = { error : 0, message : ""}
    run("rundll32.exe powrprof.dll SetSuspendState");
    res.json(result);
});

app.get('/reboot', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;

    var result = { error : 0, message : ""}
    run("shutdown /r /f");
    res.json(result);
});

app.get('/shutdown', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;

    var result = { error : 0, message : ""}
    run("shutdown /s /f");
    res.json(result);
});

app.get('/do', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;

    var result = { error : 0, message : ""}

    res.json(result);
});

app.get('/logs', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;
    
    var result = { error : 0, message : ""}
    
    res.json(result);
});

var port = 4425
var server = app.listen(port, function () {
    logger.v(`Server has started on port ${port}`);
});