/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : 127.0.0.1:3306
 Source Schema         : composite_indicator

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 06/09/2021 09:16:31
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
  `full_class_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '全类名',
  `step_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '步骤名称',
  `exec_order` int(2) NULL DEFAULT NULL COMMENT '执行次序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of algorithm
-- ----------------------------
INSERT INTO `algorithm` VALUES (1, 'FactorAnalysis', '因子分析', 'com.jc.research.entity.algorithm.FactorAnalysis', 'weightingAndAggregation', 2);
INSERT INTO `algorithm` VALUES (2, 'ZScores', 'z分数', 'com.jc.research.entity.algorithm.ZScores', 'normalisation', 1);

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
-- Records of algorithm_exec_step
-- ----------------------------
INSERT INTO `algorithm_exec_step` VALUES (1, '缺失值填补', 'missDataImputation', 1);
INSERT INTO `algorithm_exec_step` VALUES (2, '多变量分析', 'multivariateAnalysis', 2);
INSERT INTO `algorithm_exec_step` VALUES (3, '标准化', 'normalisation', 3);
INSERT INTO `algorithm_exec_step` VALUES (4, '权重和聚合', 'weightingAndAggregation', 4);

-- ----------------------------
-- Table structure for country
-- ----------------------------
DROP TABLE IF EXISTS `country`;
CREATE TABLE `country`  (
  `id` bigint(11) NOT NULL,
  `country_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家名称',
  `base_indicator` json NULL COMMENT '基础指标数据',
  `composite_indicator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '综合指标数据',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of country
-- ----------------------------
INSERT INTO `country` VALUES (1430718530439462913, '芬兰', '{\"exports\": 50.7, \"patents\": 187, \"internet\": 200.2, \"royalties\": 125.6, \"schooling\": 10, \"telephones\": 3.08, \"university\": 27.4, \"electricity\": 4.15}', NULL);
INSERT INTO `country` VALUES (1430719106149629953, '美国', '{\"exports\": 66.2, \"patents\": 289, \"internet\": 179.1, \"royalties\": 130, \"schooling\": 12, \"telephones\": 3, \"university\": 13.9, \"electricity\": 4.07}', NULL);

-- ----------------------------
-- Table structure for technology_achievement_index
-- ----------------------------
DROP TABLE IF EXISTS `technology_achievement_index`;
CREATE TABLE `technology_achievement_index`  (
  `id` bigint(16) NOT NULL AUTO_INCREMENT,
  `country_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `patents` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `royalties` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `internet` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `exports` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `telephones` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `electricity` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `schooling` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `university` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of technology_achievement_index
-- ----------------------------
INSERT INTO `technology_achievement_index` VALUES (1, '芬兰', '187', '125.6', '200.2', '50.7', '3.08', '4.15', '10', '27.4');
INSERT INTO `technology_achievement_index` VALUES (2, '美国', '289', '130', '179.1', '66.2', '3', '4.07', '12', '13.9');
INSERT INTO `technology_achievement_index` VALUES (3, '瑞典', '271', '156.6', '125.8', '59.7', '3.1', '4.14', '11.4', '15.3');
INSERT INTO `technology_achievement_index` VALUES (4, '日本', '994', '64.6', '49', '80.8', '3', '3.86', '9.5', '10');
INSERT INTO `technology_achievement_index` VALUES (5, '韩国', '779', '9.8', '4.8', '66.7', '2.97', '3.65', '10.8', '23.2');
INSERT INTO `technology_achievement_index` VALUES (6, '荷兰', '189', '151.2', '136', '50.9', '3.02', '3.77', '9.4', '9.5');
INSERT INTO `technology_achievement_index` VALUES (7, '英国', '82', '134', '57.4', '61.9', '3.02', '3.73', '9.4', '14.9');
INSERT INTO `technology_achievement_index` VALUES (8, '加拿大', '31', '38.6', '108', '48.7', '2.94', '4.18', '11.6', '14.2');
INSERT INTO `technology_achievement_index` VALUES (9, '澳大利亚', '75', '18.2', '125.9', '16.2', '2.94', '3.94', '10.9', '25.3');
INSERT INTO `technology_achievement_index` VALUES (10, '新加坡', '8', '25.5', '72.3', '74.9', '2.95', '3.83', '7.1', '24.2');
INSERT INTO `technology_achievement_index` VALUES (11, '德国', '235', '36.8', '41.2', '64.2', '2.94', '3.75', '10.2', '14.4');
INSERT INTO `technology_achievement_index` VALUES (12, '挪威', '103', '20.2', '193.6', '19', '3.12', '4.39', '11.9', '11.2');
INSERT INTO `technology_achievement_index` VALUES (13, '爱尔兰', '106', '110.3', '48.6', '53.6', '2.97', '3.68', '9.4', '12.3');
INSERT INTO `technology_achievement_index` VALUES (14, '比利时', '72', '73.9', '58.9', '47.6', '2.91', '3.86', '9.3', '13.6');
INSERT INTO `technology_achievement_index` VALUES (15, '新西兰', '103', '13', '146.7', '15.4', '2.86', '3.91', '11.7', '13.1');
INSERT INTO `technology_achievement_index` VALUES (16, '奥地利', '165', '14.8', '84.2', '50.3', '2.99', '3.79', '8.4', '13.6');
INSERT INTO `technology_achievement_index` VALUES (17, '法国', '205', '33.6', '36.4', '58.9', '2.97', '3.8', '7.9', '12.6');
INSERT INTO `technology_achievement_index` VALUES (18, '以色列', '74', '43.6', '43.2', '45', '2.96', '3.74', '9.6', '11');
INSERT INTO `technology_achievement_index` VALUES (19, '西班牙', '42', '8.6', '21', '53.4', '2.86', '3.62', '7.3', '15.6');
INSERT INTO `technology_achievement_index` VALUES (20, '意大利', '13', '9.8', '30.4', '51', '3', '3.65', '7.2', '13');
INSERT INTO `technology_achievement_index` VALUES (21, '捷克共和国', '28', '4.2', '25', '51.7', '2.75', '3.68', '9.5', '8.2');
INSERT INTO `technology_achievement_index` VALUES (22, '匈牙利', '26', '6.2', '21.6', '63.5', '2.73', '3.46', '9.1', '7.7');
INSERT INTO `technology_achievement_index` VALUES (23, '斯洛文尼亚', '105', '4', '20.3', '49.5', '2.84', '3.71', '7.1', '10.6');

SET FOREIGN_KEY_CHECKS = 1;
