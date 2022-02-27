/*
 * FCM 알림
 */

require("dotenv").config();
const request = require('request');
const fs = require('fs');
const idFilePath = './fcm_id'

var admin = require("firebase-admin");
var serviceAccount = require("./firebaseAccountKey.json");
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

module.exports = {
    send : function (title, content, callback) {
        if(!fs.existsSync(idFilePath)) {
            callback.error("FCM ID가 정의되지 않았습니다.");
        }
        const deviceToken = fs.readFileSync(idFilePath, 'utf-8');

        const message = {
            token: deviceToken,
            notification: {
                title: title,
                body: content
            }
        };

        admin
          .messaging()
          .send(message)
          .then(function(response) {
              console.log(response);
            callback.success(response);
          })
          .catch(function(err) {
            callback.error(err);
          });
    },
    update_id : function(id) {
        fs.writeFileSync(idFilePath, id, 'utf8');
    }
}