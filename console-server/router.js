/*
 * 라우팅 테이블 정의 파일
 */

const secret = require('../common/secret');
const log = require('./logger')
const tokenManager = require('./../common/token-manager')
const remote_server = require('./remote-server-api');
const shell = require('shelljs');
const fcm = require('./messaging/fcm');
const mail = require('./messaging/email');
const fd = require('./food_dispenser/food_api');
const temperature = require('./temperature');

const log_header = 'router.js';

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
            log.warning(log_header, `잘못된 경로로 접근 요청됨`, ipv4(req));
        },    
        establishment: function (req, res, next) {
            const ip = ipv4(req);
        
            if(secret.check(req.headers.key)) {
                var token = tokenManager.new();
                res.send(token);
            }
            else {
                log.warning(log_header, `잘못된 토큰으로 인증 시도됨`, ip);
                res.statusCode = 403
                res.message = "Unauthorized"
                res.json();
            }
        },
        lookup: async function(req, res, next) {
            var ip = ipv4(req);
            if(!lookup_ips.includes(ip)) {
                log.verbose(log_header, `새로운 lookup 요청됨`, ip);
                lookup_ips.push(ip);
            }

            var result = { server : { status : 1, temp : temperature.getTemp() }, pc : { status : 0 } };

            var lookup_result = await remote_server.lookup();
            if(lookup_result != undefined && lookup_result != null) {
                lookup_result = JSON.parse(lookup_result);
                result.pc.status = lookup_result.status;
                result.pc.temp = lookup_result.temp;
            }
            
            res.json(result);
        },
        reboot: function(req, res, next) {
            log.verbose(log_header, `서버 재부팅 요청됨`, ipv4(req));
            if(!authorize(req, res)) return;
            log.info(log_header, `서버 재부팅 요청 승인됨`, ipv4(req));
            shell.exec('sudo reboot');
            res.send("OK");
        },
    },

    // 알림 관련 코드
    noti : {
        // spcm 앱으로 푸쉬 알림 송신
        send_fcm: function(req, res, next) {
            log.verbose(log_header, `FCM 송신 요청됨`, ipv4(req));
            
            if(!authorize(req, res)) return;
        
            var title = req.body.title;
            var body = req.body.body;
            fcm.send(title, body, {
                success: function() {
                    res.send("OK");
                    log.verbose(log_header, `FCM 송신 성공됨 : ${body}`, ipv4(req));
                },
                error: function(msg) {
                    res.statusCode = 500;
                    res.send("");
                    log.error(log_header, `FCM 송신 실패됨 : ${msg}`, ipv4(req));
                }
            })
        },
        // 푸쉬 알림 갱신용 함수
        update_fcm_token: function(req, res, next) {
            log.verbose(log_header, `FCM 토큰 업데이트 요청됨`, ipv4(req));
    
            if(!authorize(req, res)) return;
    
            var token = req.body.token;
            fcm.update_id(token);
            res.send("OK");

            log.info(log_header, `FCM 토큰 업데이트됨 : ${token}`, ipv4(req));
        },
        // 관리자에게 메일 전송
        send_mail: function(req, res, next) {
            log.verbose(log_header, `메일 송신 요청됨`, ipv4(req));
            
            if(!authorize(req, res)) return;
        
            var title = req.body.title;
            var body = req.body.body;
            mail.send(title, body);
            res.send("OK");

            log.verbose(log_header, `메일 송신 성공됨 : ${body}`, ipv4(req));
        }
    },

    // 원격 컴퓨터 전원 제어 코드
    power: {
        wakeup: function (req, res, next) {
            log.verbose(log_header, `데스크탑 부팅 요청됨`, ipv4(req));
            if(!authorize(req, res)) return;
            require("./iptime-wol").wakeup();
            res.send("OK");
        },
        sleep: function (req, res, next) {
            log.verbose(log_header, `데스크탑 절전 요청됨`, ipv4(req));
            if(!authorize(req, res)) return;
            remote_server.sleep(ipv4(req));
            res.send("OK");
        },
        reboot: function (req, res, next) {
            log.verbose(log_header, `데스크탑 재시작 요청됨`, ipv4(req));
            if(!authorize(req, res)) return;
            remote_server.reboot(ipv4(req));
            res.send("OK");
        },
        shutdown: function (req, res, next) {
            log.verbose(log_header, `데스크탑 종료 요청됨`, ipv4(req));
            if(!authorize(req, res)) return;
            remote_server.shutdown(ipv4(req));
            res.send("OK");
        }
    }, 

    // 파일 서버 제어 코드
    file_server : {
        start: function (req, res, next) {
            log.verbose(log_header, `파일 서버 시작 요청됨`, ipv4(req));
            if(!authorize(req, res)) return;
            remote_server.startFileServer(ipv4(req));
            res.send("OK");
        },
        stop: function (req, res, next) {
            log.verbose(log_header, `파일 서버 중단 요청됨`, ipv4(req));
            if(!authorize(req, res)) return;
            remote_server.stopFileServer(ipv4(req));
            res.send("OK");
        }
    },

    // rdp 서버 제어 코드
    rdp_server : {
        start: function (req, res, next) {
            log.verbose(log_header, `팀뷰어 서버 시작 요청됨`, ipv4(req));
            if(!authorize(req, res)) return;
            remote_server.startRdpServer(ipv4(req));
            res.send("OK");
        }    
    },
    

    // 원격지 미디어 제어 코드
    media : {
        // 볼륨 제어
        volume: function (req, res, next) {
            log.verbose(log_header, `데스크탑 볼륨 제어 요청됨`, ipv4(req));
            if(!authorize(req, res)) return;
            let volume = req.headers.amount;
            if(volume===undefined) {
                res.statusCode = 400;
                res.send("Bad Request");
                return;
            };
            remote_server.volume(ipv4(req), volume);
            res.send("OK");
        },
        // 음소거 설정/해제, 0일때 해제, 1일때 뮤트, 이외는 토글
        mute: function (req, res, next) {
            log.verbose(log_header, `데스크탑 음소거 요청됨`, ipv4(req));
            if(!authorize(req, res)) return;
            let option = req.headers.option;
            if(option===undefined) {
                option = 2;
            };
            remote_server.mute(ipv4(req), option);
            res.send("OK");
        },
        // 유튜브 영상 재생
        play: function (req, res, next) {
            log.verbose(log_header, `데스크탑 링크 실행 요청됨`, ipv4(req));
            if(!authorize(req, res)) return;
            let src = req.headers.src;
            if(src===undefined) {
                res.statusCode = 400;
                res.send("Bad Request");
                return;
            };
            remote_server.play(ipv4(req), src);
            res.send("OK");
        }
    },

    hetzer: function(req, res, next) {
        log.verbose(log_header, `트윗 청소기 실행 요청됨`, ipv4(req));
        
        if(!authorize(req, res)) return;
    
        res.send("OK"); // 트청 끝난 후에 반환하면 타임아웃남
        shell.exec('sh /git/tweeter/hetzer.sh');
    },

    food_dispenser: function(req, res, next) {
        log.verbose(log_header, `메뉴추천기 실행 요청됨`, ipv4(req));
        
        if(!authorize(req, res)) return;
        fd.random(function(result) {
            res.json(result); 
        }, log.error);
    }
}