const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');
const weatherMapper = require('../WeatherMapper');

const Gateway = require("./../../GenericDataHelper/GatewayBase");
class NotificationGateway extends Gateway {
    constructor() { super() }

    getWeather(conn) {
        Gateway.authentication(conn, () => {
            try {
                weatherMapper.getCurrentWeather((result) => {
                    if (result.length > 0) conn.send(result[0]);
                    else conn.send(null); 
                });
            } catch (e) {
                connection.internalError();
            }
        });
    }

    getForecast(conn) {
        Gateway.authentication(conn, () => {
            let query = `SELECT * FROM forecast_map WHERE \`date\`>='${modules.time().format("YYYY-MM-DD")}' ORDER BY \`date\` ASC LIMIT 10`;
            Gateway.query(query, conn, (result) => {
                for(var i in result) {
                    result[i].date = modules.time(result[i].date).format("YYYY-MM-DD");
                }
                conn.send(result);
            })
        });
    }
}

module.exports = new NotificationGateway();