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

CREATE TABLE `food_list` (
	`FoodId` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '고유번호',
	`Name` VARCHAR(100) NOT NULL COMMENT '메뉴명',
	`Store` VARCHAR(100) NOT NULL NULL COMMENT '매장명',
	`Price` INT UNSIGNED NOT NULL  COMMENT '가격',
	`Phone` VARCHAR(18) NOT NULL NULL COMMENT '전화번호',
	`Description` TEXT NULL DEFAULT NULL COMMENT '비고',
	`Disabled` TINYINT NOT NULL DEFAULT '0' COMMENT '비활성화 여부',
	PRIMARY KEY (`FoodId`)
)
COLLATE='utf8_general_ci';
