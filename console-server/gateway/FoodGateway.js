const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');

const Gateway = require("./GatewayBase");
class FoodGateway extends Gateway {
    constructor() { super() }

    pickRandom(conn) {
        Gateway.authentication(conn, () => {
            let query = `SELECT * FROM food ORDER BY RAND() LIMIT 1`;
            Gateway.query(query, conn, (result) => {
                if (result.length > 0) conn.send(result[0]);
                else conn.send(null); 
            })
        });
    }
}

module.exports = new FoodGateway();