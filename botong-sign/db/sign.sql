
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sign_config
-- ----------------------------
DROP TABLE IF EXISTS `sign_config`;
CREATE TABLE `sign_config` (
  `id` bigint(36) NOT NULL COMMENT '主键',
  `org_id` bigint(36) DEFAULT NULL COMMENT '主键',
  `is_open_photo` tinyint(1) DEFAULT '1' COMMENT '是否允许上传图片',
  `is_distance` tinyint(1) DEFAULT '0' COMMENT '是否允许微调',
  `distance` int(11) DEFAULT '100' COMMENT '微调距离',
  `is_open_time` tinyint(1) DEFAULT '0' COMMENT '是否开设置签到时间',
  `start_time` varchar(5) DEFAULT NULL COMMENT '开启时间',
  `end_time` varchar(5) DEFAULT NULL COMMENT '结束时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到配置';

-- ----------------------------
-- Table structure for sign_config_daily
-- ----------------------------
DROP TABLE IF EXISTS `sign_config_daily`;
CREATE TABLE `sign_config_daily` (
  `id` bigint(36) NOT NULL COMMENT '主键',
  `org_id` bigint(36) DEFAULT NULL COMMENT '主键',
  `is_open_photo` tinyint(1) DEFAULT '1' COMMENT '主键',
  `is_distance` tinyint(1) DEFAULT '1' COMMENT '是否允许微调',
  `distance` int(11) DEFAULT '100' COMMENT '微调距离',
  `is_open_time` tinyint(1) DEFAULT '0' COMMENT '是否开设置签到时间',
  `start_time` varchar(20) DEFAULT NULL COMMENT '开启时间',
  `end_time` varchar(20) DEFAULT NULL COMMENT '结束时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sign_detail
-- ----------------------------
DROP TABLE IF EXISTS `sign_detail`;
CREATE TABLE `sign_detail` (
  `id` bigint(36) NOT NULL COMMENT '主键',
  `org_id` bigint(36) DEFAULT NULL COMMENT '机构id',
  `user_id` bigint(36) DEFAULT NULL COMMENT '机构id',
  `address_title` varchar(500) DEFAULT NULL COMMENT '签到地址标头',
  `address` varchar(500) DEFAULT NULL COMMENT '签到详细地址',
  `lng` varchar(20) DEFAULT NULL COMMENT '签到经度',
  `lat` varchar(20) DEFAULT NULL COMMENT '签到纬度',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `count` int(11) DEFAULT NULL COMMENT '签到次数',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  `deptIds` varchar(500) DEFAULT '' COMMENT '部门ids',
  `deptNames` varchar(500) DEFAULT '' COMMENT '部门名称',
  `userName` varchar(50) DEFAULT '' COMMENT '成员名称',
  `postName` varchar(255) DEFAULT '' COMMENT '职位名称',
  PRIMARY KEY (`id`),
  KEY `index_userId` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到明细';

-- ----------------------------
-- Table structure for sign_detail_daily
-- ----------------------------
DROP TABLE IF EXISTS `sign_detail_daily`;
CREATE TABLE `sign_detail_daily` (
  `id` bigint(36) NOT NULL COMMENT '主键',
  `org_id` bigint(36) DEFAULT NULL COMMENT '机构id',
  `user_id` bigint(36) DEFAULT NULL COMMENT '机构id',
  `address_title` varchar(500) DEFAULT NULL COMMENT '签到地址标头',
  `address` varchar(2000) DEFAULT NULL COMMENT '签到详细地址',
  `lng` varchar(20) DEFAULT NULL COMMENT '签到经度',
  `lat` varchar(20) DEFAULT NULL COMMENT '签到纬度',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(2000) DEFAULT NULL COMMENT '备注',
  `count` int(11) DEFAULT NULL COMMENT '签到次数',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到明细';

-- ----------------------------
-- Table structure for sign_detail_img
-- ----------------------------
DROP TABLE IF EXISTS `sign_detail_img`;
CREATE TABLE `sign_detail_img` (
  `id` bigint(36) NOT NULL COMMENT '主键',
  `sign_detail_id` bigint(36) DEFAULT NULL COMMENT '签到明细id',
  `url` varchar(400) DEFAULT NULL COMMENT '图片地址',
  `sort` tinyint(4) DEFAULT NULL COMMENT '图片排序',
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sign_detail_img_daily
-- ----------------------------
DROP TABLE IF EXISTS `sign_detail_img_daily`;
CREATE TABLE `sign_detail_img_daily` (
  `id` bigint(36) NOT NULL COMMENT '主键',
  `sign_detail_id` bigint(36) DEFAULT NULL COMMENT '签到明细id',
  `url` varchar(400) DEFAULT NULL COMMENT '图片地址',
  `sort` tinyint(4) DEFAULT NULL COMMENT '图片排序',
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
