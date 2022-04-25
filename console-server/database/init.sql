create database spcm;

CREATE TABLE `log` (
	`idx` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '고유번호',
	`created_at` DATETIME NOT NULL COMMENT '기록 날짜',
	`level` TINYINT NOT NULL COMMENT '로그 레벨',
	`subject` VARCHAR(128) NOT NULL COMMENT '분류',
	`content` TEXT NOT NULL COMMENT '로그 내용',
	`from` VARCHAR(128) NULL DEFAULT NULL COMMENT '기록 아이피',
	PRIMARY KEY (`idx`)
)
COLLATE='utf8_general_ci';

CREATE TABLE `schedule` (
	`idx` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '고유번호',
	`active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '활성화 여부',
	`name` TEXT NOT NULL COMMENT '작업 이름',
	`cron` VARCHAR(128) NOT NULL COMMENT '실행할 시간(cron)',
	`command` TEXT NOT NULL COMMENT '실행할 명령어',
	`created_at` DATETIME NOT NULL COMMENT '기록 날짜',
	PRIMARY KEY (`idx`)
)
COLLATE='utf8_general_ci';

CREATE TABLE `timeline` (
	`idx` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '고유번호',
	`created_at` DATETIME NOT NULL NULL COMMENT '기록 날짜',
	`subject` VARCHAR(128) NOT NULL COMMENT '분류',
	`title` VARCHAR(128) NOT NULL COMMENT '제목',
	`url` TEXT NOT NULL COMMENT '링크',
	`thumbnail` TEXT NOT NULL COMMENT '섬네일 링크',
	`custom1` TEXT NOT NULL COMMENT '커스텀 칼럼 1',
	`custom2` TEXT NOT NULL COMMENT '커스텀 칼럼 2',
	PRIMARY KEY (`idx`)
)
COLLATE='utf8_general_ci';

CREATE TABLE `food` (
	`FoodId` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '고유번호',
	`Name` VARCHAR(100) NOT NULL COMMENT '메뉴명',
	`Store` VARCHAR(100) NOT NULL NULL COMMENT '매장명',
	`Price` INT UNSIGNED NOT NULL  COMMENT '가격',
	`Phone` VARCHAR(18) NOT NULL NULL COMMENT '전화번호',
	`Description` TEXT NULL DEFAULT NULL COMMENT '비고',
	`Disabled` TINYINT NOT NULL DEFAULT '0' COMMENT '비활성화 여부',
	`Thumbnail_URL` TEXT NULL DEFAULT NULL COMMENT '섬네일 주소',
	PRIMARY KEY (`FoodId`)
)
COLLATE='utf8_general_ci';

CREATE TABLE `destroyed_tweet` (
	`idx` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '고유번호',
	`text` TEXT NOT NULL COMMENT '본문',
	`is_mention` TINYINT NOT NULL DEFAULT '0' COMMENT '멘션인지 여부',
	`is_retweet` TINYINT NOT NULL DEFAULT '0' COMMENT '리트윗인지 여부',
	`created_at` DATETIME NOT NULL COMMENT '트윗 날짜',
	`destroyed_at` DATE NOT NULL COMMENT '삭제 날짜',
	`retweet_count` INT UNSIGNED NULL DEFAULT NULL COMMENT '리트윗 수',
	`favorite_count` INT UNSIGNED NULL DEFAULT NULL COMMENT '마음 수',
	PRIMARY KEY (`idx`)
)
COLLATE='utf8mb4_unicode_ci';

CREATE TABLE `header_image` (
	`idx` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '고유번호',
	`url` TEXT NOT NULL COMMENT 'url',
	`description` TEXT NULL DEFAULT NULL COMMENT '비고',
	PRIMARY KEY (`idx`)
)
COLLATE='utf8_general_ci';