-- ----------------------------------------------------------------------
-- SQL create script
-- ----------------------------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `posemuckel`;

CREATE DATABASE `posemuckel`;

-- -------------------------------------
-- Tables

CREATE TABLE `posemuckel`.`user` (
  `nickname` VARCHAR(20) NOT NULL,
  `LastName` VARCHAR(20) NULL,
  `firstName` VARCHAR(20) NULL,
  `email` VARCHAR(60) NOT NULL,
  `user_comment` TEXT NULL,
  `password` VARCHAR(20) NOT NULL,
  `user_ip` VARCHAR(15) NOT NULL,
  `lang` VARCHAR(2) NULL DEFAULT 'EN',
  `gender` ENUM('MALE','FEMALE') NULL,
  `location` VARCHAR(50) NULL,
  `hash` VARCHAR(32) NOT NULL,
  `logged_in` BOOLEAN NOT NULL,
  PRIMARY KEY (`nickname`)
);

CREATE TABLE `posemuckel`.`user_urls` (
  `user_url_id` INT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_nickname` VARCHAR(20) NOT NULL,
  `project_id` INT(20) UNSIGNED NOT NULL,
  `url_timestamp` TIMESTAMP NOT NULL,
  `referred_by_url` INT(20),
  `url_id` INT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`user_url_id`),
  CONSTRAINT `visited_by` FOREIGN KEY `visited_by` (`user_nickname`)
    REFERENCES `posemuckel`.`user` (`nickname`) ON DELETE CASCADE,
  CONSTRAINT `project_urls` FOREIGN KEY `project_urls` (`project_id`)
    REFERENCES `posemuckel`.`projects` (`project_id`) ON DELETE CASCADE,
  CONSTRAINT `address` FOREIGN KEY `address` (`url_id`)
    REFERENCES `posemuckel`.`url` (`url_id`) ON DELETE CASCADE
);

CREATE TABLE `posemuckel`.`ratings` (
  `ratings_id` INT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` INT(20) UNSIGNED NOT NULL,
  `user_nickname` VARCHAR(20) NOT NULL,
  `rating` INT(1),
  `rating_notes` TEXT NULL,
  `url_id` INT(20) UNSIGNED NOT NULL,
  `rating_timestamp` TIMESTAMP NOT NULL,
  PRIMARY KEY (`ratings_id`),
  CONSTRAINT `is_rated` FOREIGN KEY `is_rated` (`url_id`)
    REFERENCES `posemuckel`.`url` (`url_id`),
  CONSTRAINT `project_ratings` FOREIGN KEY `project_ratings` (`project_id`)
    REFERENCES `posemuckel`.`projects` (`project_id`),
  CONSTRAINT `user_rating` FOREIGN KEY `user_rating` (`user_nickname`)
    REFERENCES `posemuckel`.`user` (`nickname`)
);

CREATE TABLE `posemuckel`.`chat_progress` (
  `progress_id` INT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `chat_id` INT(20) UNSIGNED NOT NULL,
  `phrase` TEXT NOT NULL,
  `progress_timestamp` TIMESTAMP NOT NULL,
  `user_nickname` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`progress_id`),
  CONSTRAINT `progress` FOREIGN KEY `progress` (`chat_id`)
    REFERENCES `posemuckel`.`chat` (`chat_id`),
  CONSTRAINT `talks` FOREIGN KEY `talks` (`user_nickname`)
    REFERENCES `posemuckel`.`user` (`nickname`)
);

CREATE TABLE `posemuckel`.`user_chat` (
  `chat_id` INT(20) UNSIGNED NOT NULL,
  `user_nickname` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`chat_id`, `user_nickname`),
  CONSTRAINT `participate` FOREIGN KEY `participate` (`user_nickname`)
    REFERENCES `posemuckel`.`user` (`nickname`) ON DELETE CASCADE,
  CONSTRAINT `members` FOREIGN KEY `members` (`chat_id`)
    REFERENCES `posemuckel`.`chat` (`chat_id`) ON DELETE CASCADE
);

CREATE TABLE `posemuckel`.`chat` (
  `chat_id` INT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `private_chat` BOOLEAN NOT NULL,
  `chat_owner` VARCHAR(20) NOT NULL,
  `chat_closed_by` VARCHAR(20) NULL,
  PRIMARY KEY (`chat_id`),
  CONSTRAINT `owned_by` FOREIGN KEY `owned_by` (`chat_owner`)
    REFERENCES `posemuckel`.`user` (`nickname`) ON DELETE CASCADE,
  CONSTRAINT `closed_by` FOREIGN KEY `closed_by` (`chat_closed_by`)
    REFERENCES `posemuckel`.`user` (`nickname`) ON DELETE CASCADE
);

