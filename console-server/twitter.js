require("dotenv").config();
const log = require('./logger');
const log_header = __filename;

class Twitter {
    #client; #time; #log; #sql;

    constructor() {
        this.#client = new require('twitter')({
            consumer_key: process.env.TWITTER_APP_TOKEN,
            consumer_secret: process.env.TWITTER_APP_SECRET,
            access_token_key: process.env.TWITTER_ACCESS_TOKEN,
            access_token_secret: process.env.TWITTER_ACCESS_SECRET
        });

        this.#time = require('./time');
        this.#sql = require('./database/sql');
    }
    
    /**
     * 트윗을 게시합니다.
     * @param {*} content 트윗할 내용
     * @param {*} callback optional, 트윗 후 실행할 코드 
     */
    publishTweet(content, callback) {
        this.#client.post('statuses/update', { status: content }, function (error, tweet, response) {
            if (!error) {
                if(!callback) callback();
            } else {
                this.#log.error(log_header, error);
            }
        }.bind(this));
    }

    startTweetCleaner() {
        this.#cleanerRecursive(this);
    }
    
    //#region 트윗청소기 구현

    #cleanerRecursive(lastExcludedTweetId) {
        // 대체 왜 screen_name으로 조회하게 짜놓은거임?
        // 잘 모르겠지만 잘 돌아가고.. 이제와서 고치기는 귀찮으니까 냅둠
        var req = { screen_name: process.env.TWITTER_MY_SCREENNAME, count: 20};
        if(lastExcludedTweetId != undefined) req.max_id = lastExcludedTweetId;

        this.#client.get('statuses/user_timeline', req, function (error, tweets, response) {
            if (error) {
                log.error(log_header, JSON.stringify(error));
                return;
            }
    
            // 한 개 이상의 트윗을 가져왔는지 확인
            // request에 유효한 max_id가 있으면 무조건 한 개는 돌아오므로 예외처리
            var empty = (lastExcludedTweetId != undefined) ? 1 : 0;
            if(tweets.length > empty) {
                this.#destroyRecursive(tweets, function(id) {
                    this.#cleanerRecursive(id);
                }.bind(this));
            }
            else {
                this.#onFinishedCleaner();
            }
        }.bind(this));
    }
    
    /**
     * 트윗 삭제하는 재귀함수
     * 1. 트윗 배열을 전달받음
     * 2. 삭제 대상이라면 삭제
     * 3. 배열의 끝까지 재귀하면 lastExcludedTweetId를 인자로 callback 호출
     * 
     * required
     * @param {Array<Tweet>} tweets 트윗 배열
     * @param {function(Int)} callback 배열 다 털었을 때 호출할 함수
     * 
     * optional
     * @param {Int} i 현재 배열 인덱스
     * @param {Int} lastExcludedTweetId 가장 오래된 삭제 대상이 아닌 트윗의 id
     * 
     * @returns -
     */
    #destroyRecursive(tweets, callback, lastExcludedTweetId, i) {
        i=!i?0:i;

        if(tweets.length <= i) {
            callback(lastExcludedTweetId);
            return;
        }

        var tweet = tweets[i];
        if(this.#isExcludeTweet(tweet)) {
            lastExcludedTweetId = tweet.id_str;
            this.#destroyRecursive(tweets, callback, lastExcludedTweetId, i+1);
        }
        else {
            this.#client.post(`statuses/destroy/${tweet.id_str}.json`, function(error) {
                if (error) {
                    log.debug(log_header, error);
                }
                else {
                    this.#writeCleanerLog(tweet);
                }
                this.#destroyRecursive(tweets, callback, lastExcludedTweetId, i+1);
            }.bind(this));
        }
    }
    
    /**
     * 삭제하면 안되는 트윗인지 확인하는 함수
     * @param {Tweet} tweet 판별 대상 트윗
     * @returns 지우지 않을 트윗이면 true
     */
    #isExcludeTweet(tweet) {
        var result = false;

        // 내가 마음찍은 내 트윗 제외
        if(tweet.retweeted_status == undefined) {
            if(tweet.favorited == true) {
                result = true;
            }
        }
    
        // 일정 시간 지난 트윗만 삭제
        var time_cut = 1000*60*60*2;
        if(Date.parse(tweet.created_at)+time_cut > this.#time()) {
            result = true;
        }
        
        return result;
    }
    
    #writeCleanerLog(tweet) {
        if(!tweet) return;
        const text = tweet.text.replace('\'', ('\\\''));
        if(!text) return;
        const is_retweet = tweet.retweeted;
        const is_mention = !is_retweet && text.includes("@");
        const created_at = this.#time(new Date(tweet.created_at)).format("YYYY-MM-DD HH:mm:ss");
        const destroyed_at = this.#time().format("YYYY-MM-DD");
        const retweet_count = !is_retweet?tweet.retweet_count:null;
        const favorite_count = !is_retweet?tweet.favorite_count:null;

        var query = `INSERT INTO \`destroyed_tweet\` (text, is_mention, is_retweet, created_at, destroyed_at, retweet_count, favorite_count) 
                     VALUES (${this.#sql.escape(text)},${is_mention},${is_retweet},'${created_at}','${destroyed_at}',${retweet_count},${favorite_count})`;
        this.#sql.query(query, function(err, results, fields) {
            if(err) {
                console.log(query);
                log.debug(log_header, err.sqlMessage);
            }
        });
    }

    #onFinishedCleaner() {
        // console.log("트윗 청소 완료!");
    }


    //#endregion
}


module.exports = new Twitter();