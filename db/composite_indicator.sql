/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : localhost:3306
 Source Schema         : composite_indicator

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 03/10/2021 15:16:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for algorithm
-- ----------------------------
DROP TABLE IF EXISTS `algorithm`;
CREATE TABLE `algorithm`  (
  `id` bigint(11) NOT NULL,
  `algorithm_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '算法名称/类名',
  `display_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '中文名称/显示名称',
  `full_class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '全类名',
  `step_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '步骤名称',
  `exec_order` int(2) NULL DEFAULT NULL COMMENT '执行次序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for algorithm_exec_step
-- ----------------------------
DROP TABLE IF EXISTS `algorithm_exec_step`;
CREATE TABLE `algorithm_exec_step`  (
  `id` bigint(64) NOT NULL,
  `step_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '步骤名称',
  `step_value` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '步骤值',
  `exec_order` int(2) NULL DEFAULT NULL COMMENT '步骤次序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ci_construct_target
-- ----------------------------
DROP TABLE IF EXISTS `ci_construct_target`;
CREATE TABLE `ci_construct_target`  (
  `id` bigint(16) NOT NULL AUTO_INCREMENT,
  `target_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `belong_column_index` int(2) NULL DEFAULT NULL,
  `ci_framework_object_id` bigint(16) NULL DEFAULT NULL,
  `data` json NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 257 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ci_framework_indicator
-- ----------------------------
DROP TABLE IF EXISTS `ci_framework_indicator`;
CREATE TABLE `ci_framework_indicator`  (
  `id` bigint(16) NOT NULL AUTO_INCREMENT,
  `indicator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指标名称',
  `indicator_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指标描述',
  `indicator_level` int(2) NULL DEFAULT NULL COMMENT '指标层级',
  `head_flag` tinyint(1) NULL DEFAULT NULL COMMENT '是否是表头',
  `ci_framework_object_id` bigint(16) NULL DEFAULT NULL COMMENT '综合指数架构对象id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13134 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ci_framework_object
-- ----------------------------
DROP TABLE IF EXISTS `ci_framework_object`;
CREATE TABLE `ci_framework_object`  (
  `id` bigint(16) NOT NULL AUTO_INCREMENT,
  `framework_object_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指标架构名称',
  `max_depth` int(2) NULL DEFAULT NULL COMMENT '最深的层级',
  `file_url` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件url路径',
  `upload_date` datetime(0) NULL DEFAULT NULL COMMENT '上传时间',
  `uploader_id` bigint(16) NULL DEFAULT NULL COMMENT '上传者id',
  `data_first_column` int(2) NULL DEFAULT NULL COMMENT '第一列数据列的下标（列数/深度）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ci_framework_treepath
-- ----------------------------
DROP TABLE IF EXISTS `ci_framework_treepath`;
CREATE TABLE `ci_framework_treepath`  (
  `ancestor` bigint(16) NOT NULL COMMENT '祖先节点id',
  `descendant` bigint(16) NOT NULL COMMENT '后代节点id',
  `path_depth` int(2) NULL DEFAULT NULL COMMENT '节点深度，自身为0',
  `ci_framework_object_id` bigint(20) NULL DEFAULT NULL COMMENT '综合指数架构对象id',
  PRIMARY KEY (`ancestor`, `descendant`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for composite_indicator
-- ----------------------------
DROP TABLE IF EXISTS `composite_indicator`;
CREATE TABLE `composite_indicator`  (
  `id` bigint(16) NOT NULL,
  `ci_framework_object_id` bigint(16) NULL DEFAULT NULL COMMENT '架构对象的id',
  `com_indicator` json NULL COMMENT '计算得到的综合指标',
  `use_algorithm` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所用的算法',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
