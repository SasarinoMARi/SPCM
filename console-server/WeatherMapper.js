const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('./ModuleManager');
require("dotenv").config();
const request = require('request');
const { forever, initParams } = require('request');

const key = process.env.OPENWEATHERMAP_API_KEY;
// 낙성대역 기본 좌표
const default_lat = 37.477050;
const default_lon = 126.963572;

class WeatherMapper {
    #buildOption(url, lat, lon) {
        return {
            uri: `${url}?lat=${lat}&lon=${lon}&appid=${key}`
        }
    }

    #buildLocation(lat, lon) {
        const result = {};
        if(!lat || !lon){
            result.lat = default_lat;
            result.lon = default_lon;
        } else {
            result.lat = lat;
            result.lon = lon;
        }
        return result;
    }

    /**
     * 배열의 특정 값을 기준으로 배열을 분류함(groupBy)
     * @param {*} data 분류할 데이터 array
     * @param {*} key 분류 기준으로 삼을 변수
     * @returns key를 기준으로 분류된 array
     */
    #groupBy(data, key) {
        return data.reduce(function (carry, el) {
            var group = key(el);
    
            carry[group] = carry[group] || [];
            carry[group].push(el);
            return carry
        }, {})
    }

    /**
     * 날씨 아이콘 코드로 대략적인 날씨 매핑
     * @param {*} icon 
     * @returns 
     */
    #getWeatherCodeByIcon(icon) {
        if(!icon) return null;
        if(icon.startsWith('01')) return 800
        else if(icon.startsWith('02')) return 801
        else if(icon.startsWith('03')) return 802
        else if(icon.startsWith('04')) return 803
        else if(icon.startsWith('09')) return 300
        else if(icon.startsWith('10')) return 500
        else if(icon.startsWith('11')) return 200
        else if(icon.startsWith('13')) return 600
        else if(icon.startsWith('50')) return 701
    }

    #fetchForecast(location, callback) {
        const options = this.#buildOption('https://api.openweathermap.org/data/2.5/forecast', location.lat, location.lon);
        request.get(options, (error, response, body) => {
            if (error) {
                modules.log.error(LOG_SUBJECT, error);
                return;
            } else if(response.statusCode != 200) {
                let logMsg = `Response status code is not 200\n[${response.statusCode}] ${response.statusMessage}`;
                modules.log.error(LOG_SUBJECT, logMsg);
                return;
            } else if (!response.body) {
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
                    temp_min: Math.round((w.main.temp_min - 273.15)*10)/10,
                    temp_max: Math.round((w.main.temp_max - 273.15)*10)/10,

                    weather: w.weather[0].id,
                    weather_icon: w.weather[0].icon,
                    date: w.dt_txt,
                };
                result.push(obj);
            };        
            callback(result);
        });
    }

    /**
     * forecast가 하루를 8등분해서 돌아오기 때문에 이걸 그룹화하는 작업이 필요
     * @param {*} forecasts 
     * @param {*} callback 
     */
    #categoryForecast(forecasts, callback) {
        const result = [];
        const group = this.#groupBy(forecasts, x=>x.date.substring(0,10));
        for(var key in group) {
            const day = group[key];
            
            const item = {};
            item.date = key;

            var temp = 0, temp_cnt = 0;
            var temp_min = undefined, temp_max = undefined, weather = undefined;
            var weather_ratio = new Map();
            for(var i in day) {
                var w = day[i];
                
                // 심야와 새벽 시간대를 제외
                const hour = w.date.substring(10).trim().split(':')[0];
                if(!hour || hour > 21 || hour < 9) continue;

                temp += w.temp;
                temp_cnt++;
                if(!temp_min || w.temp_min < temp_min) temp_min = w.temp_min;
                if(!temp_max || w.temp_max > temp_max) temp_max = w.temp_max;

                // 우선순위에 따라 날씨 배정
                if(!weather) weather = "";
                if(!weather.startsWith('11')) {
                    if(w.weather_icon.startsWith('11')) weather = w.weather_icon; // 뇌우
                    else if(!weather.startsWith('10')) {
                        if(w.weather_icon.startsWith('10')) weather = w.weather_icon; // 비
                        else if(!weather.startsWith('9')) {
                            if(w.weather_icon.startsWith('9')) weather = w.weather_icon; // 이슬비
                            else if(!weather.startsWith('13')) {
                                if(w.weather_icon.startsWith('13')) weather = w.weather_icon; // 눈
                                else {
                                    // 주요 날씨 없을 경우 많이 나오는 날씨로 설정
                                    var wt = w.weather_icon;
                                    if(weather_ratio.has(wt)) weather_ratio.set(wt, (weather_ratio.get(wt) + 1));
                                    else weather_ratio.set(wt, 1);
                                }
                            }    
                        }
                    }
                }                
            }

            item.temp = temp/temp_cnt;
            item.temp_min = temp_min;
            item.temp_max = temp_max;

            // 주요 날씨가 없을 경우 과반수 날씨로 설정
            if(weather == "") {
                const keys = Array.from(weather_ratio.keys());
                const values = Array.from(weather_ratio.values())
                var max = Math.max(...values);
                var i = values.indexOf(max);
                var maxicon = keys[i];
                weather = maxicon;
            }
            item.icon = weather;
            item.weather = this.#getWeatherCodeByIcon(weather);
            result.push(item);
        }
        callback(result);
    }

    #recordForecast(location, forecasts, callback) {
        const param = [];
        for(var i in forecasts) {
            var weather = forecasts[i];

            if (!weather.weather || !weather.icon || !weather.temp || !weather.temp_min || !weather.temp_max)
                continue;

            param.push([
                location.lat, location.lon,
                weather.date,
                weather.weather, weather.icon,
                weather.temp, weather.temp_min, weather.temp_max
            ]);
        }

        const query = 'REPLACE INTO `forecast_map` (`lat`, `lon`, `date`, `weather`, `icon`, `temp`, `temp_min`, `temp_max`) VALUES ?;';
        modules.sql.query(query, [param], function(error, results, fields) {
            if (error) {
                const content = `SQL 에러가 발생했습니다.\n${error.sqlMessage}\n\n쿼리: ${query}`;
                modules.log.sqlError(LOG_SUBJECT, content);
                return;
            }
        });
    }


    #fetchWeather(location, callback) {
        const options = this.#buildOption('https://api.openweathermap.org/data/2.5/weather', location.lat, location.lon);
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
                icon: w.weather[0].icon,
        
                temp: Math.round((w.main.temp - 273.15)*10)/10,
                temp_min: Math.round((w.main.temp_min - 273.15)*10)/10,
                temp_max: Math.round((w.main.temp_max - 273.15)*10)/10
            };

            callback(result);
        });
    }

    #recordWeather(location, weather, callback) {
        const param = [[
            location.lat, location.lon,
            modules.time().format("YYYY-MM-DD"),
            modules.time().format("HH:mm:ss"),
            weather.weather, weather.icon,
            weather.temp, weather.temp_min, weather.temp_max
        ]];

        const query = 'INSERT INTO `weather_log` (`lat`, `lon`, `date`, `time`, `weather`, `icon`, `temp`, `temp_min`, `temp_max`) VALUES ?;';
        modules.sql.query(query, [param], function(error, results, fields) {
            if (error) {
                const content = `SQL 에러가 발생했습니다.\n${error.sqlMessage}\n\n쿼리: ${query}`;
                modules.log.sqlError(LOG_SUBJECT, content);
                return;
            }
        });
    }
    
    /**
     * DB에 일기예보 기록
     */
    mapForecast() {
        const location = this.#buildLocation(undefined, undefined);
        this.#fetchForecast(location, forecast => {
            this.#categoryForecast(forecast, weathers => {
                this.#recordForecast(location, weathers);
            });
        });
    }

    /**
     * DB에 현재 날씨 기록
     */
    mapWeather() {
        const location = this.#buildLocation(undefined, undefined);
        this.#fetchWeather(location, weather => {
            this.#recordWeather(location, weather);
        });
    }

    /**
     * DB에서 현재 날씨를 가져오는 함수.
     * sql 에러시 exception
     * @param {*} callback 
     */
    getCurrentWeather(callback) {
        let query = `SELECT *, (SELECT AVG(temp) FROM weather_log WHERE 
                        \`date\` = DATE(SUBDATE(NOW(), INTERVAL 1 DAY) AND 
                        \`time\` BETWEEN '08:55:00' AND '21:05:00')) AS temp_yesterday
                    FROM weather_log
                    ORDER BY idx DESC LIMIT 1 `;

        modules.sql.query(query, (error, result, fields) => {
            if (error) {
                const content = `SQL 에러가 발생했습니다.\n${error.sqlMessage}\n\n쿼리: ${query}`;
                console.log(content);
                modules.log.sqlError(LOG_SUBJECT, content);
                throw 'sqlError';
            }

            callback(result);
        });
    }

    getWeatherString(code) {
        if (200 <= code && code < 300) return "뇌우";
        else if (300 <= code && code < 310) return "이슬비";
        else if (310 <= code && code < 400) return "가랑비";
        else if (500 <= code && code < 505) return "비";
        else if (code == 511) return "우박"
        else if (520 <= code && code < 600) return "소나니";
        else if (600 <= code && code < 613) return "눈";
        else if (613 <= code && code < 617) return "진눈깨비";
        else if (620 <= code && code < 622) return "소낙눈";
        else if (code == 622) return "폭설";
        else if (code == 701) return "옅은 안개";
        else if (code == 711) return "안개";
        else if (code == 721) return "짙은 안개";
        else if (731 <= code && code < 762) return "먼지";
        else if (code == 762) return "화산재";
        else if (771 <= code && code < 782) return "태풍";
        else if (code == 800) return "쾌청";
        else if (code == 801) return "살짝 흐림";
        else if (code == 802) return "흐림";
        else if (code == 803) return "꽤 흐림";
        else if (code == 804) return "매우 흐림";
        else return `알 수 없는 날씨 코드 ${code}`;
    }

    getClothString(temp) {
        if (temp >= 27) return "반팔, 민소매 / 반바지"
        else if (temp >= 23) return "반팔, 얇은 셔츠 / 얇은 바지"
        else if (temp >= 20) return "얇은 가디건, 긴팔티 / 청바지"
        else if (temp >= 17) return "얇은 니트, 맨투맨, 얇은 재킷 / 청바지"
        else if (temp >= 12) return "자켓, 야상, 니트 /두꺼운 바지"
        else if (temp >= 9) return "트렌치코트, 야상, 니트"
        else if (temp >= 5) return "코드, 히트텍, 니트"
        else return "패딩, 두꺼운 코트, 목도리"
    }
    
}

module.exports = new WeatherMapper();

