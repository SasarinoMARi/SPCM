/*
 * 프로그램 진입점 파일
 */

const PORT = 4426

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

var server = app.listen(PORT, function () {
    console.log(`Server has started on port ${PORT}`);
});