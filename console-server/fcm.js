/*
 * FCM 알림
 */

require("dotenv").config();
const request = require('request');
const fs = require('fs');
const idFilePath = './fcm_id'

module.exports = {
    send : function (title, content, callback) {
        if(!fs.existsSync(idFilePath)) {
            callback.error("FCM ID가 정의되지 않았습니다.");
        }

        const id = fs.readFileSync(idFilePath, 'utf-8');
        const options = {
            uri: `https://fcm.googleapis.com/fcm/send/`,
            headers: {
              'Authorization': process.env.FCM_TOKEN
            },
            body: {
                to: id,
                priority: 'high',
                notification: {
                    title: title,
                    body: content
                }
            }
        };

        request.post(options, function (error, response, body) {
            if (error) {
                logger.e("fcm error : " + error);
            }
            else {
                const statusCode = response && response.statusCode;

                if (statusCode == 200) {
                    let json = JSON.parse(body);
                    callback.success(json.token);
                }
                else {
                    callback.error();
                }
            }
        });
    },
    update_id : function(id) {
        fs.writeFileSync(idFilePath, id, 'utf8');
    }
}