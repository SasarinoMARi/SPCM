const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');

const Gateway = require("./DataGateway");
class FoodGateway extends Gateway {
    constructor() { super("food") }
}

module.exports = new FoodGateway();