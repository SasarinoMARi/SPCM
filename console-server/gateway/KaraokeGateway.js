const LOG_SUBJECT = require('path').basename(__filename);
const modules = require('../ModuleManager');

const Gateway = require("./../../GenericDataHelper/DataGateway");
class KaraokeGateway extends Gateway {
    constructor() { super("karaoke") }
}

module.exports = new KaraokeGateway();