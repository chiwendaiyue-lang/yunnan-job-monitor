-- 云南省企业就业失业数据采集系统 - 表结构 (MySQL 8+)
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS data_correction;
DROP TABLE IF EXISTS employment_report;
DROP TABLE IF EXISTS survey_period;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS enterprise;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_role;

CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL UNIQUE COMMENT '角色编码',
  name VARCHAR(128) NOT NULL COMMENT '角色名称',
  description VARCHAR(512) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色';

CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  display_name VARCHAR(128) NULL,
  role_id BIGINT NOT NULL,
  region_code VARCHAR(32) NULL COMMENT '市/县编码，企业用户可空由企业表带出',
  enterprise_id BIGINT NULL COMMENT '企业用户关联企业',
  active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

CREATE TABLE enterprise (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  region_province VARCHAR(32) NOT NULL DEFAULT '530000',
  region_city VARCHAR(32) NOT NULL COMMENT '所属地市',
  region_county VARCHAR(32) NOT NULL COMMENT '所属市县',
  region_area VARCHAR(128) NULL COMMENT '所处区域',
  org_code VARCHAR(9) NOT NULL UNIQUE COMMENT '组织机构代码',
  name VARCHAR(256) NOT NULL,
  nature_level1 VARCHAR(64) NOT NULL,
  nature_level2 VARCHAR(64) NOT NULL,
  industry_level1 VARCHAR(64) NOT NULL,
  industry_level2 VARCHAR(64) NOT NULL,
  main_business VARCHAR(512) NOT NULL,
  contact_name VARCHAR(128) NOT NULL,
  address_level1 VARCHAR(64) NOT NULL,
  address_level2 VARCHAR(64) NOT NULL,
  address_detail VARCHAR(256) NOT NULL,
  postal_code CHAR(6) NOT NULL,
  phone VARCHAR(64) NOT NULL,
  fax VARCHAR(64) NOT NULL,
  email VARCHAR(128) NULL,
  filing_status VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT,SUBMITTED,APPROVED,REJECTED',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业备案';

CREATE TABLE survey_period (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL COMMENT '调查期名称',
  report_start DATETIME NOT NULL,
  report_end DATETIME NOT NULL,
  active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调查期与上报时限';

CREATE TABLE employment_report (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  enterprise_id BIGINT NOT NULL,
  survey_period_id BIGINT NOT NULL,
  archive_employment INT NOT NULL COMMENT '建档期就业人数',
  survey_employment INT NOT NULL COMMENT '调查期就业人数',
  reduce_type VARCHAR(128) NULL COMMENT '就业人数减少类型',
  main_reason VARCHAR(128) NULL,
  main_reason_note VARCHAR(512) NULL,
  secondary_reason VARCHAR(128) NULL,
  secondary_reason_note VARCHAR(512) NULL,
  third_reason VARCHAR(128) NULL,
  third_reason_note VARCHAR(512) NULL,
  other_reason VARCHAR(512) NOT NULL DEFAULT '',
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT,SUBMITTED,CITY_APPROVED,PROVINCE_APPROVED,RETURNED,REJECTED',
  return_remark VARCHAR(1024) NULL COMMENT 'CC-001 退回备注',
  returned_at DATETIME NULL,
  returned_by_user_id BIGINT NULL,
  submitted_at DATETIME NULL,
  city_reviewed_at DATETIME NULL,
  province_reviewed_at DATETIME NULL,
  reported_to_national TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已标记上报部级',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_ent_period (enterprise_id, survey_period_id),
  CONSTRAINT fk_report_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise(id),
  CONSTRAINT fk_report_period FOREIGN KEY (survey_period_id) REFERENCES survey_period(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就业填报';

CREATE TABLE data_correction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  report_id BIGINT NOT NULL,
  operator_user_id BIGINT NOT NULL,
  field_name VARCHAR(64) NOT NULL,
  original_value TEXT NULL,
  new_value TEXT NOT NULL,
  reason VARCHAR(512) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_corr_report FOREIGN KEY (report_id) REFERENCES employment_report(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='省级修正留痕';

CREATE TABLE notification (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  content VARCHAR(2000) NOT NULL,
  publisher_user_id BIGINT NOT NULL,
  publisher_unit VARCHAR(256) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_notif_user FOREIGN KEY (publisher_user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知';

CREATE TABLE audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NULL,
  action VARCHAR(64) NOT NULL,
  entity_type VARCHAR(64) NOT NULL,
  entity_id BIGINT NULL,
  detail_json JSON NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_audit_time (created_at),
  INDEX idx_audit_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CC-003 审计日志';

SET FOREIGN_KEY_CHECKS = 1;
