require("node-hide-console-window").hideConsole();

/*
 * 프로그램 진입점 파일
 */
require("dotenv").config();
const app = require('express')();
const router = require('./router');

/* 라우팅 테이블 초기화 */
app.get('/establishment', router.system.establishment);
app.get('/lookup', router.system.lookup);

app.get('/power/reboot', router.power.reboot);
app.get('/power/shutdown', router.power.shutdown);

app.get('/file_server/start', router.file_server.start);
app.get('/file_server/stop', router.file_server.stop);

app.get('/rdp_server/start', router.rdp_server.start);

app.get('/media/volume', router.media.volume);
app.get('/media/mute', router.media.mute);
app.get('/media/play', router.media.play);

app.get('/do', router.do);

var port = process.env.PORT;
var server = app.listen(port, function () {
    console.log(`Server has started on port ${port}`);
    require('./notification').sendFcm("알림", "PC가 부팅되었습니다.");
});