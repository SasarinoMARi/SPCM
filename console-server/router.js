/*
 * 라우팅 테이블 정의 파일
 */

const secret = require('../common/secret');
const log = require('./logger')
const tokenManager = require('./../common/token-manager')
const remote_server = require('./desktop-api');
const shell = require('shelljs');
const notifier = require('./messaging/notifier');
const fd = require('./food_dispenser/food_api');
const temperature = require('./temperature');
const sql = require('./database/sql');
const scheduler = require('./scheduler');

const log_header = 'router.js';

function getIpAddress(req) {
    var ip = req.connection.remoteAddress;
    ip.replace("::ffff:192.168.0.", "localhost ");
    if(ip.startsWith("::ffff:")) ip = ip.slice(7);
    return ip;
}

function authorize(req, res) {
    let token = req.headers.token;
    if(!tokenManager.contains(token)) {
        res.statusCode = 403
        res.send("Unauthorized");
        return false;
    }
    else return true;
}

var lookup_ips = []; // lookup을 요청한 사용자의 ip 수집

module.exports = {
    // 서비스 제어 코드
    system: {
        default: function (req, res, next) {
            res.render("wtf.ejs");
            log.critical(log_header, `잘못된 경로로 접근 요청됨 : ${req.originalUrl}`, getIpAddress(req));
        },    
        establishment: function (req, res, next) {
            const ip = getIpAddress(req);
            if(secret.check(req.headers.key)) {
                var token = tokenManager.new();
                res.send(token);
            }
            else {
                log.critical(log_header, `잘못된 토큰으로 인증 시도됨`, ip);
                res.statusCode = 403
                res.message = "Unauthorized"
                res.json();
            }
        },
        lookup: async function(req, res, next) {
            var ip = getIpAddress(req);
            if(!lookup_ips.includes(ip)) {
                log.verbose(log_header, `새로운 아이피로부터 lookup 요청됨`, ip);
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
            const ip = getIpAddress(req);
            log.verbose(log_header, `서버 재부팅 요청됨`, ip);
            if(!authorize(req, res)) return;
            shell.exec('sudo reboot');
            res.send("OK");
        },
        logs: function(req, res, next) {
            const ip = getIpAddress(req);
            if(!authorize(req, res)) return;
            const level = req.headers.level ? req.headers.level : 0;
            const page = req.headers.page ? req.headers.page : 0;

            const rpp = 50;
            const offect = rpp * page;

            const query = `SELECT * from \`log\` where \`level\`>=${level} ORDER BY idx desc LIMIT ${rpp} OFFSET ${offect}`;
            sql.query(query, function(err, logs, fields) {
                if(err) {
                    log.error(log_header, `Error fetching log list: ${err}`);
                    return;
                }
                res.json(logs);
            });
        },
        log: function(req, res, next) {
            const ip = getIpAddress(req);
            if(!authorize(req, res)) return;
            const level = req.body.level;
            const subject = req.body.subject;
            const content = req.body.content;
            if(!level || !subject || !content) {
                res.statusCode = 500;
                res.send("");
                return;
            }
            log.log(level, subject, content, ip);
            res.send("OK");
        },
        reload_schedule: function(req, res, next) {
            const ip = getIpAddress(req);
            if(!authorize(req, res)) return;
            res.send("OK");

            scheduler.loadSchedules();
        }
    },

    // 알림 관련 코드
    noti : {
        // spcm 앱으로 푸쉬 알림 송신
        send_fcm: function(req, res, next) {
            const ip = getIpAddress(req);
            
            if(!authorize(req, res)) return;
        
            var title = req.body.title;
            var body = req.body.body;
            notifier.sendFcm(title, body, {
                success: function() {
                    res.send("OK");
                    log.verbose(log_header, `FCM 송신 성공 : ${body}`, ip);
                },
                error: function(msg) {
                    res.statusCode = 500;
                    res.send("");
                    log.error(log_header, `FCM 송신 실패 : ${msg}`, ip);
                }
            })
        },
        // 푸쉬 알림 갱신용 함수
        update_fcm_token: function(req, res, next) {
            const ip = getIpAddress(req);
    
            if(!authorize(req, res)) return;
    
            var token = req.body.token;
            notifier.update_fcm_id(token);
            res.send("OK");

            log.verbose(log_header, `FCM 토큰 업데이트됨 : ${token}`, ip);
        },
        // 관리자에게 메일 전송
        send_mail: function(req, res, next) {
            const ip = getIpAddress(req);
            
            if(!authorize(req, res)) return;
        
            var title = req.body.title;
            var body = req.body.body;
            notifier.sendEmail(title, body);
            res.send("OK");

            log.verbose(log_header, `메일 송신 성공 : ${body}`, ip);
        }
    },

    // 원격 컴퓨터 전원 제어 코드
    power: {
        wakeup: function (req, res, next) {
            const ip = getIpAddress(req);
            log.verbose(log_header, `데스크탑 부팅 요청됨`, ip);
            if(!authorize(req, res)) return;
            require("./iptime-api").wakeup(ip);
            res.send("OK");
        },
        sleep: function (req, res, next) {
            const ip = getIpAddress(req);
            log.verbose(log_header, `데스크탑 절전 요청됨`, ip);
            if(!authorize(req, res)) return;
            remote_server.sleep(ip);
            res.send("OK");
        },
        reboot: function (req, res, next) {
            const ip = getIpAddress(req);
            log.verbose(log_header, `데스크탑 재시작 요청됨`, ip);
            if(!authorize(req, res)) return;
            remote_server.reboot(ip);
            res.send("OK");
        },
        shutdown: function (req, res, next) {
            const ip = getIpAddress(req);
            log.verbose(log_header, `데스크탑 종료 요청됨`, ip);
            if(!authorize(req, res)) return;
            remote_server.shutdown(ip);
            res.send("OK");
        }
    }, 

    // 파일 서버 제어 코드
    file_server : {
        start: function (req, res, next) {
            const ip = getIpAddress(req);
            log.verbose(log_header, `파일 서버 시작 요청됨`, ip);
            if(!authorize(req, res)) return;
            remote_server.startFileServer(ip);
            res.send("OK");
        },
        stop: function (req, res, next) {
            const ip = getIpAddress(req);
            log.verbose(log_header, `파일 서버 중단 요청됨`, ip);
            if(!authorize(req, res)) return;
            remote_server.stopFileServer(ip);
            res.send("OK");
        }
    },

    // rdp 서버 제어 코드
    rdp_server : {
        start: function (req, res, next) {
            const ip = getIpAddress(req);
            log.verbose(log_header, `팀뷰어 서버 시작 요청됨`, ip);
            if(!authorize(req, res)) return;
            remote_server.startRdpServer(ip);
            res.send("OK");
        }    
    },
    

    // 원격지 미디어 제어 코드
    media : {
        // 볼륨 제어
        volume: function (req, res, next) {
            const ip = getIpAddress(req);
            if(!authorize(req, res)) return;
            let volume = req.headers.amount;
            if(volume===undefined) {
                res.statusCode = 400;
                res.send("Bad Request");
                return;
            };
            remote_server.volume(ip, volume);
            res.send("OK");
        },
        // 음소거 설정/해제, 0일때 해제, 1일때 뮤트, 이외는 토글
        mute: function (req, res, next) {
            const ip = getIpAddress(req);
            if(!authorize(req, res)) return;
            let option = req.headers.option;
            if(option===undefined) {
                option = 2;
            };
            remote_server.mute(ip, option);
            res.send("OK");
        },
        // 유튜브 영상 재생
        play: function (req, res, next) {
            const ip = getIpAddress(req);
            log.verbose(log_header, `데스크탑 링크 실행 요청됨`, ip);
            if(!authorize(req, res)) return;
            let src = req.headers.src;
            if(src===undefined) {
                res.statusCode = 400;
                res.send("Bad Request");
                return;
            };
            remote_server.play(ip, src);
            res.send("OK");
        }
    },

    hetzer: function(req, res, next) {
        log.verbose(log_header, `트윗 청소기 실행 요청됨`, getIpAddress(req));
        
        if(!authorize(req, res)) return;
    
        res.send("OK"); // 트청 끝난 후에 반환하면 타임아웃남
        shell.exec('sh /git/tweeter/hetzer.sh');
    },

    food_dispenser: function(req, res, next) {
        if(!authorize(req, res)) return;
        fd.random(function(result) {
            res.json(result); 
        }, log.error);
    }
}