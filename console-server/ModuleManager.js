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

    constructor() { 
        this.temperature = require('./temperature');
        this.scheduler = require('./scheduler');
        this.shell = require('shelljs');
        this.desktop = require('./desktop-api');
        this.token_manager = require('../common/token-manager');
        this.secret = require('../common/secret');
        this.logger = require('./logger');
        this.sql = require('./database/sql.js');
        this.notifier = require('./messaging/notifier');
        this.iptime = require("./iptime-api");
        this.log = require("./logger");
    }

}

module.exports = new ModuleManager();