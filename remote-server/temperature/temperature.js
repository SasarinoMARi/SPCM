const util = require('util');
const exec = util.promisify(require('child_process').exec);

module.exports = {
    getTemp : async function() {
        var temp = "ERROR";
        try {
            const {stdout,stderr} = await exec("\"" + __dirname + "/CPUTemperature.exe\"");
            temp = stdout.trim();
            console.log(temp);
        } catch(e) {
            console.log(e);
        }
        return temp;
    }
}