-- Dumping database structure for kaggle_fb
CREATE DATABASE IF NOT EXISTS `kaggle_fb_t1` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `kaggle_fb_t1`;


-- Dumping structure for table kaggle_fb.tags
CREATE TABLE IF NOT EXISTS `tags` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- Dumping structure for table kaggle_fb.train
CREATE TABLE IF NOT EXISTS `train` (
  `id` int(10) unsigned NOT NULL,
  `title` varchar(1023) DEFAULT NULL,
  `body` mediumtext,
  `tags` varchar(1023) DEFAULT NULL,
  `rand` tinyint(3) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='train data';

-- Dumping structure for table kaggle_fb.train_tag_mapping
CREATE TABLE IF NOT EXISTS `train_tag_mapping` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `train_id` int(10) NOT NULL,
  `tag_id` int(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `train_id_tag_id` (`train_id`,`tag_id`),
  KEY `tag_id` (`tag_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


-- Dumping structure for table kaggle_fb.mv_count_tags
CREATE TABLE IF NOT EXISTS `mv_count_tags` (
  `name` char(255) NOT NULL,
  `count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- Dumping structure for table kaggle_fb.mv_count_tags
CREATE TABLE IF NOT EXISTS `mv_x2_count_tags` (
	`id1` INT(11) NOT NULL,
	`id2` INT(11) NOT NULL,
	`count` INT(10) UNSIGNED NOT NULL DEFAULT '0',
	PRIMARY KEY (`id1`, `id2`),
	INDEX `id1` (`id1`),
	INDEX `id2` (`id2`)
)
COLLATE='latin1_swedish_ci'
ENGINE=MyISAM;


-- Load data
TRUNCATE TABLE `train`;
INSERT INTO train SELECT * FROM kaggle_fb.train where rand < 3;
