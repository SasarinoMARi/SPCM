/*
 * 라우팅 테이블 정의 파일
 */

const secret = require('../common/secret');
const logger = require('./../common/logger')
const tokenManager = require('./../common/token-manager')
const remote_server = require('./remote-server-api');
const shell = require('shelljs');
const fcm = require('./fcm');
const mail = require('./email');

// 접속자의 ipv4 주소를 반환
function ipv4(req) {
    // return req.headers['x-forwarded-for'] ||  req.connection.remoteAddress; // 프록시 중첩 헤더
    var ipv4 = req.connection.remoteAddress;
    ipv4.replace("::ffff:192.168.0.", "localhost-");
    return ipv4;
}

// 로그인 유효성 검사
function authorize(req, res) {
    let token = req.headers.token;
    // logger.v("token: " + token);
    if(!tokenManager.contains(token)) {
        res.statusCode = 403
        res.send("Unauthorized");
        return false;
    }
    else return true;
}

var lookup_ips = [];                                        // lookup을 요청한 사용자의 ip 수집

module.exports = {
    // 서비스 제어 코드
    system: {
        default: function (req, res, next) {
            res.render("main.ejs");
        },    
        establishment: function (req, res, next) {
            const ip = ipv4(req);
        
            if(secret.check(req.headers.key)) {
                logger.v(`${ip} : establishment successed`);
                var token = tokenManager.new();
                res.send(token);
            }
            else {
                logger.v(`${ip} : establishment failed`);
                res.statusCode = 403
                res.message = "Unauthorized"
                res.json();
            }
        },
        lookup: async function(req, res, next) {
            var ip = ipv4(req);
            if(!lookup_ips.includes(ip)) {
                logger.v(`/lookup from ${ip}`);
                lookup_ips.push(ip);
            }
        
            var result = "Online";
            var lookup_result = await remote_server.lookup();
            if(lookup_result === null) result = "Offline";
            res.send(result);
        },
        reboot: function(req, res, next) {
            logger.v(`/reboot_pi from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            shell.exec('sudo reboot');
            res.send("OK");
        },
    },

    // 알림 관련 코드
    noti : {
        // spcm 앱으로 푸쉬 알림 전송
        send_fcm: function(req, res, next) {
            logger.v(`/fcm_send from ${ipv4(req)}`);
            
            if(!authorize(req, res)) return;
        
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
        // 푸쉬 알림 갱신용 함수
        update_fcm_token: function(req, res, next) {
            logger.v(`/fcm_update_token from ${ipv4(req)}`);
    
            if(!authorize(req, res)) return;
    
            var token = req.body.token;
            fcm.update_id(token);
            res.send("OK");
        },
        // 관리자에게 메일 전송
        send_mail: function(req, res, next) {
            logger.v(`/mail_send from ${ipv4(req)}`);
            
            if(!authorize(req, res)) return;
        
            var title = req.body.title;
            var body = req.body.body;
            mail.send(title, body);
            res.send("OK");
        }
    },

    // 원격 컴퓨터 전원 제어 코드
    power: {
        wakeup: function (req, res, next) {
            logger.v(`/wakeup from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            require("./iptime-wol").wakeup();
            res.send("OK");
        },
        sleep: function (req, res, next) {
            logger.v(`/sleep from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            remote_server.sleep();
            res.send("OK");
        },
        reboot: function (req, res, next) {
            logger.v(`/reboot from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            remote_server.reboot();
            res.send("OK");
        },
        shutdown: function (req, res, next) {
            logger.v(`/shutdown from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            remote_server.shutdown();
            res.send("OK");
        }
    }, 

    // 파일 서버 제어 코드
    file_server : {
        start: function (req, res, next) {
            logger.v(`/start-fs from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            remote_server.start_fs();
            res.send("OK");
        },
        stop: function (req, res, next) {
            logger.v(`/stop-fs from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            remote_server.stop_fs();
            res.send("OK");
        }
    },

    // rdp 서버 제어 코드
    rdp_server : {
        start: function (req, res, next) {
            logger.v(`/start-tv from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            remote_server.start_tv();
            res.send("OK");
        }    
    },
    

    // 원격지 미디어 제어 코드
    media : {
        // 볼륨 제어
        volume: function (req, res, next) {
            logger.v(`/media/volume from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            let volume = req.headers.amount;
            if(volume===undefined) {
                res.statusCode = 400;
                res.send("Bad Request");
                return;
            };
            remote_server.volume(volume);
            res.send("OK");
        },
        // 음소거 설정/해제, 0일때 해제, 1일때 뮤트, 이외는 토글
        mute: function (req, res, next) {
            logger.v(`/media/mute from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            let option = req.headers.option;
            if(option===undefined) {
                option = 2;
            };
            remote_server.mute(option);
            res.send("OK");
        },
        // 유튜브 영상 재생
        play: function (req, res, next) {
            logger.v(`/media/play from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            let src = req.headers.src;
            if(src===undefined) {
                res.statusCode = 400;
                res.send("Bad Request");
                return;
            };
            remote_server.play(src);
            res.send("OK");
        }
    },

    hetzer: function(req, res, next) {
        logger.v(`/hetzer from ${ipv4(req)}`);
        
        if(!authorize(req, res)) return;
    
        res.send("OK"); // 트청 끝난 후에 반환하면 타임아웃남
        shell.exec('sh /git/tweeter/hetzer.sh');
    }
}