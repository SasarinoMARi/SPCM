const log_header = 'MorningCall.js';
const modules = require('./ModuleManager');
const list = [
    "https://www.youtube.com/watch?v=oy6MDr6I6rM",
];

class MorningCall {
    constructor() { }

    getMornincall() {
        if (!list || list.length == 0)
            return null;
            
        let i = this.#getRandomInt(0, list.length);
        return list[i];
    }

    #getRandomInt(min, max) {
        min = Math.ceil(min);
        max = Math.floor(max);
        return Math.floor(Math.random() * (max - min)) + min; //최댓값은 제외, 최솟값은 포함
    }
}

module.exports = new MorningCall();