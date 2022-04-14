class Logger {
    #sql;
    #time;
    #fcm;

    constructor() {
        this.#sql = require('./database/sql');
        this.#time = require('./time');
        this.#fcm = require('./messaging/fcm')
    }
    
    /**
     * 
     * @param {int} id 로그 중요도
     * @param {string} subject 로그 분류
     * @param {string} content 로그 내용
     * @param {string} ip (Optional) 기록 아이피
     */
    #__general_logging__(id, subject, content, ip) {
        var query = "INSERT INTO `log` (created_at, `level`, subject, content, `from`) VALUES (?,?,?,?,?)";
        var param = [this.#time().format("YYYY-MM-DD HH:mm:ss"), id, subject, content, ip];
        this.#sql.query(query, param, function(err, results, fields) {
            if(err) {
                console.log(err);
            }
        });
    }

    verbose         (subject, content, ip) { this.#__general_logging__(0, subject, content, ip) }
    debug           (subject, content, ip) { this.#__general_logging__(1, subject, content, ip) }
    info            (subject, content, ip) { this.#__general_logging__(2, subject, content, ip) }
    warning         (subject, content, ip) { this.#__general_logging__(3, subject, content, ip); this.#fcm.send(subject, content) }
    error           (subject, content, ip) { this.#__general_logging__(4, subject, content, ip); this.#fcm.send(subject, content) }
    critical        (subject, content, ip) { this.#__general_logging__(5, subject, content, ip); this.#fcm.send(subject, content) }
    
    destroy         ()                     { sql.destroy() }
}


module.exports = new Logger();