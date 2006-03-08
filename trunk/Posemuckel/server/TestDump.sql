-- MySQL dump 10.9
--
-- Host: localhost    Database: posemuckel
-- ------------------------------------------------------
-- Server version	4.1.16-nt

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `buddies`
--

DROP TABLE IF EXISTS `buddies`;
CREATE TABLE `buddies` (
  `user_nickname` varchar(20) NOT NULL default '',
  `buddy_nickname` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`user_nickname`,`buddy_nickname`),
  KEY `is_buddy` (`buddy_nickname`),
  CONSTRAINT `has_buddies` FOREIGN KEY (`user_nickname`) REFERENCES `user` (`nickname`) ON DELETE CASCADE,
  CONSTRAINT `is_buddy` FOREIGN KEY (`buddy_nickname`) REFERENCES `user` (`nickname`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `buddies`
--


/*!40000 ALTER TABLE `buddies` DISABLE KEYS */;
LOCK TABLES `buddies` WRITE;
INSERT INTO `buddies` VALUES ('Jens','Holger'),('Lars','Holger'),('stephan','Holger'),('Tanja','Holger'),('Holger','Jens'),('Lars','Jens'),('stephan','Jens'),('Tanja','Jens'),('Holger','Lars'),('Jens','Lars'),('stephan','Sandro'),('Holger','stephan'),('Jens','stephan'),('Tanja','stephan'),('Jens','Tanja'),('stephan','Tanja');
UNLOCK TABLES;
/*!40000 ALTER TABLE `buddies` ENABLE KEYS */;

--
-- Table structure for table `chat`
--

DROP TABLE IF EXISTS `chat`;
CREATE TABLE `chat` (
  `chat_id` int(20) unsigned NOT NULL auto_increment,
  `private_chat` tinyint(1) NOT NULL default '0',
  `chat_owner` varchar(20) NOT NULL default '',
  `chat_closed_by` varchar(20) default NULL,
  PRIMARY KEY  (`chat_id`),
  KEY `owned_by` (`chat_owner`),
  KEY `closed_by` (`chat_closed_by`),
  CONSTRAINT `closed_by` FOREIGN KEY (`chat_closed_by`) REFERENCES `user` (`nickname`) ON DELETE CASCADE,
  CONSTRAINT `owned_by` FOREIGN KEY (`chat_owner`) REFERENCES `user` (`nickname`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `chat`
--


/*!40000 ALTER TABLE `chat` DISABLE KEYS */;
LOCK TABLES `chat` WRITE;
INSERT INTO `chat` VALUES (1,0,'Holger',NULL),(2,0,'Holger',NULL),(3,1,'Holger',NULL),(4,0,'stephan',NULL),(5,1,'Jens',NULL),(6,1,'Tanja',NULL),(7,1,'Tanja',NULL),(8,1,'Tanja',NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `chat` ENABLE KEYS */;

--
-- Table structure for table `chat_progress`
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `chat_progress`
--


/*!40000 ALTER TABLE `chat_progress` DISABLE KEYS */;
LOCK TABLES `chat_progress` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `chat_progress` ENABLE KEYS */;

--
-- Table structure for table `folder_urls`
--

DROP TABLE IF EXISTS `folder_urls`;
CREATE TABLE `folder_urls` (
  `folder_id` int(20) unsigned NOT NULL default '0',
  `url_id` int(20) unsigned NOT NULL default '0',
  PRIMARY KEY  (`folder_id`,`url_id`),
  KEY `folder_url` (`url_id`),
  CONSTRAINT `folder_url` FOREIGN KEY (`url_id`) REFERENCES `url` (`url_id`) ON DELETE CASCADE,
  CONSTRAINT `result_folder` FOREIGN KEY (`folder_id`) REFERENCES `folders` (`folder_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `folder_urls`
--


/*!40000 ALTER TABLE `folder_urls` DISABLE KEYS */;
LOCK TABLES `folder_urls` WRITE;
INSERT INTO `folder_urls` VALUES (18,21),(18,27),(7,74),(7,77),(10,91);
UNLOCK TABLES;
/*!40000 ALTER TABLE `folder_urls` ENABLE KEYS */;

--
-- Table structure for table `folders`
--

DROP TABLE IF EXISTS `folders`;
CREATE TABLE `folders` (
  `folder_id` int(20) unsigned NOT NULL auto_increment,
  `project_id` int(20) unsigned NOT NULL default '0',
  `name` varchar(30) default NULL,
  `parent_folder` int(20) default NULL,
  `unsorted_folder` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`folder_id`),
  KEY `folder_project` (`project_id`),
  CONSTRAINT `folder_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `folders`
--


/*!40000 ALTER TABLE `folders` DISABLE KEYS */;
LOCK TABLES `folders` WRITE;
INSERT INTO `folders` VALUES (6,4,'UNSORTED',NULL,1),(7,4,'Posemuckel',NULL,0),(10,5,'insecten',NULL,0),(11,1,'Verwandte der Feldmaus',22,0),(18,1,'Lebensraum',22,0),(19,1,'Feinde',22,0),(20,1,'Freunde',22,0),(21,5,'OrdnerSoFrüh',NULL,0),(22,1,'Die Feldmaus',NULL,0),(23,1,'Die Feldmaus',NULL,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `folders` ENABLE KEYS */;

--
-- Table structure for table `members`
--

DROP TABLE IF EXISTS `members`;
CREATE TABLE `members` (
  `project_id` int(20) unsigned NOT NULL default '0',
  `user_nickname` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`project_id`,`user_nickname`),
  KEY `is_member` (`user_nickname`),
  CONSTRAINT `has_members` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE,
  CONSTRAINT `is_member` FOREIGN KEY (`user_nickname`) REFERENCES `user` (`nickname`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `members`
--


/*!40000 ALTER TABLE `members` DISABLE KEYS */;
LOCK TABLES `members` WRITE;
INSERT INTO `members` VALUES (1,'Holger'),(2,'Holger'),(3,'Holger'),(4,'Holger'),(1,'Jens'),(4,'Jens'),(1,'Lala'),(4,'Sandro'),(1,'stephan'),(4,'stephan'),(1,'Tanja'),(4,'Tanja'),(5,'Tanja');
UNLOCK TABLES;
/*!40000 ALTER TABLE `members` ENABLE KEYS */;

--
-- Table structure for table `project_inviteduser`
--

DROP TABLE IF EXISTS `project_inviteduser`;
CREATE TABLE `project_inviteduser` (
  `project_id` int(20) unsigned NOT NULL default '0',
  `invited_user` varchar(20) NOT NULL default '',
  `invitation_confirm` tinyint(1) default NULL,
  `invitation_answered` tinyint(1) default NULL,
  PRIMARY KEY  (`project_id`,`invited_user`),
  KEY `Visitor` (`invited_user`),
  CONSTRAINT `User_Project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE,
  CONSTRAINT `Visitor` FOREIGN KEY (`invited_user`) REFERENCES `user` (`nickname`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `project_inviteduser`
--


/*!40000 ALTER TABLE `project_inviteduser` DISABLE KEYS */;
LOCK TABLES `project_inviteduser` WRITE;
INSERT INTO `project_inviteduser` VALUES (2,'Jens',0,0),(2,'Lars',0,0),(3,'Jens',0,0),(3,'Lars',0,0),(4,'Holger',1,1),(4,'Jens',1,1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `project_inviteduser` ENABLE KEYS */;

--
-- Table structure for table `projects`
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
  KEY `Project_ChatID` (`project_chat`),
  CONSTRAINT `Owner_Nickname` FOREIGN KEY (`project_owner`) REFERENCES `user` (`nickname`) ON DELETE CASCADE,
  CONSTRAINT `Project_ChatID` FOREIGN KEY (`project_chat`) REFERENCES `chat` (`chat_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `projects`
--


/*!40000 ALTER TABLE `projects` DISABLE KEYS */;
LOCK TABLES `projects` WRITE;
INSERT INTO `projects` VALUES (1,1,'Das Leben der Feldmaus','Hier wird das Leben der Feldmaus beschrieben',5,10,'Holger','PUBLIC','2006-01-31'),(2,2,'Linux','Hier geht es um ..... Linux',1,20,'Holger','PUBLIC','2006-01-31'),(3,3,'BLOGGER','Hier geht es um Blogger',1,3,'Holger','PRIVATE','2006-01-31'),(4,4,'Pose- vs. Pusemuckel','blub',5,10,'stephan','PUBLIC','2006-02-17'),(5,8,'no entry','privaten Bereich der Webseite teste',1,1,'Tanja','PRIVATE','2006-02-17');
UNLOCK TABLES;
/*!40000 ALTER TABLE `projects` ENABLE KEYS */;

--
-- Table structure for table `ratings`
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
  KEY `user_rating` (`user_nickname`),
  CONSTRAINT `is_rated` FOREIGN KEY (`url_id`) REFERENCES `url` (`url_id`),
  CONSTRAINT `project_ratings` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`),
  CONSTRAINT `user_rating` FOREIGN KEY (`user_nickname`) REFERENCES `user` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ratings`
--


/*!40000 ALTER TABLE `ratings` DISABLE KEYS */;
LOCK TABLES `ratings` WRITE;
INSERT INTO `ratings` VALUES (1,1,'Holger',5,NULL,1,'2006-01-31 10:13:42'),(2,1,'Jens',4,NULL,7,'2006-01-31 10:14:34'),(3,1,'Holger',0,NULL,2,'2006-01-31 10:14:47'),(4,1,'Jens',5,NULL,10,'2006-01-31 10:15:13'),(5,1,'Holger',4,NULL,11,'2006-01-31 10:15:20'),(6,1,'Holger',4,NULL,13,'2006-01-31 10:16:00'),(7,1,'Holger',5,NULL,17,'2006-01-31 10:16:30'),(8,1,'Holger',0,NULL,14,'2006-01-31 10:16:37'),(9,1,'Holger',0,NULL,19,'2006-01-31 10:16:58'),(10,1,'Holger',0,NULL,20,'2006-01-31 10:17:04'),(11,1,'Jens',4,NULL,18,'2006-01-31 10:17:14'),(12,1,'Holger',3,NULL,12,'2006-01-31 10:17:16'),(13,1,'Jens',5,NULL,21,'2006-01-31 10:17:36'),(14,1,'Holger',2,NULL,24,'2006-01-31 10:19:08'),(15,1,'Jens',5,NULL,27,'2006-01-31 10:35:08'),(16,1,'Holger',2,NULL,32,'2006-01-31 10:36:44'),(17,1,'Holger',5,NULL,28,'2006-01-31 10:38:09'),(18,1,'Jens',0,NULL,28,'2006-01-31 10:38:02'),(19,1,'Holger',3,NULL,43,'2006-01-31 10:45:49'),(20,1,'Jens',5,NULL,44,'2006-01-31 10:45:47'),(21,1,'Jens',5,NULL,47,'2006-01-31 10:47:01'),(22,1,'Holger',3,'Langweilig',38,'2006-01-31 10:48:34'),(23,1,'Holger',2,NULL,49,'2006-01-31 10:49:39'),(24,4,'stephan',5,NULL,52,'2006-02-17 10:32:29'),(25,4,'stephan',5,NULL,67,'2006-02-17 10:50:04'),(26,4,'Jens',5,NULL,74,'2006-02-17 10:59:06'),(27,4,'Jens',5,NULL,70,'2006-02-17 11:00:17'),(28,4,'Jens',5,'',77,'2006-02-17 11:02:07'),(29,4,'Tanja',3,NULL,70,'2006-02-17 13:28:25'),(30,4,'Tanja',2,NULL,67,'2006-02-17 13:29:17'),(31,4,'Tanja',0,NULL,82,'2006-02-17 13:30:12'),(32,5,'Tanja',5,'cool',91,'2006-02-17 13:36:57'),(33,4,'Tanja',3,'Tanja hat die Seite mit 3 bewertet!',52,'2006-02-17 20:07:31');
UNLOCK TABLES;
/*!40000 ALTER TABLE `ratings` ENABLE KEYS */;

--
-- Table structure for table `url`
--

DROP TABLE IF EXISTS `url`;
CREATE TABLE `url` (
  `url_id` int(20) unsigned NOT NULL auto_increment,
  `address` text NOT NULL,
  `title` text NOT NULL,
  PRIMARY KEY  (`url_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `url`
--


/*!40000 ALTER TABLE `url` DISABLE KEYS */;
LOCK TABLES `url` WRITE;
INSERT INTO `url` VALUES (1,'http://www.heise.de/','heise online'),(2,'http://www.google.de/','Google'),(3,'http://www.google.de/search?hl=de&q=Feldmaus&meta=','Feldmaus - Google-Suche'),(4,'http://www.ebe-online.de/home/fmg/projekte/Projekte02/Boden/hamster/feldmaus.htm','Fehler'),(5,'http://www.maedchen-schmuck.de/bafoegantrag.htm','http://www.maedchen-schmuck.de/bafoegantrag.htm'),(6,'http://www.24www.de/24startupcenter.htm','Die Startseite : Schnell - Professionell - All in One'),(7,'http://www.kinder-tierlexikon.de/f/feldmaus.htm','Kinder-Tierlexikon, die Feldmaus'),(8,'http://www.feldmaus.de/','Haus Feldmaus - Hotel, Seminarhaus, Vollwert-Restaurant u. Kunst in der Eifel'),(9,'http://www.google.de/search?hl=de&q=das+leben+der+Feldmaus&btnG=Google-Suche&meta=','das leben der Feldmaus - Google-Suche'),(10,'http://www.exotische-nager.de/Feldmaus.htm','Feldmaus'),(11,'http://www.zum.de/Faecher/Materialien/hupfeld/index.htm?/Faecher/Materialien/hupfeld/Aufgaben/abiotische-faktoren-feldmaus.htm','Die besondere Biologie-Seite von Helmut Hupfeld'),(12,'http://www.100tiere.de/saugetier/sauge_feldmaus.html','100Tiere'),(13,'http://www.yolanthe.de/stories/c-akfeld.htm','Kalmer: Das Märchen von der Feldmaus'),(14,'http://www.google.de/search?q=das+leben+der+Feldmaus&hl=de&lr=&start=10&sa=N','das leben der Feldmaus - Google-Suche'),(15,'http://www.wikipedia.de/','Wikipedia'),(16,'http://de.wikipedia.org/wiki/Hauptseite','Hauptseite - Wikipedia'),(17,'http://gutenberg.spiegel.de/fabeln/woerdema/helmu046.htm','Projekt Gutenberg-DE - Kultur - SPIEGEL ONLINE'),(18,'http://de.wikipedia.org/wiki/M%C3%A4use','Mäuse - Wikipedia'),(19,'http://www.bio-gaertner.de/Auxx/Fehler.html','Fehlerhafter Link! - Der Bio-Gärtner'),(20,'http://www.google.de/search?q=das+leben+der+Feldmaus&hl=de&lr=&start=20&sa=N','das leben der Feldmaus - Google-Suche'),(21,'http://de.wikipedia.org/wiki/Feldmaus','Feldmaus - Wikipedia'),(22,'http://www.tierena.de/tiere-3.432.13.html','feldmaus - Webkatalog auf tierena zu feldmaus - 1/4'),(23,'http://www.zum.de/Faecher/Materialien/dittrich/Steckbrief/feldmaus.htm','Die Feldmaus'),(24,'http://www.kinder-tierlexikon.de/','Kinder-Tierlexikon, Tiere von Kindern für Kinder'),(25,'http://www.google.de/search?q=Feldmaus+Plage&hl=de','Feldmaus Plage - Google-Suche'),(26,'http://www.google.de/search?hl=de&q=FledermausPlage&spell=1','FledermausPlage - Google-Suche'),(27,'http://www.wer-weiss-was.de/theme204/article1008819.html','wer-weiss-was | \"Fledermausplage\" | aus Forum Witze'),(28,'http://www.fitzworld.de/npw/','Posaunenwerk Rheinland (Herzlich Willkommen)'),(29,'http://www.fitzworld.de/npw/index.php?wx=6&wy=0&imp=8350f1f5f051aa33467acedde5e5cf2f','Posaunenwerk Rheinland (Forum)'),(30,'http://www.fitzworld.de/npw/index.php?wx=2&wy=0&imp=7c36cfb6da267bb7deb454f9cdb8880a','Posaunenwerk Rheinland (Angebote)'),(31,'http://www.fitzworld.de/npw/index.php?wx=3&wy=0&imp=8350f1f5f051aa33467acedde5e5cf2f','Posaunenwerk Rheinland (Posaunenwerk)'),(32,'http://www.gmx.net/de/','GMX - Mail · Message · More'),(33,'http://www.ciao.de/Bats_Fliegende_Teufel__Test_1552654','Bats - Fliegende Teufel - Erfahrungsbericht - abgekupfertes B-Movie'),(34,'http://www.google.de/search?q=FledermausPlage&hl=de&lr=&start=10&sa=N','FledermausPlage - Google-Suche'),(35,'http://www.google.de/search?q=FledermausPlage&hl=de&lr=&start=20&sa=N','FledermausPlage - Google-Suche'),(36,'http://www.ahgz.info/touristik/2001,42,110123892.html','Fledermausplage im Nationalmuseum - Allgemeine Hotel- und Gaststätten-Zeitung (AHGZ)'),(37,'http://www.maus.de/','MausNet(R) Homepage'),(38,'http://www.maus.de/maus/inhalt.html','Inhaltsverzeichnis'),(39,'http://www.google.de/search?hl=de&q=M%C3%A4use&meta=','Mäuse - Google-Suche'),(40,'http://www.das-tierlexikon.de/echte_maeuse.htm','Echte Mäuse'),(41,'http://www.google.de/search?hl=de&q=Lebensraum+Feldmaus&meta=','Lebensraum Feldmaus - Google-Suche'),(42,'http://www.maus.de/maus/database/sysprot.html','MausNet Informationen'),(43,'http://www.ohse.de/uwe/mausnet/sysoptreff95.html','MausNet Sysoptreffen 1995'),(44,'http://www.bba.de/oekoland/oeko3/w-maus.htm','-BBA - Wühlmausbekämpfung im ökologischen Landbau-'),(45,'http://www.bba.de/oekoland/oeko3/w-maus3.htm','-BBA - Wühlmausbekämpfung im ökologischen Landbau -'),(46,'http://www.google.de/search?hl=de&q=Falle+Feldmaus&meta=','Falle Feldmaus - Google-Suche'),(47,'http://www.hausmaus.at/F3.htm','Die Feldmaus'),(48,'http://www.hausmaus.at/FeldmausZeitErfolgJagd.htm','Die Feldmaus mit Erfolg jagen'),(49,'http://www.hausmaus.at/LaermFeldmausBahnBundesstrasse.htm','Wird die Feldmaus vom Zug und Auto gestört'),(50,'http://www.google.de/search?hl=de&q=pusemuckel&meta=','pusemuckel - Google-Suche'),(51,'http://www.ibusiness.de/ibex/db/ibex.7505jg.6952ln.7005ln.html','Server nicht gefunden'),(52,'http://dict.leo.org/cgi-bin/dico/forum.cgi?action=show&sort_order=&list_size=&list_skip=0&group=forum003_correct&file=20051101134503','dicoForum:'),(53,'http://www.google.de/search?hl=de&q=posemuckel&meta=','posemuckel - Google-Suche'),(54,'http://www.dogpile.com/','Dogpile Web Search Home Page'),(55,'http://dict.leo.org/?lp=frde&lang=de&searchLoc=0&cmpType=relaxed&relink=on&sectHdr=on&spellToler=std&search=Posemuckel','LEO Ergebnisse für \"Posemuckel\"'),(56,'http://www.redensarten-index.de/suche.php?suchbegriff=~~Posemuckel&suchspalte%5B%5D=rart_ou','Posemuckel'),(57,'http://forum.gamigo.de/member.php?find=lastposter&t=18417','Gamigo Foren - Profil ansehen: Pusemuckel'),(58,'http://www.dogpile.com/info.dogpl/search/web/posemuckel/1/-/1/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/417/top','Dogpile - Web Search: posemuckel'),(59,'http://www.merkur.de/9369.0.html?&no_cache=1','::Rheinischer Merkur - Die Magazinzeitung für Deutschland jede Woche : 2005_50.Klein Posemuckel ::'),(60,'http://www.redensarten-index.de/suche.php?suchbegriff=Posemuckel&bool=relevanz&suchspalte%5B%5D=rart_ou','Posemuckel - Wörterbuch Deutsch'),(61,'http://www.google.de/advanced_search?hl=de','Google Erweiterte Suche'),(62,'http://www.redensarten-index.de/suche.php?suchbegriff=Pusemuckel&bool=relevanz&suchspalte%5B%5D=rart_ou','Pusemuckel - Wörterbuch Deutsch'),(63,'http://www.google.de/search?as_q=posemuckel&num=10&hl=de&btnG=Google-Suche&as_epq=&as_oq=&as_eq=&lr=lang_en&as_ft=i&as_filetype=&as_qdr=all&as_occt=any&as_dt=i&as_sitesearch=&as_rights=&safe=images','posemuckel - Google-Suche'),(64,'http://google50.angebot-overture.de/','http://google50.angebot-overture.de/'),(65,'http://forum.gamigo.de/search.php?searchid=97709','Gamigo Foren - Suchergebnisse'),(66,'http://de.wikipedia.org/wiki/Wikipedia:Humorarchiv/Geographisches','Wikipedia:Humorarchiv/Geographisches - Wikipedia'),(67,'http://de.wikipedia.org/wiki/Bild:Einwohner.jpg','Bild:Einwohner.jpg - Wikipedia'),(68,'http://upload.wikimedia.org/wikipedia/de/d/d5/Einwohner.jpg','http://upload.wikimedia.org/wikipedia/de/d/d5/Einwohner.jpg'),(69,'http://posemuckel.kleine-planeten.de/','Posemuckel - Eine ganz normale Internetseite'),(70,'http://www.posemuckel.de/','http://www.posemuckel.de/'),(71,'http://de.wikipedia.org/w/index.php?title=Bild_Diskussion:Einwohner.jpg&action=edit','Bearbeiten von Bild Diskussion:Einwohner.jpg - Wikipedia'),(72,'http://posemuckel.kleine-planeten.de/finster/index.htm','posemuckel.DE - wer den Sinn entdeckt, darf einziehen.'),(73,'http://posemuckel.kleine-planeten.de/index.htm','Posemuckel - Eine ganz normale Internetseite'),(74,'http://de.wikipedia.org/wiki/Posemuckel','Posemuckel - Wikipedia'),(75,'http://www.goleomat.de/','Goleomat - Die Höhle des Löwen'),(76,'http://de.wikipedia.org/wiki/Podmokle_Wielkie','Podmokle Wielkie - Wikipedia'),(77,'http://posemuckel.no-ip.org/','Posemuckel'),(78,'http://posemuckel.no-ip.org/index.php?op=pubproj&id=4','Posemuckel'),(79,'http://posemuckel.no-ip.org/index.php?op=pubproj&id=1','Posemuckel'),(80,'http://posemuckel.no-ip.org/index.php?op=pubproj&id=2','Posemuckel'),(81,'http://posemuckel.no-ip.org/index.php?','Posemuckel'),(82,'http://posemuckel.no-ip.org/index.php?op=folder&id=1','Posemuckel'),(83,'http://posemuckel.no-ip.org/index.php?op=folder&id=Array','Posemuckel'),(84,'http://posemuckel.no-ip.org/index.php?op=subfolder&id=4','Posemuckel'),(85,'http://posemuckel.no-ip.org/index.php?op=folder&id=5','Posemuckel'),(86,'http://posemuckel.no-ip.org/index.php?op=folder&id=2','Posemuckel'),(87,'http://posemuckel.no-ip.org/index.php?op=folder&id=4','Posemuckel'),(88,'http://posemuckel.no-ip.org/index.php?op=folder&id=3','Posemuckel'),(89,'http://posemuckel.no-ip.org/index.php?op=subfolder&id=3','Posemuckel'),(90,'http://www.google.de/search?hl=de&q=%22no+entry%22&meta=','\"no entry\" - Google-Suche'),(91,'http://www.nature.com/nature/journal/v438/n7067/abs/438442a.html','Insect communication/`No entry/\' signal in ant foraging : Nature'),(92,'http://de.wikipedia.org/wiki/Bild:Mus_Musculus-huismuis.jpg','Bild:Mus Musculus-huismuis.jpg - Wikipedia');
UNLOCK TABLES;
/*!40000 ALTER TABLE `url` ENABLE KEYS */;

--
-- Table structure for table `user`
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
  `lang` char(2) default 'EN',
  `gender` enum('MALE','FEMALE') default NULL,
  `location` varchar(50) default NULL,
  `hash` varchar(32) NOT NULL default '',
  `logged_in` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--


/*!40000 ALTER TABLE `user` DISABLE KEYS */;
LOCK TABLES `user` WRITE;
INSERT INTO `user` VALUES ('Holger','Bach','Holger','bach@gmx.li','','Bach','192.168.168.101','EN','MALE','Braunschweig','48d166332958472be2d1bea2ea2ff589',0),('Jens','Neppe','Jens-Dietrich','jn@linux-fuer-alle.de','Ich hab hier kein sinnvolles Kommentar.','Neppe','127.0.0.1','DE','MALE','Posemuckel','67b030fd6d755e94dae4216db6399e1',0),('Lala','','','Lala','','Lala','84.133.24.199','DE','MALE','','f6d0a32815fcaad27f772c757eac984e',0),('Lars','Michler','Lars','michlerl@tiscali.de','So surft man heute!','Michler','192.168.168.100','DE','MALE','Posemuckel','57ed4cb5f24285ad76c519eb8dbd552b',0),('Sandro','Scaiano','Sandro','xx','','Scaiano','192.168.0.3','EN','MALE','xx','89393f61ea5166146248f8ce59dff298',0),('stephan','','Stephan','stephan.lukosch@fernuni-hagen.de','','s','127.0.0.1','EN','MALE','','8356ade61fa4cb70f826dd4ad590da',0),('Tanja','Buttler','Tanja','Tanja.Buttler@FernUni-Hagen.de','','Buttler','217.249.82.180','EN','FEMALE','Aalen','ea8f1e54136c672fad6d6052ea7b63a1',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

--
-- Table structure for table `user_chat`
--

DROP TABLE IF EXISTS `user_chat`;
CREATE TABLE `user_chat` (
  `chat_id` int(20) unsigned NOT NULL default '0',
  `user_nickname` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`chat_id`,`user_nickname`),
  KEY `participate` (`user_nickname`),
  CONSTRAINT `members` FOREIGN KEY (`chat_id`) REFERENCES `chat` (`chat_id`) ON DELETE CASCADE,
  CONSTRAINT `participate` FOREIGN KEY (`user_nickname`) REFERENCES `user` (`nickname`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_chat`
--


/*!40000 ALTER TABLE `user_chat` DISABLE KEYS */;
LOCK TABLES `user_chat` WRITE;
INSERT INTO `user_chat` VALUES (1,'Holger'),(2,'Holger'),(3,'Holger'),(4,'Holger'),(5,'Holger'),(1,'Jens'),(4,'Jens'),(5,'Jens'),(1,'Lala'),(4,'Sandro'),(6,'Sandro'),(1,'stephan'),(4,'stephan'),(7,'stephan'),(1,'Tanja'),(4,'Tanja'),(5,'Tanja'),(6,'Tanja'),(7,'Tanja'),(8,'Tanja');
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_chat` ENABLE KEYS */;

--
-- Table structure for table `user_urls`
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
  KEY `address` (`url_id`),
  CONSTRAINT `address` FOREIGN KEY (`url_id`) REFERENCES `url` (`url_id`) ON DELETE CASCADE,
  CONSTRAINT `project_urls` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE,
  CONSTRAINT `visited_by` FOREIGN KEY (`user_nickname`) REFERENCES `user` (`nickname`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_urls`
--


/*!40000 ALTER TABLE `user_urls` DISABLE KEYS */;
LOCK TABLES `user_urls` WRITE;
INSERT INTO `user_urls` VALUES (1,'Holger',1,'2006-01-31 10:13:35',NULL,1),(2,'Jens',1,'2006-01-31 10:13:38',NULL,2),(3,'Jens',1,'2006-01-31 10:13:46',2,3),(4,'Jens',1,'2006-01-31 10:13:52',3,4),(5,'Holger',1,'2006-01-31 10:13:53',NULL,5),(6,'Holger',1,'2006-01-31 10:13:58',5,6),(7,'Jens',1,'2006-01-31 10:13:59',3,7),(8,'Holger',1,'2006-01-31 10:14:27',NULL,2),(9,'Jens',1,'2006-01-31 10:14:47',3,8),(10,'Holger',1,'2006-01-31 10:14:50',2,9),(11,'Jens',1,'2006-01-31 10:14:55',3,10),(12,'Holger',1,'2006-01-31 10:15:07',9,11),(13,'Jens',1,'2006-01-31 10:15:44',3,12),(14,'Holger',1,'2006-01-31 10:15:57',9,13),(15,'Holger',1,'2006-01-31 10:16:06',9,14),(16,'Jens',1,'2006-01-31 10:16:08',NULL,15),(17,'Jens',1,'2006-01-31 10:16:15',15,16),(18,'Holger',1,'2006-01-31 10:16:20',14,17),(19,'Jens',1,'2006-01-31 10:16:46',16,18),(20,'Holger',1,'2006-01-31 10:16:48',14,19),(21,'Holger',1,'2006-01-31 10:17:02',14,20),(22,'Holger',1,'2006-01-31 10:17:08',20,12),(23,'Jens',1,'2006-01-31 10:17:26',18,21),(24,'Jens',1,'2006-01-31 10:18:03',NULL,20),(25,'Holger',1,'2006-01-31 10:18:12',20,22),(26,'Jens',1,'2006-01-31 10:18:15',20,22),(27,'Holger',1,'2006-01-31 10:18:26',22,23),(28,'Jens',1,'2006-01-31 10:18:27',22,23),(29,'Jens',1,'2006-01-31 10:18:55',23,24),(30,'Holger',1,'2006-01-31 10:18:56',23,24),(31,'Jens',1,'2006-01-31 10:23:08',20,25),(32,'Holger',1,'2006-01-31 10:23:09',20,25),(33,'Jens',1,'2006-01-31 10:23:16',25,26),(34,'Holger',1,'2006-01-31 10:23:17',25,26),(35,'Jens',1,'2006-01-31 10:23:24',26,27),(36,'Holger',1,'2006-01-31 10:23:27',26,27),(37,'Jens',1,'2006-01-31 10:35:50',26,28),(38,'Holger',1,'2006-01-31 10:35:54',26,28),(39,'Jens',1,'2006-01-31 10:36:00',28,29),(40,'Holger',1,'2006-01-31 10:36:01',28,29),(41,'Holger',1,'2006-01-31 10:36:07',29,30),(42,'Jens',1,'2006-01-31 10:36:08',29,31),(43,'Holger',1,'2006-01-31 10:36:12',30,31),(44,'Holger',1,'2006-01-31 10:36:23',29,26),(45,'Holger',1,'2006-01-31 10:36:40',NULL,32),(46,'Jens',1,'2006-01-31 10:37:01',26,33),(47,'Holger',1,'2006-01-31 10:37:02',32,26),(48,'Jens',1,'2006-01-31 10:37:20',26,34),(49,'Holger',1,'2006-01-31 10:37:20',27,34),(50,'Jens',1,'2006-01-31 10:37:32',34,35),(51,'Jens',1,'2006-01-31 10:37:43',35,36),(52,'Jens',1,'2006-01-31 10:41:05',NULL,37),(53,'Jens',1,'2006-01-31 10:41:28',37,38),(54,'Holger',1,'2006-01-31 10:42:21',NULL,38),(55,'Jens',1,'2006-01-31 10:44:12',2,39),(56,'Jens',1,'2006-01-31 10:44:21',39,40),(57,'Jens',1,'2006-01-31 10:45:18',39,41),(58,'Holger',1,'2006-01-31 10:45:23',38,42),(59,'Holger',1,'2006-01-31 10:45:25',42,43),(60,'Jens',1,'2006-01-31 10:45:27',41,44),(61,'Jens',1,'2006-01-31 10:46:07',44,45),(62,'Jens',1,'2006-01-31 10:46:42',41,46),(63,'Jens',1,'2006-01-31 10:46:48',46,47),(64,'Jens',1,'2006-01-31 10:47:14',47,48),(65,'Jens',1,'2006-01-31 10:48:15',47,49),(66,'Holger',1,'2006-01-31 10:49:19',NULL,49),(67,'stephan',4,'2006-02-17 10:25:48',NULL,2),(68,'Holger',4,'2006-02-17 10:25:52',NULL,2),(69,'stephan',4,'2006-02-17 10:26:03',2,50),(70,'Holger',4,'2006-02-17 10:26:04',2,50),(71,'stephan',4,'2006-02-17 10:29:38',50,51),(72,'Tanja',4,'2006-02-17 10:29:59',NULL,50),(73,'Holger',4,'2006-02-17 10:30:00',50,51),(74,'stephan',4,'2006-02-17 10:30:08',50,52),(75,'Holger',4,'2006-02-17 10:30:11',51,52),(76,'Tanja',4,'2006-02-17 10:30:23',50,52),(77,'Holger',4,'2006-02-17 10:31:28',50,53),(78,'Sandro',4,'2006-02-17 10:31:52',NULL,54),(79,'Jens',4,'2006-02-17 10:32:49',NULL,50),(80,'Tanja',4,'2006-02-17 10:33:06',52,55),(81,'Sandro',4,'2006-02-17 10:33:07',NULL,52),(82,'Holger',4,'2006-02-17 10:33:17',53,56),(83,'Jens',4,'2006-02-17 10:33:34',57,57),(84,'Sandro',4,'2006-02-17 10:33:39',54,58),(85,'Sandro',4,'2006-02-17 10:33:44',58,59),(86,'stephan',4,'2006-02-17 10:33:47',NULL,56),(87,'Holger',4,'2006-02-17 10:33:48',56,60),(88,'Tanja',4,'2006-02-17 10:33:58',NULL,2),(89,'Tanja',4,'2006-02-17 10:34:19',2,61),(90,'Holger',4,'2006-02-17 10:34:31',60,62),(91,'Tanja',4,'2006-02-17 10:34:46',61,63),(92,'Holger',4,'2006-02-17 10:35:38',62,64),(93,'Jens',4,'2006-02-17 10:38:00',57,65),(94,'stephan',4,'2006-02-17 10:49:36',50,66),(95,'stephan',4,'2006-02-17 10:49:51',66,67),(96,'Sandro',4,'2006-02-17 10:50:37',NULL,67),(97,'Holger',4,'2006-02-17 10:50:48',NULL,67),(98,'Holger',4,'2006-02-17 10:50:58',67,68),(99,'Sandro',4,'2006-02-17 10:51:38',69,69),(100,'Holger',4,'2006-02-17 10:51:45',NULL,70),(101,'Tanja',4,'2006-02-17 10:52:22',NULL,67),(102,'Tanja',4,'2006-02-17 10:53:04',NULL,70),(103,'Jens',4,'2006-02-17 10:54:02',NULL,67),(104,'Holger',4,'2006-02-17 10:54:08',67,71),(105,'Sandro',4,'2006-02-17 10:54:25',69,72),(106,'Sandro',4,'2006-02-17 10:54:38',73,73),(107,'Holger',4,'2006-02-17 10:55:01',67,74),(108,'Jens',4,'2006-02-17 10:56:15',NULL,56),(109,'Jens',4,'2006-02-17 10:57:27',NULL,74),(110,'Tanja',4,'2006-02-17 10:58:47',70,75),(111,'Jens',4,'2006-02-17 10:59:25',74,76),(112,'Jens',4,'2006-02-17 11:00:08',NULL,70),(113,'Jens',4,'2006-02-17 11:01:03',NULL,77),(114,'Jens',4,'2006-02-17 11:01:07',77,78),(115,'Jens',4,'2006-02-17 11:01:09',78,79),(116,'Holger',4,'2006-02-17 11:02:32',NULL,79),(117,'Holger',4,'2006-02-17 11:02:40',79,78),(118,'Holger',4,'2006-02-17 11:02:50',78,80),(119,'Sandro',4,'2006-02-17 11:20:38',NULL,79),(120,'Tanja',4,'2006-02-17 11:32:03',NULL,78),(121,'Tanja',4,'2006-02-17 11:32:25',78,79),(122,'Tanja',4,'2006-02-17 11:32:32',79,77),(123,'Holger',2,'2006-02-17 11:32:41',NULL,77),(124,'Holger',2,'2006-02-17 11:32:50',77,81),(125,'Holger',2,'2006-02-17 11:33:13',81,79),(126,'Holger',2,'2006-02-17 11:33:33',79,78),(127,'Tanja',4,'2006-02-17 11:36:30',79,82),(128,'Holger',2,'2006-02-17 11:36:49',79,82),(129,'Holger',2,'2006-02-17 11:37:29',79,80),(130,'Tanja',4,'2006-02-17 11:37:35',82,83),(131,'Holger',2,'2006-02-17 11:38:54',82,84),(132,'Holger',2,'2006-02-17 11:38:56',84,85),(133,'Holger',2,'2006-02-17 11:38:58',85,86),(134,'Holger',2,'2006-02-17 11:38:59',86,87),(135,'Holger',2,'2006-02-17 11:39:01',87,88),(136,'Holger',2,'2006-02-17 11:39:05',88,78),(137,'Holger',2,'2006-02-17 11:39:13',82,89),(138,'Holger',2,'2006-02-17 11:39:41',89,87),(139,'Holger',2,'2006-02-17 11:39:47',85,88),(140,'Holger',2,'2006-02-17 11:40:04',86,21),(141,'Holger',2,'2006-02-17 11:40:50',86,88),(142,'Holger',2,'2006-02-17 11:41:19',88,79),(143,'Holger',2,'2006-02-17 11:42:23',85,79),(144,'Holger',2,'2006-02-17 11:42:29',84,86),(145,'Tanja',5,'2006-02-17 13:35:15',NULL,2),(146,'Tanja',5,'2006-02-17 13:35:25',2,90),(147,'Tanja',5,'2006-02-17 13:36:19',90,91),(148,'Tanja',1,'2006-02-19 02:23:18',NULL,1),(149,'Jens',1,'2006-02-19 11:34:37',18,92),(150,'Jens',1,'2006-02-19 12:12:19',NULL,1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_urls` ENABLE KEYS */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

