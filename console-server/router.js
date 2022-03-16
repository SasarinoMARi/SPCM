/*
 * 라우팅 테이블 정의 파일
 */

const secret = require('../common/secret');
const logger = require('./../common/logger')
const tokenManager = require('./../common/token-manager')
const api = require('./remote-server-api');
const fcm = require('./fcm');
const mail = require('./email');

// 요청자 ip 반환 함수
function getIp(req) {
    // return req.headers['x-forwarded-for'] ||  req.connection.remoteAddress; // 프록시 중첩 헤더
    return req.connection.remoteAddress;
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
    
        if(secret.check(req.headers.key)) {
            logger.v(`${ip} : establishment successed`);
            var token = tokenManager.new();
            res.send(token);
        }
        else {
            logger.v(`${ip} : establishment failed`);
            unauthorized(res);
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
    },
    fcm_send: function(req, res, next) {
        logger.v(`/fcm_send from ${getIp(req)}`);
        
        if(!checkLoggedIn(req, res)) return;
    
        var title = req.body.title;
        var body = req.body.body;
        fcm.send(title, body, {
            success: function() {
                res.send("OK");
            },
            error: function(msg) {
                logger.e(msg);
                res.statusCode = 500;
                res.send("");
            }
        })
    },
    fcm_update_token: function(req, res, next) {
        logger.v(`/fcm_update_token from ${getIp(req)}`);

        if(!checkLoggedIn(req, res)) return;

        var token = req.body.token;
        fcm.update_id(token);
        res.send("OK");
    },
    mail_send: function(req, res, next) {
        logger.v(`/mail_send from ${getIp(req)}`);
        
        if(!checkLoggedIn(req, res)) return;
    
        var title = req.body.title;
        var body = req.body.body;
        mail.send(title, body);
        res.send("OK");
    },
}