const app = require('express')();
const sha256 = require('./../sha256').SHA256

const session = require('express-session');

app.use(session({
 secret: '*HR:_!a+_Ut&ZxzA3w8sHu:%',
 resave: false,
 saveUninitialized: true
}));

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

function log(ip, msg) {
    console.log(`[${Date()}] ${ip} : ${msg}`);
}

function checkKey(key) {
    var k = require("./../sha256").SHA256(require("./../secret").key);
    console.log(`input :\t${key}`)
    console.log(`comp :\t${k}`)
    return key === k;
}

function unauthorized(res) {
    res.statusCode = 403
    res.message = "인증 정보가 잘못되었습니다."
    res.json();
}

function checkLoggedIn(req, res) {
    if(req.session.loggedIn != true) {
        unauthorized(res);
        return false;
    }
    else return true;
}

app.get('/establishment', function (req, res, next) {
    const ip = getIp(req);

    var result = { error : 0, message : ""}
    var key = req.query.key
    if(key === undefined || !checkKey(key)) {
        log(ip, "establishment failed");
        unauthorized(res);
    }
    else {
        log(ip, "establishment successed");
        req.session.loggedIn = true;
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
    console.log(`Server has started on port ${port}`);
});