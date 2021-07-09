const fs = require('fs');
const time = require('./time');

function appendLogFile(msg) {
    var fn = `logs/${time().format("YYYY-MM-DD")}.log`
    fs.appendFileSync(fn, `${msg}\n`);
}

module.exports = {
    v: function (str) {
        var msg = `[${time().format("HH:mm:ss")}] Verbose :  ${str}`;
        console.log(msg);
        appendLogFile(msg);
    },
    e: function (str) {
        var msg = `[${time().format("HH:mm:ss")}] Error :  ${str}`;
        console.log(msg);
        appendLogFile(msg);
    },
    d: function (str) {
        var msg = `[${time().format("HH:mm:ss")}] Debug :  ${str}`;
        console.log(msg);
        appendLogFile(msg);
    }
}