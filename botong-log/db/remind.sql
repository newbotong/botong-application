DROP TABLE
IF EXISTS remind;

CREATE TABLE `remind` (
  `id` VARCHAR (32) NOT NULL COMMENT '主键',
  `task_id` VARCHAR (32) DEFAULT NULL COMMENT '任务id',
  `member_id` VARCHAR (32) NOT NULL COMMENT '用户所在企业成员id',
  `app_id` VARCHAR (32) NOT NULL COMMENT '应用id',
  `org_id` VARCHAR (32) NOT NULL COMMENT '组织架构id',
  `remind_switch` SMALLINT (1) NOT NULL DEFAULT '0' COMMENT '提醒开关（0-关闭，1-打开）',
  `submit_type` SMALLINT (1) NOT NULL COMMENT '提交类型周期（1每天 2 每周 3 每月 4 季度 5 年度）',
  `cycle` VARCHAR (20) DEFAULT NULL COMMENT '提醒周期（日 01:30，周 1-7，月 7号，一次发送：2018-03-23）',
  `cycle_type` VARCHAR (20) DEFAULT NULL COMMENT '周期类型（day:日 周：week 月：month 一次发送：once）',
  `job_time` VARCHAR (20) DEFAULT NULL COMMENT '提醒时间（如 15:20）',
  `remind_mode` SMALLINT (1) DEFAULT NULL COMMENT '提醒方式（0-应用内推送，1-短信，2-dang）',
  `is_manager` SMALLINT (1) DEFAULT 0 COMMENT '是否是管理员 0-不是，1-是',
  `create_time` BIGINT (20) DEFAULT NULL COMMENT '创建时间',
  `update_time` BIGINT (20) DEFAULT NULL COMMENT '修改时间',
  `is_delete` SMALLINT (1) DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '提醒';