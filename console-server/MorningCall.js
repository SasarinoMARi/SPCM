const morningcalls = [
    "https://www.youtube.com/watch?v=oy6MDr6I6rM",
    "https://www.youtube.com/watch?v=Z9DXOZUbmJ4&list=RDGMEMhCgTQvcskbGUxqI4Sn2QYw&start_radio=1&rv=XeI8E20ZUE4",
    "https://www.youtube.com/watch?v=siNFnlqtd8M",
];
const precalls = [
    "https://www.youtube.com/watch?v=nNIklOpkZ-A"
];

const log_header = 'MorningCall.js';
const modules = require('./ModuleManager');
const pc = require('./desktop-api');

class MorningCall {
    constructor() { }

    // 깨우기 전에 예열
    doPreCall() {
        const url = this.decidePreCall();

        if(!url)
            return;
            
        pc.volume("master", 30);
        pc.play("master", url);
    }

    doMorningCall() {
        const url = this.decideMorningCall();

        if(!url)
            return;

        pc.volume("master", 80);
        pc.play("master", url);
    }

    decidePreCall() {
        if (!precalls || precalls.length == 0)
            return null;
            
        let i = this.#getRandomInt(0, precalls.length);
        return precalls[i];
    }

    decideMorningCall() {
        if (!morningcalls || morningcalls.length == 0)
            return null;
            
        let i = this.#getRandomInt(0, morningcalls.length);
        return morningcalls[i];
    }

    #getRandomInt(min, max) {
        min = Math.ceil(min);
        max = Math.floor(max);
        return Math.floor(Math.random() * (max - min)) + min; //최댓값은 제외, 최솟값은 포함
    }
}

module.exports = new MorningCall();