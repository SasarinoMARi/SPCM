const shell = require('shelljs');

module.exports = {
    getTemp : function() {
        var temp = "ERROR";
        try {
            const {stdout, stdin, code} = shell.exec("vcgencmd measure_temp", {silent : true});
            temp = stdout.slice(5).replace('\'C', '').trim();
        } catch(e) {

        }
        return temp;
    }
}