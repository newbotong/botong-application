DROP TABLE
IF EXISTS notice;

CREATE TABLE `notice` (
  `id` bigint(20) NOT NULL COMMENT '公告id',
  `issue_user_id` bigint(32) DEFAULT NULL COMMENT '发布人用户id',
  `title` varchar(32) DEFAULT NULL COMMENT '公告标题（5-10字）',
  `content` longtext COMMENT '公告内容(2000字)',
  `cover` varchar(255) DEFAULT NULL COMMENT '公告封面图',
  `picture` varchar(255) DEFAULT NULL COMMENT '图片地址',
  `author` varchar(32) DEFAULT NULL COMMENT '作者',
  `read_num` int(11) DEFAULT '0' COMMENT '阅读人数',
  `not_read_num` int(11) DEFAULT '0' COMMENT '未读数',
  `logic_delete` tinyint(1) DEFAULT '0' COMMENT '逻辑删除0正常 1删除',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新日期',
  `secrecy_state` tinyint(1) DEFAULT '1' COMMENT '保密状态 0为保密 1为不保密',
  `dang_state` tinyint(1) DEFAULT '1' COMMENT 'Dang的状态 0为Dang 1为非Dang',
  `picture_name` varchar(255) DEFAULT NULL COMMENT '图片名称，多个名称逗号隔开',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE
IF EXISTS notice_user;

CREATE TABLE `notice_user` (
  `id` bigint(20) NOT NULL COMMENT '公告与用户关系id',
  `notice_id` varchar(20) DEFAULT NULL COMMENT '公告id',
  `user_id` varchar(20) DEFAULT NULL COMMENT '用户id',
  `state` tinyint(1) DEFAULT '1' COMMENT '是否阅读 0为已读 1为未读',
  `logic_delete` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 0正常 1删除',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新日期',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE
IF EXISTS userInfo;

CREATE TABLE `userInfo` (
  `id` bigint(22) NOT NULL COMMENT '用户id',
  `img` varchar(255) DEFAULT NULL COMMENT '用户头像地址',
  `name` varchar(32) DEFAULT NULL COMMENT '用户名称',
  `phone` bigint(11) DEFAULT NULL COMMENT '手机号',
  `logic_delete` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 0正常 1删除',
  `update_time` bigint(22) DEFAULT NULL COMMENT '更新时间',
  `create_time` bigint(22) DEFAULT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


