
DROP TABLE
IF EXISTS info_catalog;
CREATE TABLE `info_catalog` (
  `id` bigint(22) NOT NULL COMMENT '主键id',
  `name` varchar(12) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
  `org_id` bigint(22) unsigned zerofill DEFAULT NULL COMMENT '公司id',
  `parent_id` bigint(22) DEFAULT NULL COMMENT '父id',
  `level` tinyint(2) DEFAULT '1' COMMENT '级别 从1开始',
  `sort` tinyint(4) DEFAULT '1' COMMENT '展示顺序 从1开始',
  `whether_show` tinyint(1) DEFAULT '1' COMMENT '是否显示0：否，1：是',
  `is_delete` tinyint(1) DEFAULT NULL COMMENT '是否删除 0：未删除；1：已删除',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资讯目录';

DROP TABLE
IF EXISTS info_content;
CREATE TABLE `info_content` (
  `id` bigint(22) NOT NULL COMMENT '主键id',
  `catalog_id` bigint(22) DEFAULT NULL COMMENT '目录id',
  `org_id` bigint(22) DEFAULT NULL COMMENT '公司id',
  `department_name` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '部门名称',
  `title` varchar(35) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `picture_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片地址',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `read_number` int(11) DEFAULT '0' COMMENT '阅读数量',
  `whether_show` tinyint(1) DEFAULT '1' COMMENT '是否显示0：否，1：是',
  `sort` tinyint(6) DEFAULT '0' COMMENT '展示顺序',
  `is_delete` tinyint(1) DEFAULT NULL COMMENT '是否删除 0：未删除；1：已删除',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资讯内容';

DROP TABLE
IF EXISTS info_dictionary;
CREATE TABLE `info_dictionary` (
  `id` bigint(22) NOT NULL COMMENT '资讯字典id',
  `name` varchar(20) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '资讯字典名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


