/*
 * 라우팅 테이블 정의 파일
 */

const secret = require('../common/secret');
const logger = require('./../common/logger');
const tokenManager = require('./../common/token-manager');
const path = require('./path.json');
const audio = require('win-audio').speaker;
const temperature = require('./temperature/temperature');

// 명령어 실행
function command(command) {
    const { exec } = require("child_process");
    exec(command, (error, stdout, stderr) => {
        if (error) {
            logger.e(`command error: ${error.message}`);
            return;
        }
        if (stderr) {
            logger.e(`command stderr: ${stderr}`);
            return;
        }
        logger.v(`command stdout: ${stdout}`);
    });
}

// 파일 실행
function execute(command) {
    const { execFile } = require("child_process");
    execFile(command, (error, stdout, stderr) => {
        if (error) {
            logger.e(`execute error: ${error.message}`);
            return;
        }
        if (stderr) {
            logger.e(`execute stderr: ${stderr}`);
            return;
        }
        logger.v(`execute stdout: ${stdout}`);
    });
}

// 접속자의 ipv4 주소를 반환
function ipv4(req) {
    var ip = req.connection.remoteAddress;
    ip.replace("::ffff:192.168.0.", "localhost ");
    if (ip.startsWith("::ffff:")) ip = ip.slice(7);
    return ip;
}

// 로그인 유효성 검사
function authorize(req, res) {
    if (!ipv4(req).startsWith("192.168.0")) {
        console.log("외부 대역에서 로그인 시도 발생!!");

        res.statusCode = 403;
        res.send("Unauthorized");
        return false;
    }

    let token = req.headers.token;
    logger.v(`new token: ${token.slice(0, 20)}...`);
    if(!tokenManager.contains(token)) {
        res.statusCode = 403;
        res.send("Unauthorized");
        return false;
    }
    else return true;
}

var lookup_ips = [];                                        // lookup을 요청한 사용자의 ip 수집


module.exports = {

    // spcm 제어
    system: {
        // 연결 수립
        establishment: function (req, res, next) {
            const ip = ipv4(req);
        
            var result = { error : 0, message : ""}
            if(secret.cr_key === req.headers.key) {
                logger.v(`${ip} : establishment successed`);
                result.token = tokenManager.new();
                res.json(result);
            }
            else {
                logger.v(`${ip} : establishment failed`);
                res.statusCode = 403;
                res.send("Unauthorized");
            }
        },
        // 업타임 체크
        lookup: async function (req, res, next) {
            var ip = ipv4(req);
            if(!lookup_ips.includes(ip)) {
                logger.v(`/lookup from ${ip}`);
                lookup_ips.push(ip);
            }
            var response = {
                status : 1,
                temp : await temperature.getTemp()
            }
            res.json(response);
        }
    },

    // 전원 제어
    power: {
        reboot: function (req, res, next) {
            logger.v(`/reboot from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            command("shutdown /r /f");
            res.send("OK");
        },
        shutdown: function (req, res, next) {
            logger.v(`/shutdown from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            command("shutdown /a");
            command("shutdown /s /f -t 5");
            res.send("OK");
        }
    },

    // 명령어 수행 요청
    do: function (req, res, next) {
        logger.v(`/do from ${ipv4(req)}`);
        if(!authorize(req, res)) return;
        // TODO: 구현
        res.send("OK");
    },

    // 파일 서버 제어
    file_server: {
        start: function (req, res, next) {
            logger.v(`/start-fs from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            execute(path.webshare);
            res.send("OK");
        },
        stop: function (req, res, next) {
            logger.v(`/stop-fs from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            command("taskkill /f /im webshare.exe");
            res.send("OK");
        }
    },

    // rdp 서버 제어
    rdp_server: {
        start: function(req, res, next) {
            logger.v(`/start-tv from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            execute(path.teamviewer);
            res.send("OK");
        }
    },

    media: {
        volume: function(req, res, next) {
            logger.v(`/volume from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            var volume = req.headers.amount;
            if(volume===undefined) {
                res.statusCode = 400;
                res.send("Bad Request");
                return;
            };
            volume = Number(volume);
            logger.v(`Set system volume to ${volume}`);
            audio.set(volume);
            res.send("OK");
        },
        // 음소거 설정/해제, 0일때 해제, 1일때 뮤트, 이외는 토글
        mute: function(req, res, next) {
            logger.v(`/mute from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            var option = req.headers.option;
            if(option===undefined) {
                res.statusCode = 400;
                res.send("Bad Request");
                return;
            };
            if(option == 0) audio.unmute();
            else if(option == 1) audio.mute();
            else if(audio.isMuted()) audio.unmute(); else audio.mute();
            res.send("OK");
        },
        play: function(req, res, next) {
            logger.v(`/play from ${ipv4(req)}`);
            if(!authorize(req, res)) return;
            let src = req.headers.src;
            if(src===undefined) {
                res.statusCode = 400;
                res.send("Bad Request");
                return;
            };
            command(`"${path.chrome}" --profile-directory="Default" ${src}`);

            logger.d(`play ${src}`);
            res.send("OK");
        }
    }
}