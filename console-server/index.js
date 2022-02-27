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

/* 라우팅 테이블 정의 */
app.get('/', router.default);
app.get('/establishment', router.establishment);
app.get('/lookup', router.lookup);
app.get('/wakeup', router.wakeup);
app.get('/sleep', router.sleep);
app.get('/reboot', router.reboot);
app.get('/shutdown', router.shutdown);
app.get('/start-fs', router.start_fs);
app.get('/stop-fs', router.stop_fs);
app.get('/start-tv', router.start_tv);
app.get('/reboot-pi', router.reboot_pi);
app.get('/hetzer', router.hetzer);

var port = process.env.PORT;
var server = app.listen(port, function () {
    console.log(`Server has started on port ${port}`);
});