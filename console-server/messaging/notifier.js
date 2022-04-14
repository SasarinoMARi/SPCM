class Notifier {
    #fcm
    #email

    constructor() {
        this.#fcm = require('./fcm');
        this.#email = require('./email');
    }

    sendFcm(title, content, callback) { this.#fcm.send(title, content, callback); }
    sendEmail(title, content, callback) { this.#email.send(title, content, callback); }
    sendBoth(title, content, callback) {
        this.sendFcm(title, content, callback)
        this.sendEmail(title, content, callback)
    }

    update_fcm_id(id) { this.#fcm.update_id(id); }
}

module.exports = new Notifier();