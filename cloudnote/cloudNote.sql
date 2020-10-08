/*
Navicat MySQL Data Transfer

Source Server         : 47.100.173.4
Source Server Version : 50729
Source Host           : 47.100.173.4:3306
Source Database       : cloudNote

Target Server Type    : MYSQL
Target Server Version : 50729
File Encoding         : 65001

Date: 2020-10-08 13:20:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `account_id` varchar(32) NOT NULL,
  `account_name` varchar(10) NOT NULL,
  `account_password` varchar(255) NOT NULL,
  `birthday` varchar(10) NOT NULL DEFAULT '',
  `email` varchar(255) NOT NULL,
  `phone` varchar(11) NOT NULL DEFAULT '',
  `last_login_time` varchar(19) NOT NULL DEFAULT '',
  `is_locked` varchar(6) NOT NULL DEFAULT 'UNLOCK',
  `sex` varchar(1) NOT NULL DEFAULT '',
  `head_image_url` varchar(255) NOT NULL,
  `login_count` int(11) NOT NULL DEFAULT '0',
  `area` varchar(255) NOT NULL DEFAULT '',
  `remark` varchar(200) NOT NULL DEFAULT '',
  `update_time` varchar(19) NOT NULL DEFAULT '',
  `create_time` varchar(19) NOT NULL,
  `illegal_data` text NOT NULL COMMENT '违规记录',
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `file_id` varchar(32) NOT NULL,
  `account_id` varchar(32) NOT NULL,
  `file_name` varchar(90) NOT NULL,
  `file_type` varchar(10) NOT NULL,
  `file_size` varchar(255) NOT NULL,
  `create_time` varchar(19) NOT NULL,
  `file_path` text NOT NULL,
  `file_url` text NOT NULL,
  `whole_name` varchar(100) NOT NULL,
  `is_recycle` varchar(3) NOT NULL DEFAULT 'NO',
  `update_time` varchar(19) NOT NULL DEFAULT '',
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for image
-- ----------------------------
DROP TABLE IF EXISTS `image`;
CREATE TABLE `image` (
  `image_id` varchar(32) NOT NULL,
  `account_id` varchar(32) NOT NULL,
  `image_name` varchar(90) NOT NULL,
  `image_type` varchar(10) NOT NULL,
  `image_size` varchar(255) NOT NULL,
  `create_time` varchar(19) NOT NULL,
  `image_path` text NOT NULL,
  `image_url` text NOT NULL,
  `whole_name` varchar(100) NOT NULL,
  `is_recycle` varchar(3) NOT NULL DEFAULT 'NO',
  `update_time` varchar(19) DEFAULT '',
  PRIMARY KEY (`image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for note
-- ----------------------------
DROP TABLE IF EXISTS `note`;
CREATE TABLE `note` (
  `note_id` varchar(32) NOT NULL,
  `account_id` varchar(32) NOT NULL,
  `note_type` varchar(10) NOT NULL,
  `note_title` varchar(30) NOT NULL,
  `note_content` text NOT NULL,
  `create_time` varchar(19) NOT NULL,
  `update_time` varchar(19) NOT NULL DEFAULT '',
  `is_recycle` varchar(3) NOT NULL DEFAULT 'NO',
  `star` varchar(3) NOT NULL DEFAULT 'NO',
  PRIMARY KEY (`note_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for schedule
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule` (
  `schedule_id` varchar(32) NOT NULL,
  `account_id` varchar(32) NOT NULL,
  `schedule_content` text NOT NULL,
  `start_time` varchar(19) NOT NULL,
  `remind_time` varchar(19) NOT NULL DEFAULT '',
  `create_time` varchar(19) NOT NULL,
  `update_time` varchar(19) NOT NULL DEFAULT '',
  `schedule_title` varchar(20) NOT NULL,
  `is_need_remind` varchar(3) NOT NULL,
  `ahead_time` varchar(10) NOT NULL,
  PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
