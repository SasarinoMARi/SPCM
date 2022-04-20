class Logger {
    #sql;
    #time;
    #notifier;

    constructor() {
        this.#sql = require('./database/sql');
        this.#time = require('./time');
        this.#notifier = require('./messaging/notifier');
    }
    
    /**
     * 
     * @param {int} id 로그 중요도
     * @param {string} subject 로그 분류
     * @param {string} content 로그 내용
     * @param {string} ip (Optional) 기록 아이피
     */
    #general_logging(level, subject, content, ip) {
        if(level >= 3) console.log(`[${subject}] ${content}`);
        var query = `INSERT INTO \`log\` (created_at, \`level\`, subject, content, \`from\`) VALUES (?,?,?,${this.#sql.escape(content)},?)`;
        var param = [this.#time().format("YYYY-MM-DD HH:mm:ss"), level, subject, ip];
        this.#sql.query(query, param, function(err, results, fields) {
            if(err) {
                console.log(err);
            }
        });
    }

    #notifyToAdmin(level, subject, text, ip) {
        var title = `서버에 문제가 발생했습니다.`;
        var content = `보안 주체: ${subject}\n발생 날짜: ${this.#time().format("YYYY-MM-DD HH:mm:ss")}\n\n${text}\n\n기록된 아이피: ${ip}`;
        this.#notifier.sendBoth(title, content);
    }

    log             (level, subject, content, ip) { this.#general_logging(level, subject, content, ip); }
    verbose         (subject, content, ip) { this.#general_logging(0, subject, content, ip); }
    debug           (subject, content, ip) { this.#general_logging(1, subject, content, ip); }
    info            (subject, content, ip) { this.#general_logging(2, subject, content, ip); }
    warning         (subject, content, ip) { this.#general_logging(3, subject, content, ip); this.#notifyToAdmin(3, subject, content, ip) }
    error           (subject, content, ip) { this.#general_logging(4, subject, content, ip); this.#notifyToAdmin(4, subject, content, ip) }
    critical        (subject, content, ip) { this.#general_logging(5, subject, content, ip); this.#notifyToAdmin(5, subject, content, ip) }
    
    destroy         ()                     { sql.destroy() }
}


module.exports = new Logger();