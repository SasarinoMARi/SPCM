/*
 * 프로그램 진입점 파일
 */
require("dotenv").config();
const app = require('express')();
const router = require('./router');

/* 라우팅 테이블 초기화 */
app.get('/establishment', router.establishment);
app.get('/lookup', router.lookup);
app.get('/wakeup', router.wakeup);
app.get('/sleep', router.sleep);
app.get('/reboot', router.reboot);
app.get('/shutdown', router.shutdown);
app.get('/do', router.do);
app.get('/start-fs', router.start_fs);
app.get('/stop-fs', router.stop_fs);
app.get('/start-tv', router.start_tv);

var port = process.env.PORT;
var server = app.listen(port, function () {
    console.log(`Server has started on port ${port}`);
});