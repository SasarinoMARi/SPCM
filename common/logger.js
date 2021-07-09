const fs = require('fs');
function appendLogFile(msg) {
    var fn = `logs/error_${new Date().toISOString().substring(0,10)}.log`
    fs.appendFileSync(fn, `${msg}\n`);
}

module.exports = {
    v: function (str) {
        var msg = `Verbose[${Date()}] ${str}`;
        console.log(msg);
        appendLogFile(msg);
    },
    e: function (str) {
        var msg = `Error[${Date()}] ${str}`;
        console.log(msg);
        appendLogFile(msg);
    },
    d: function (str) {
        var msg = `Debug[${Date()}] ${str}`;
        console.log(msg);
        appendLogFile(msg);
    }
}