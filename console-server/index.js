const app = require('express')();
const session = require('express-session');
const sha256 = require('./../sha256').SHA256

const remoteServer = require('./remoteServer')

app.use(session({
 secret: '*HR:_!a+_Ut&ZxzA3w8sHu:%',
 resave: false,
 saveUninitialized: true
}));

function getIp(req) {
    return req.headers['x-forwarded-for'] ||  req.connection.remoteAddress;
}

function log(ip, msg) {
    console.log(`[${Date()}] ${ip} : ${msg}`);
}

function getKey() {
    var d = new Date();
    var h = d.getHours();
    var m = d.getMinutes();
    var k = `s${(m+h)}`;
    return sha256(k);
}

function checkKey(key) {
    var k = getKey();
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
    res.json(remoteServer.lookup());
});

app.get('/wakeup', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;

    var result = { error : 0, message : ""}
    
    res.json(result);
});

app.get('/sleep', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;
    
    res.json(remoteServer.sleep());
});

app.get('/reboot', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;

    res.json(remoteServer.reboot());
});

app.get('/shutdown', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;

    res.json(remoteServer.shutdown());
});

app.get('/do', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;

    res.json(remoteServer.do());
});

app.get('/logs', function (req, res, next) {
    if(!checkLoggedIn(req, res)) return;
    
    res.json(remoteServer.logs());
});

var port = 4424
var server = app.listen(port, function () {
    console.log(`Server has started on port ${port}`);
});