/**
 * DB에서 불러온 코드를 정해진 시간에 실행하는 스케줄러
 * 코드상에서 사용하지 않는 모듈도 eval에서 실행시킬 때 편하게 하기 위해 구태여 참조.
 * 
 * - command에서는 절대 경로만 사용할 것!
 * - loadSchedules 호출 후 코드 내에서 사용하는 모듈이 변경되더라도 재시작 전까지 반영되지 않음. 
 *   (eval에서 require한 코드만 해당. 2차적으로 참조하는 경우 괜찮을 듯)
 * 
 * 
 * Cron 표현식 참고용
 *  *    *    *    *    *    *
 *  ┬    ┬    ┬    ┬    ┬    ┬
 *  │    │    │    │    │    │
 *  │    │    │    │    │    └ day of week (0 - 7) (0 or 7 is Sun)
 *  │    │    │    │    └───── month (1 - 12)
 *  │    │    │    └────────── day of month (1 - 31)
 *  │    │    └─────────────── hour (0 - 23)
 *  │    └──────────────────── minute (0 - 59)
 *  └───────────────────────── second (0 - 59, OPTIONAL)
 */
const log_header = 'scheduler.js';
const cron = require('node-schedule');
const log = require('../GenericDataHelper/Logger').instance();
const sql = require('../GenericDataHelper/Sql').instance();

const notifier = require('./messaging/notifier')
const shell = require('shelljs');
const twitter = require('./twitter');

class Scheduler {
    constructor() { }
    loadSchedules() {
        cron.gracefulShutdown();

        console.log("Start fetching schedules...");
        sql.query('SELECT * FROM schedule', function(err, schedules, fields) {
            if(err) {
                log.error(log_header, `Error fetching schedule list: ${err.sqlMessage}`);
                return;
            }
            log.info(log_header, `Fetching schedule complete! ${schedules.length} schedules is ready.`);
            schedules.forEach(schedule => {
                if(schedule.active == 1) {
                    console.log(`${schedule.name} : ${schedule.cron}  ${schedule.command}`);
                    cron.scheduleJob(schedule.cron, function() { 
                        log.verbose(log_header, `run schedule: ${schedule.name}`);
                        try{ eval(schedule.command); }
                        catch(e) { 
                            log.warning(log_header, `\nError running scheduled command : \n${schedule.command}\n\n${e}\n\n`);
                        }
                    });
                }
            });
        });
    }
}

module.exports = new Scheduler();