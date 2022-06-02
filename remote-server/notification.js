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
                callback(body.replace(/\"/g, ''));
            }
            else {
                console.log("error: " + statusCode);
            }
        }
    });
}
function sendFcm(token, title, content) {
    const options = {
        uri: `${process.env.PI_SERVER}/noti/send_fcm`,
        headers: {
          'token': token
        },
        form: {
            title: title,
            body: content
        }
    };


    console.log(options);
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

function sendMail(token, title, content) {
    const options = {
        uri: `${process.env.PI_SERVER}/noti/send_mail`,
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

module.exports = {
    sendFcm : function(title, content) {
        establishment(function(token) {
            sendFcm(token, title, content);
        });
    },
    sendMail : function(title, content) {
        establishment(function(token) {
            sendMail(token, title, content);
        });
    }
}