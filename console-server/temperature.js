/**
 * 라즈베리파이 온도 측정용 모듈
 */
const shell = require('shelljs');
const log = require('./logger');

module.exports = {
    getTemp : function() {
        var temp = "ERROR";
        try {
            const {stdout, stdin, code} = shell.exec("vcgencmd measure_temp", {silent : true});
            temp = stdout.slice(5).replace('\'C', '').trim();
        } catch(e) {
            log.error('temperature.js', e);
        }
        return temp;
    }
}