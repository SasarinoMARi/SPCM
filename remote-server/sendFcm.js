const request = require('request');
require("dotenv").config();

function establishment(callback) {
    const options = {
        uri: `${process.env.PI_SERVER}/establishment`,
        headers: {
          'key': require('../common/secret').pi_key
        }
    };

    request.get(options, function (error, response, body) {
        if (error) {
            console.log("error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;

            if (statusCode == 200) {
                callback(body);
            }
            else {
                console.log("error: " + statusCode);
            }
        }
    });
}
function sendFcm(token, title, content) {
    const options = {
        uri: `${process.env.PI_SERVER}/fcm_send`,
        headers: {
          'token': token
        },
        form: {
            title: title,
            body: content
        }
    };

    request.post(options, function (error, response, body) {
        if (error) {
            console.log("error : " + error);
        }
        else {
            const statusCode = response && response.statusCode;

            if (statusCode == 200) {
                console.log("success");
            }
            else {
                console.log("error: " + statusCode);
            }
        }
    });
}

establishment(function(token) {
    sendFcm(token, "알림 테스트!", "데스크탑에서 보내는 겁니다..");
});