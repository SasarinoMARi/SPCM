const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');

const Gateway = require("./GatewayBase");
class NotificationGateway extends Gateway {
    constructor() { super() }

    getWeather(conn) {
        Gateway.authentication(conn, () => {
            let query = `SELECT *, (SELECT AVG(temp) FROM weather_log WHERE 
                            \`date\` = DATE(SUBDATE(NOW(), INTERVAL 1 DAY) AND 
                            \`time\` BETWEEN '08:55:00' AND '21:05:00')) AS temp_yesterday
                        FROM weather_log
                        ORDER BY \`date\` DESC LIMIT 1 `;
            Gateway.query(query, conn, (result) => {
                if (result.length > 0) conn.send(result[0]);
                else conn.send(null); 
            })
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