CREATE TABLE `posemuckel`.`projects` (
  `project_id` INT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_chat` INT(20) UNSIGNED NOT NULL,
  `project_title` VARCHAR(50) NOT NULL,
  `project_description` TINYTEXT NULL,
  `count_members` INT(8) NOT NULL,
  `max_members` INT(8) NOT NULL,
  `project_owner` VARCHAR(20) NOT NULL,
  `project_type` ENUM('PRIVATE','PUBLIC') NOT NULL,
  `project_date` DATE NOT NULL,
  PRIMARY KEY (`project_id`),
  CONSTRAINT `Owner_Nickname` FOREIGN KEY `Owner_Nickname` (`project_owner`)
    REFERENCES `posemuckel`.`user` (`nickname`) ON DELETE CASCADE,
  CONSTRAINT `Project_ChatID` FOREIGN KEY `Project_ChatID` (`project_chat`)
    REFERENCES `posemuckel`.`chat` (`chat_id`) ON DELETE CASCADE
);

CREATE TABLE `posemuckel`.`project_invitedUser` (
  `project_id` INT(20) UNSIGNED NOT NULL,
  `invited_user` VARCHAR(20) NOT NULL,
  `invitation_confirm` BOOLEAN NULL,
  `invitation_answered` BOOLEAN NULL,
  PRIMARY KEY (`project_id`, `invited_user`),
  CONSTRAINT `User_Project` FOREIGN KEY `User_Project` (`project_id`)
    REFERENCES `posemuckel`.`projects` (`project_id`) ON DELETE CASCADE,
  CONSTRAINT `Visitor` FOREIGN KEY `Visitor` (`invited_user`)
    REFERENCES `posemuckel`.`user` (`nickname`) ON DELETE CASCADE
);

CREATE TABLE `posemuckel`.`members` (
  `project_id` INT(20) UNSIGNED NOT NULL,
  `user_nickname` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`project_id`, `user_nickname`),
  CONSTRAINT `is_member` FOREIGN KEY `is_member` (`user_nickname`)
    REFERENCES `posemuckel`.`user` (`nickname`) ON DELETE CASCADE,
  CONSTRAINT `has_members` FOREIGN KEY `has_members` (`project_id`)
    REFERENCES `posemuckel`.`projects` (`project_id`) ON DELETE CASCADE
);

CREATE TABLE `posemuckel`.`buddies` (
  `user_nickname` VARCHAR(20) NOT NULL,
  `buddy_nickname` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`user_nickname`, `buddy_nickname`),
  CONSTRAINT `has_buddies` FOREIGN KEY `has_buddies` (`user_nickname`)
    REFERENCES `posemuckel`.`user` (`nickname`) ON DELETE CASCADE,
  CONSTRAINT `is_buddy` FOREIGN KEY `is_buddy` (`buddy_nickname`)
    REFERENCES `posemuckel`.`user` (`nickname`) ON DELETE CASCADE
);

CREATE TABLE `posemuckel`.`url` (
  `url_id` INT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `address` TEXT NOT NULL,
  `title` TEXT NOT NULL,
  PRIMARY KEY (`url_id`)
);

CREATE TABLE `posemuckel`.`folders` (
  `folder_id` INT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` INT(20) UNSIGNED NOT NULL,
  `name` VARCHAR(30) NULL,
  `parent_folder` INT(20) NULL,
  `unsorted_folder` BOOLEAN NOT NULL,
  PRIMARY KEY (`folder_id`),
  CONSTRAINT `folder_project` FOREIGN KEY `folder_project` (`project_id`)
    REFERENCES `posemuckel`.`projects` (`project_id`) ON DELETE CASCADE  
);

CREATE TABLE `posemuckel`.`folder_urls` (
  `folder_id` INT(20) UNSIGNED NOT NULL,
  `url_id` INT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`folder_id`,`url_id`),
  CONSTRAINT `result_folder` FOREIGN KEY `result_folder` (`folder_id`)
    REFERENCES `posemuckel`.`folders` (`folder_id`) ON DELETE CASCADE,
  CONSTRAINT `folder_url` FOREIGN KEY `folder_url` (`url_id`)
    REFERENCES `posemuckel`.`url` (`url_id`) ON DELETE CASCADE
);

CREATE TABLE `posemuckel`.`log` (
  `project_id` INT(20) UNSIGNED NOT NULL,
  `text` TEXT NOT NULL,
  `timestamp` TIMESTAMP NOT NULL,
  PRIMARY KEY (`project_id`, `timestamp`),
  CONSTRAINT `log_project` FOREIGN KEY `log_project` (`project_id`)
    REFERENCES `posemuckel`.`projects` (`project_id`) ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;
