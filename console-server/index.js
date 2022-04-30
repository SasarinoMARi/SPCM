/*
 * 프로그램 진입점 파일
 */
require("dotenv").config();
const express = require('express');
const app = express();
const modules = require('./ModuleManager');
const Connection = require("./Connection");
const gateways = {
    system : require('./gateway/SystemGateway'),
    desktop : require('./gateway/DesktopGateway'),
    noti : require('./gateway/NotificationGateway'),
    food : require('./gateway/FoodGateway'),
    schedule : require('./gateway/ScheduleGateway'),
    weather : require('./gateway/WeatherGateway'),
};

/*
 * Request Body를 json으로 파싱하기 위한 설정
 */
app.use(express.urlencoded({extended: true}));
app.use(express.json())

/* 라우팅 테이블 정의 */
app.get('/establishment', (req, res, next) => gateways.system.establishment(new Connection(req, res)));
app.get('/lookup', (req, res, next) => gateways.system.lookup(new Connection(req, res)));
app.get('/reboot', (req, res, next) => gateways.system.reboot(new Connection(req, res)));
app.get('/logs', (req, res, next) => gateways.system.getLogs(new Connection(req, res)));
app.post('/log', (req, res, next) => gateways.system.writeLog(new Connection(req, res)));
app.get('/header_image', (req, res, next) => gateways.system.header_image(new Connection(req, res)));

app.post('/noti/update_fcm_token', (req, res, next) => gateways.noti.updateFcmToken(new Connection(req, res)));
app.post('/noti/send_fcm', (req, res, next) => gateways.noti.sendFcm(new Connection(req, res)));
app.post('/noti/send_mail', (req, res, next) => gateways.noti.sendMail(new Connection(req, res)));

app.get('/power/wakeup', (req, res, next) => gateways.desktop.wakeup(new Connection(req, res)));
app.get('/power/reboot', (req, res, next) => gateways.desktop.reboot(new Connection(req, res)));
app.get('/power/shutdown', (req, res, next) => gateways.desktop.shutdown(new Connection(req, res)));

app.get('/file_server/start', (req, res, next) => gateways.desktop.startFileServer(new Connection(req, res)));
app.get('/rdp_server/start', (req, res, next) => gateways.desktop.startTeamviewerServer(new Connection(req, res)));

app.get('/media/volume', (req, res, next) => gateways.desktop.setVolume(new Connection(req, res)));
app.get('/media/mute', (req, res, next) => gateways.desktop.mute(new Connection(req, res)));
app.get('/media/play', (req, res, next) => gateways.desktop.play(new Connection(req, res)));

app.get('/food/pick_random', (req, res, next) => gateways.food.pickRandom(new Connection(req, res)));
app.get('/food/get', (req, res, next) => gateways.food.getFoods(new Connection(req, res)));

app.get('/schedule/reload', (req, res, next) => gateways.schedule.reload(new Connection(req, res)));
app.get('/schedule/get', (req, res, next) => gateways.schedule.get(new Connection(req, res)));
app.post('/schedule/set', (req, res, next) => gateways.schedule.set(new Connection(req, res)));

app.get('/weather/get', (req, res, next) => gateways.weather.getWeather(new Connection(req, res)));

app.use(express.static(__dirname + '/public'));
app.use((req, res, next) => gateways.system.default(new Connection(req, res)));

modules.scheduler.loadSchedules();

var port = process.env.PORT;
var server = app.listen(port, function () {
    console.log(`Server has started on port ${port}`);

    // DB 서버 초기화 주먹구구로 대기 (5초면 되지 않을까)
    setTimeout(function() {
        modules.log.info('index.js', '나루 서버가 시작됩니다.');
    }, 5 * 1000);
    
});
