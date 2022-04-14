/*
 * 프로그램 진입점 파일
 */
require("dotenv").config();
const express = require('express');
const app = express();
const router = require('./router')

/*
 * EJS 렌더링 설정 및 공용 디렉터리 호스팅
 */
/*
외부 접속할 일 없으면 꺼놓기-> 보안 취약함
app.engine('html', require('ejs').renderFile);
app.set('view engine', 'html');
app.use(express.static(__dirname + '/public'));
*/

/*
 * Request Body를 json으로 파싱하기 위한 설정
 */
app.use(express.urlencoded({extended: true}));
app.use(express.json())

/* 라우팅 테이블 정의 */
app.get('/establishment', router.system.establishment);
app.get('/lookup', router.system.lookup);
app.get('/reboot', router.system.reboot);

app.post('/noti/send_fcm', router.noti.send_fcm);
app.post('/noti/update_fcm_token', router.noti.update_fcm_token);
app.post('/noti/send_mail', router.noti.send_mail);

app.get('/power/wakeup', router.power.wakeup);
app.get('/power/reboot', router.power.reboot);
app.get('/power/shutdown', router.power.shutdown);

app.get('/file_server/start', router.file_server.start);
app.get('/file_server/stop', router.file_server.stop);

app.get('/rdp_server/start', router.rdp_server.start);

app.get('/media/volume', router.media.volume);
app.get('/media/mute', router.media.mute);
app.get('/media/play', router.media.play);

app.get('/hetzer', router.hetzer);
app.get('/food_dispenser', router.food_dispenser);

var port = process.env.PORT;
var server = app.listen(port, function () {
    console.log(`Server has started on port ${port}`);
    require('./logger').info('index.js', '서버 시작');
});