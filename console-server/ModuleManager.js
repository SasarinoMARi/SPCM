class ModuleManager {
    temperature;
    scheduler;
    shell;
    desktop;
    token_manager;
    secret;
    logger;
    sql;
    notifier;
    iptime;
    log;
    time;

    constructor() { 
        this.temperature = require('./temperature');
        this.scheduler = require('./scheduler');
        this.shell = require('shelljs');
        this.desktop = require('./desktop-api');
        this.token_manager = require('../common/token-manager');
        this.secret = require('../common/secret');
        this.notifier = require('./messaging/notifier');
        this.iptime = require("./iptime-api");
        
        this.sql = require('../GenericDataHelper/Sql.js').instance();
        this.log = require("../GenericDataHelper/Logger").instance();
        this.time = require("../GenericDataHelper/Time");
    }

}

module.exports = new ModuleManager();