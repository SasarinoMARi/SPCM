const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');
require("dotenv").config();
const request = require('request');

const key = process.env.OPENWEATHERMAP_API_KEY;

const Gateway = require("./GatewayBase");
class NotificationGateway extends Gateway {
    constructor() { super() }

    #buildOption(url, lat, lon) {
        // 좌표 올바르지 않으면 낙성대역으로 초기화
        if(!lat || !lon) {
            lat = 37.477679;
            lon = 126.963449;
        } 

        return {
            uri: `${url}?lat=${lat}&lon=${lon}&appid=${key}`
        }
    }

    getWeather(conn) {
        const options = this.#buildOption('https://api.openweathermap.org/data/2.5/weather', conn.body.lat, conn.body.lon);
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
            const w = JSON.parse(response.body);
           
            const result = {
                weather: w.weather[0].id,
                weather_icon: `http://openweathermap.org/img/wn/${w.weather[0].icon}@2x.png`,
        
                temp: Math.round((w.main.temp - 273.15)*10)/10,
                temp_min: Math.round((w.main.temp_min - 273.15)*10)/10,
                temp_max: Math.round((w.main.temp_max - 273.15)*10)/10
            };
            
            conn.send(result);
        });
    }


    getForecast(conn) {
        const options = this.#buildOption('https://api.openweathermap.org/data/2.5/forecast', conn.body.lat, conn.body.lon);
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
            const result = [];

            const json = JSON.parse(response.body);
            for(var i in json.list) {
                const w = json.list[i];
                const obj = {
                    temp: Math.round((w.main.temp - 273.15)*10)/10,
                    temp_min:  Math.round((w.main.temp_min - 273.15)*10)/10,
                    temp_max: Math.round((w.main.temp_max - 273.15)*10)/10,

                    weather: w.weather[0].id,
                    weather_icon: `http://openweathermap.org/img/wn/${w.weather[0].icon}@2x.png`,
                    date: w.dt_txt,
                };
                result.push(obj);
            };            
            conn.send(result);
        });
    }
}

module.exports = new NotificationGateway();