/*
 * 라우팅 테이블 정의 파일
 */

const sha256 = require('./../common/sha256').SHA256
const logger = require('./../common/logger')
const tokenManager = require('./../common/token-manager')
const api = require('./remote-server-api');

// 요청자 ip 반환 함수
function getIp(req) {
    // return req.headers['x-forwarded-for'] ||  req.connection.remoteAddress; // 프록시 중첩 헤더
    return req.connection.remoteAddress;
}

// 한국시 반환 함수
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

// 올바른 인증 키 반환 함수
function getKey() {
    var d = getUTC9Date();
    var h = d.getHours();
    var m = d.getMinutes();
    var k = `s${(m+h)}`;
    return sha256(k);
}

// 키 검증 함수
function checkKey(key) {
    var k = getKey();

    logger.v(`input :\t${key}`)
    logger.v(`comp :\t${k}`)
    return key === k;
}

// 인증 실패시 호출하는 함수
function unauthorized(res) {
    res.statusCode = 403
    res.message = "인증 정보가 잘못되었습니다."
    res.json();
}

// 로그인 유효성 검사 함수
function checkLoggedIn(req, res) {
    let token = req.headers.token;
    // logger.v("token: " + token);
    if(!tokenManager.contains(token)) {
        unauthorized(res);
        return false;
    }
    else return true;
}

var lookup_ips = [];
module.exports = {
    default: function (req, res, next) {
        res.render("main.ejs");
    },    
    establishment: function (req, res, next) {
        const ip = getIp(req);
    
        var key = req.headers.key
        if(key === undefined || !checkKey(key)) {
            logger.v(`${ip} : establishment failed`);
            unauthorized(res);
        }
        else {
            logger.v(`${ip} : establishment successed`);
            var token = tokenManager.new();
            res.send(token);
        }
    },
    lookup: async function(req, res, next) {
        var ip = getIp(req);
        if(!lookup_ips.includes(ip)) {
            logger.v(`/lookup from ${ip}`);
            lookup_ips.push(ip);
        }
    
        var result = "Online";
        var lookup_result = await api.lookup();
        if(lookup_result === null) result = "Offline";
        res.send(result);
    },
    wakeup: function (req, res, next) {
        logger.v(`/wakeup from ${getIp(req)}`);
    
        if(!checkLoggedIn(req, res)) return;
    
        require("./iptime-wol").wakeup();
        res.send("OK");
    },
    sleep: function (req, res, next) {
        logger.v(`/sleep from ${getIp(req)}`);
    
        if(!checkLoggedIn(req, res)) return;
        
        api.sleep();
        res.send("OK");
    },
    reboot: function (req, res, next) {
        logger.v(`/reboot from ${getIp(req)}`);
    
        if(!checkLoggedIn(req, res)) return;
    
        api.reboot();
        res.send("OK");
    },
    shutdown: function (req, res, next) {
        logger.v(`/shutdown from ${getIp(req)}`);
    
        if(!checkLoggedIn(req, res)) return;
    
        api.shutdown();
        res.send("OK");
    }, 
    start_fs: function (req, res, next) {
        logger.v(`/start-fs from ${getIp(req)}`);
    
        if(!checkLoggedIn(req, res)) return;
    
        api.start_fs();
        res.send("OK");
    },
    stop_fs: function (req, res, next) {
        logger.v(`/stop-fs from ${getIp(req)}`);
        
        if(!checkLoggedIn(req, res)) return;
    
        api.stop_fs();
        res.send("OK");
    },
    start_tv: function (req, res, next) {
        logger.v(`/start-tv from ${getIp(req)}`);
    
        if(!checkLoggedIn(req, res)) return;
    
        api.start_tv();
        res.send("OK");
    },
    reboot_pi: function(req, res, next) {
        logger.v(`/reboot_pi from ${getIp(req)}`);
        
        if(!checkLoggedIn(req, res)) return;
    
        api.reboot_pi();
        res.send("OK");
    },
    hetzer: function(req, res, next) {
        logger.v(`/hetzer from ${getIp(req)}`);
        
        if(!checkLoggedIn(req, res)) return;
    
        res.send("OK"); // 트청 끝난 후에 반환하면 타임아웃남
        api.hetzer();
    }
}