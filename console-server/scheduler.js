/**
 * DB에서 불러온 코드를 정해진 시간에 실행하는 스케줄러
 * 코드상에서 사용하지 않는 모듈도 eval에서 실행시킬 때 편하게 하기 위해 구태여 참조.
 * 
 * command에서는 절대 경로만 사용할 것!
 */
const log_header = 'scheduler.js';
const log = require('./logger');
const cron = require('node-schedule');
const sql = require('./database/sql');
const notifier = require('./messaging/notifier')
const shell = require('shelljs');

class Scheduler {
    constructor() { }
    loadSchedules() {
        cron.cancelJob();

        console.log("Start fetching schedules...");
        sql.query('SELECT * FROM schedule', function(err, schedules, fields) {
            if(err) {
                log.error(log_header, `Error fetching schedule list: ${err}`);
                return;
            }
            console.log(`Fetching schedule complete! ${schedules.length} schedules is ready.`);
            schedules.forEach(schedule => {
                if(schedule.active == 1) {
                    console.log(`${schedule.cron}  ${schedule.command}`);
                    cron.scheduleJob(schedule.cron, function() { 
                        log.verbose(log_header, `run schedule: ${schedule.command}`);
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