var nodemailer = require('nodemailer');
require("dotenv").config();

var transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: process.env.MAIL_SENDER_ID,
        pass: process.env.MAIL_SENDER_PW
    }
});

module.exports = {
    send: function (title, body) {
        // 지메일 legacy deprecated에 따른 임시 처리..
        if (true) {
            return;
        }

        var mailOptions = {
            from: `"나루" ${process.env.MAIL_SENDER_ID}`,
            to: process.env.MAIL_RECEIVER_ID,
            subject: title,
            text: body
        };

        transporter.sendMail(mailOptions, function (error, info) {
            if (error) {
                console.log(error);
            } else {
                console.log('Email sent: ' + info.response);
            }

        });
    }
}