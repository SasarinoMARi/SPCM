/*
 * 라우팅 테이블 정의 파일
 */

const sha256 = require('./../common/sha256').SHA256
const logger = require('./../common/logger')
const tokenManager = require('./../common/token-manager')

function run(command) {
    const { exec } = require("child_process");
    exec(command, (error, stdout, stderr) => {
        if (error) {
            logger.e(`error: ${error.message}`);
            return;
        }
        if (stderr) {
            logger.e(`stderr: ${stderr}`);
            return;
        }
        logger.v(`stdout: ${stdout}`);
    });
}
function runFile(command) {
    const { execFile } = require("child_process");
    execFile(command, (error, stdout, stderr) => {
        if (error) {
            logger.e(`error: ${error.message}`);
            return;
        }
        if (stderr) {
            logger.e(`stderr: ${stderr}`);
            return;
        }
        logger.v(`stdout: ${stdout}`);
    });
}

function getIp(req) {
    // return req.headers['x-forwarded-for'] ||  req.connection.remoteAddress; // 프록시 중첩 헤더
    return req.connection.remoteAddress;
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

var lookup_ips = [];
module.exports = {
    establishment: function (req, res, next) {
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
    },
    lookup: function (req, res, next) {
        var ip = getIp(req);
        if(!lookup_ips.includes(ip)) {
            logger.v(`/lookup from ${ip}`);
            lookup_ips.push(ip);
        }

        var result = { error : 0, message : ""}
        res.json(result);
    },
    wakeup: function (req, res, next) {
        logger.v(`/wakeup from ${getIp(req)}`);

        if(!checkLoggedIn(req, res)) return;
    
        var result = { error : 0, message : ""}
        res.json(result);
    },
    sleep: function (req, res, next) {
        logger.v(`/sleep from ${getIp(req)}`);
    
        if(!checkLoggedIn(req, res)) return;
        
        var result = { error : 0, message : ""}
        run("rundll32.exe powrprof.dll SetSuspendState");
        res.json(result);
    },
    reboot: function (req, res, next) {
        logger.v(`/reboot from ${getIp(req)}`);
    
        if(!checkLoggedIn(req, res)) return;
    
        var result = { error : 0, message : ""}
        run("shutdown /r /f");
        res.json(result);
    },
    shutdown: function (req, res, next) {
        logger.v(`/shutdown from ${getIp(req)}`);
    
        if(!checkLoggedIn(req, res)) return;
    
        var result = { error : 0, message : ""}
        run("shutdown /s /f");
        res.json(result);
    },
    do: function (req, res, next) {
        logger.v(`/do from ${getIp(req)}`);
        
        if(!checkLoggedIn(req, res)) return;
    
        var result = { error : 0, message : ""}
    
        res.json(result);
    },
    start_fs: function (req, res, next) {
        logger.v(`/start-fs from ${getIp(req)}`);
    
        if(!checkLoggedIn(req, res)) return;
        
        var result = { error : 0, message : ""}
        path_webshare = "D:/SasarinoMARi/OneDrive/프로그램/[서버]/Berryz WebShare v0.952 rev1187/WebShare.exe"
        runFile(path_webshare);
        
        res.json(result);
    },
    stop_fs: function (req, res, next) {
        logger.v(`/stop-fs from ${getIp(req)}`);
        
        if(!checkLoggedIn(req, res)) return;
        
        var result = { error : 0, message : ""}
        run("taskkill /f /im webshare.exe");
        
        res.json(result);
    },
    start_tv: function(req, res, next) {
        logger.v(`/start-tv from ${getIp(req)}`);

        if(!checkLoggedIn(req, res)) return;
        
        var result = { error : 0, message : ""}
        path_webshare = "C:/Program Files (x86)/TeamViewer/TeamViewer.exe";
        runFile(path_webshare);
        
        res.json(result);
    }
}