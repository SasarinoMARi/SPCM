const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');
require("dotenv").config();
const request = require('request');

const key = process.env.OPENWEATHERMAP_API_KEY;

const Gateway = require("./GatewayBase");
class NotificationGateway extends Gateway {
    constructor() { super() }

    #buildOption(lat, lon) {
        // 좌표 올바르지 않으면 낙성대역으로 초기화
        if(!lat || !lon) {
            lat = 37.477679;
            lon = 126.963449;
        } 

        return {
            uri: `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${key}`
        }
    }

    getWeather(conn) {
        const options = this.#buildOption(conn.body.lat, conn.body.lon);
        request.get(options, (error, response, body) => {
            if (error) {
                conn.internalError();
                modules.log.error(LOG_SUBJECT, error);
                return;
            } else if(response.statusCode != 200) {
                conn.internalError();
                let logMsg = `Response status code is not 200\n[${response.statusCode}] ${response.statusMessage}`;
                modules.log.error(LOG_SUBJECT, logMsg);
                return;
            } else if (!response.body) {
                conn.internalError();
                let logMsg = `Response body is empty\n[${response.statusCode}] ${response.statusMessage}`;
                modules.log.error(LOG_SUBJECT, logMsg);
                return;
            }
            const json = JSON.parse(response.body);
        
            // 켈빈에서 섭씨로 변환: -273.15
            const temp =  Math.round((json.main.temp - 273.15)*10)/10; 
            const temp_min = Math.round((json.main.temp_min - 273.15)*10)/10;
            const temp_max = Math.round((json.main.temp_max - 273.15)*10)/10;
        
            const weather = json.weather[0].id;
            const icon = `http://openweathermap.org/img/wn/${json.weather[0].icon}@2x.png`;
        
            const result = {
                weather: weather,
                weather_icon: icon,
        
                temp: temp,
                temp_min: temp_min,
                temp_max: temp_max
            };
            
            conn.send(result);
        });
    }
}

module.exports = new NotificationGateway();