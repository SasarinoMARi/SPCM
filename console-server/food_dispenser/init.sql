create database food_dispenser;

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