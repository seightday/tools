CREATE DATABASE `comparator` ;

USE `comparator`;

DROP TABLE IF EXISTS `dir1`;

CREATE TABLE `dir1` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `path` varchar(500) NOT NULL,
  `md5` varchar(32) NOT NULL,
  `rpath` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dir2`;

CREATE TABLE `dir2` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `path` varchar(500) NOT NULL,
  `md5` varchar(32) NOT NULL,
  `rpath` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `path1`;

CREATE TABLE `path1` (
  `path` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `path2`;

CREATE TABLE `path2` (
  `path` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

