const logger = require("./logger")

expirationPeriod = 1000 * 60 * 30; // 30 minute
tokenLength = 256;

class token {
    constructor(){
        // Make token
        var t           = '';
        var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        var charactersLength = characters.length;
        for ( var i = 0; i < tokenLength; i++ ) {
            t += characters.charAt(Math.floor(Math.random() * charactersLength));
        }
        this.token = t;

        // Set expire date
        this.expireDate = new Date(new Date().getTime() + expirationPeriod)
    }
}

class tokenManager {
    storage = [];

    constructor() {
        this.__removeExpiredToken();
    }

    /**
     * 유효기한 지난 토큰 리스트에서 삭제
     */
    __removeExpiredToken() {
        setTimeout(() => {
            d = new Date();
            this.storage = this.storage.filter(function(t){
                return t.expireDate - d > 0
            });
            this.__removeExpiredToken();
          }, 1000 * 60 * 10);
    }

    /**
     * 새 토큰 발급
     */
    new() {
        let t = new token();
        this.storage.push(t);
        return t.token;
    }

    /**
     * 토큰이 유효한지 확인
     * @param {string} token 확인할 토큰 값
     */
    contains(token) {
        for(var i= 0; i< this.storage.length; i++) {
            let t = this.storage[i];
            if(t.token === token) {
                if(t.expireDate - new Date() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
};

module.exports = new tokenManager();