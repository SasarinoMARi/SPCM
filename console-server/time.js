/**
 * 서버 시간과 상관없이 한국 기준시를 사용할 수 있게 해주는 모듈
 */
var moment = require('moment');
require('moment-timezone');
moment.tz.setDefault("Asia/Seoul");
module.exports = moment;