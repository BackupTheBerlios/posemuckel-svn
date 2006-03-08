-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	4.1.14


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema pose_test
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ pose_test;
USE pose_test;

--
-- Table structure for table `pose_test`.`buddies`
--

DROP TABLE IF EXISTS `buddies`;
CREATE TABLE `buddies` (
  `user_nickname` varchar(20) NOT NULL default '',
  `buddy_nickname` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`user_nickname`,`buddy_nickname`),
  KEY `is_buddy` (`buddy_nickname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`buddies`
--

/*!40000 ALTER TABLE `buddies` DISABLE KEYS */;
/*!40000 ALTER TABLE `buddies` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`chat`
--

DROP TABLE IF EXISTS `chat`;
CREATE TABLE `chat` (
  `chat_id` int(20) unsigned NOT NULL auto_increment,
  `private_chat` tinyint(1) NOT NULL default '0',
  `chat_owner` varchar(20) NOT NULL default '',
  `chat_closed_by` varchar(20) default NULL,
  PRIMARY KEY  (`chat_id`),
  KEY `owned_by` (`chat_owner`),
  KEY `closed_by` (`chat_closed_by`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`chat`
--

/*!40000 ALTER TABLE `chat` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`chat_progress`
--

DROP TABLE IF EXISTS `chat_progress`;
CREATE TABLE `chat_progress` (
  `progress_id` int(20) unsigned NOT NULL auto_increment,
  `chat_id` int(20) NOT NULL default '0',
  `phrase` text NOT NULL,
  `progress_timestamp` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `user_nickname` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`progress_id`),
  KEY `progress` (`chat_id`),
  KEY `talks` (`user_nickname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`chat_progress`
--

/*!40000 ALTER TABLE `chat_progress` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_progress` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`folder_urls`
--

DROP TABLE IF EXISTS `folder_urls`;
CREATE TABLE `folder_urls` (
  `folder_id` int(20) unsigned NOT NULL default '0',
  `url_id` int(20) unsigned NOT NULL default '0',
  PRIMARY KEY  (`folder_id`,`url_id`),
  KEY `folder_url` (`url_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`folder_urls`
--

/*!40000 ALTER TABLE `folder_urls` DISABLE KEYS */;
/*!40000 ALTER TABLE `folder_urls` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`folders`
--

DROP TABLE IF EXISTS `folders`;
CREATE TABLE `folders` (
  `folder_id` int(20) unsigned NOT NULL auto_increment,
  `project_id` int(20) unsigned NOT NULL default '0',
  `name` varchar(30) default NULL,
  `parent_folder` int(20) default NULL,
  `unsorted_folder` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`folder_id`),
  KEY `folder_project` (`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`folders`
--

/*!40000 ALTER TABLE `folders` DISABLE KEYS */;
/*!40000 ALTER TABLE `folders` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`members`
--

DROP TABLE IF EXISTS `members`;
CREATE TABLE `members` (
  `project_id` int(20) unsigned NOT NULL default '0',
  `user_nickname` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`project_id`,`user_nickname`),
  KEY `is_member` (`user_nickname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`members`
--

/*!40000 ALTER TABLE `members` DISABLE KEYS */;
/*!40000 ALTER TABLE `members` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`project_inviteduser`
--

DROP TABLE IF EXISTS `project_inviteduser`;
CREATE TABLE `project_inviteduser` (
  `project_id` int(20) unsigned NOT NULL default '0',
  `invited_user` varchar(20) NOT NULL default '',
  `invitation_confirm` tinyint(1) default NULL,
  `invitation_answered` tinyint(1) default NULL,
  PRIMARY KEY  (`project_id`,`invited_user`),
  KEY `Visitor` (`invited_user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`project_inviteduser`
--

/*!40000 ALTER TABLE `project_inviteduser` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_inviteduser` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`projects`
--

DROP TABLE IF EXISTS `projects`;
CREATE TABLE `projects` (
  `project_id` int(20) unsigned NOT NULL auto_increment,
  `project_chat` int(20) unsigned NOT NULL default '0',
  `project_title` varchar(50) NOT NULL default '',
  `project_description` tinytext,
  `count_members` int(8) NOT NULL default '0',
  `max_members` int(8) NOT NULL default '0',
  `project_owner` varchar(20) NOT NULL default '',
  `project_type` enum('PRIVATE','PUBLIC') NOT NULL default 'PRIVATE',
  `project_date` date NOT NULL default '0000-00-00',
  PRIMARY KEY  (`project_id`),
  KEY `Owner_Nickname` (`project_owner`),
  KEY `Project_ChatID` (`project_chat`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`projects`
--

/*!40000 ALTER TABLE `projects` DISABLE KEYS */;
/*!40000 ALTER TABLE `projects` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`ratings`
--

DROP TABLE IF EXISTS `ratings`;
CREATE TABLE `ratings` (
  `ratings_id` int(20) unsigned NOT NULL auto_increment,
  `project_id` int(20) unsigned NOT NULL default '0',
  `user_nickname` varchar(20) NOT NULL default '',
  `rating` int(1) default NULL,
  `rating_notes` text,
  `url_id` int(20) unsigned NOT NULL default '0',
  `rating_timestamp` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`ratings_id`),
  KEY `is_rated` (`url_id`),
  KEY `project_ratings` (`project_id`),
  KEY `user_rating` (`user_nickname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`ratings`
--

/*!40000 ALTER TABLE `ratings` DISABLE KEYS */;
/*!40000 ALTER TABLE `ratings` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`url`
--

DROP TABLE IF EXISTS `url`;
CREATE TABLE `url` (
  `url_id` int(20) unsigned NOT NULL auto_increment,
  `address` text NOT NULL,
  `title` text NOT NULL,
  PRIMARY KEY  (`url_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`url`
--

/*!40000 ALTER TABLE `url` DISABLE KEYS */;
/*!40000 ALTER TABLE `url` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `nickname` varchar(20) NOT NULL default '',
  `LastName` varchar(20) default NULL,
  `firstName` varchar(20) default NULL,
  `email` varchar(60) NOT NULL default '',
  `user_comment` text,
  `password` varchar(20) NOT NULL default '',
  `user_ip` varchar(15) NOT NULL default '',
  `lang` varchar(2) default 'EN',
  `gender` enum('MALE','FEMALE') default NULL,
  `location` varchar(50) default NULL,
  `hash` varchar(32) NOT NULL default '',
  `logged_in` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`nickname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`user`
--

/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`user_chat`
--

DROP TABLE IF EXISTS `user_chat`;
CREATE TABLE `user_chat` (
  `chat_id` int(20) unsigned NOT NULL default '0',
  `user_nickname` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`chat_id`,`user_nickname`),
  KEY `participate` (`user_nickname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`user_chat`
--

/*!40000 ALTER TABLE `user_chat` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_chat` ENABLE KEYS */;


--
-- Table structure for table `pose_test`.`user_urls`
--

DROP TABLE IF EXISTS `user_urls`;
CREATE TABLE `user_urls` (
  `user_url_id` int(20) unsigned NOT NULL auto_increment,
  `user_nickname` varchar(20) NOT NULL default '',
  `project_id` int(20) unsigned NOT NULL default '0',
  `url_timestamp` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `referred_by_url` int(20) default NULL,
  `url_id` int(20) unsigned NOT NULL default '0',
  PRIMARY KEY  (`user_url_id`),
  KEY `visited_by` (`user_nickname`),
  KEY `project_urls` (`project_id`),
  KEY `address` (`url_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pose_test`.`user_urls`
--

/*!40000 ALTER TABLE `user_urls` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_urls` ENABLE KEYS */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
