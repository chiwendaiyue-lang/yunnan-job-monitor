SET NAMES utf8mb4;

INSERT INTO sys_role (code, name, description) VALUES
('ENTERPRISE', '企业用户', '填报与查询本企业数据'),
('CITY', '市用户', '审核辖区内企业上报'),
('PROVINCE', '省用户', '备案、终审、系统管理');

INSERT INTO enterprise (
  region_city, region_county, region_area, org_code, name,
  nature_level1, nature_level2, industry_level1, industry_level2, main_business,
  contact_name, address_level1, address_level2, address_detail, postal_code, phone, fax, email,
  filing_status
) VALUES (
  '530100', '530102', '主城区', '530000001', '云南示例制造有限公司',
  '内资', '有限责任公司', '制造业', '通用设备制造', '通用零部件制造',
  '张三', '云南省', '昆明市', '示例路1号', '650000', '0871-12345678', '0871-12345679', 'demo@example.com',
  'APPROVED'
);

-- 密码均为 password123 (BCrypt)
INSERT INTO sys_user (username, password_hash, display_name, role_id, region_code, enterprise_id, active) VALUES
('province', '$2b$10$E/FP1Bs.U1plxFm0p6lFaO8hFDZLCQS5HkoHPnioBdrJ7oZnVMFUq', '省管理员', 3, '530100', NULL, 1),
('city', '$2b$10$E/FP1Bs.U1plxFm0p6lFaO8hFDZLCQS5HkoHPnioBdrJ7oZnVMFUq', '市管理员', 2, '530100', NULL, 1),
('enterprise', '$2b$10$E/FP1Bs.U1plxFm0p6lFaO8hFDZLCQS5HkoHPnioBdrJ7oZnVMFUq', '示例企业', 1, NULL, 1, 1);

INSERT INTO survey_period (name, report_start, report_end, active) VALUES
('2026年3月调查期', '2026-03-01 00:00:00', '2026-12-31 23:59:59', 1);

INSERT INTO employment_report (
  enterprise_id, survey_period_id, archive_employment, survey_employment,
  other_reason, status, submitted_at, city_reviewed_at, province_reviewed_at
) VALUES (
  1, 1, 120, 118, '无', 'PROVINCE_APPROVED', NOW(), NOW(), NOW()
);

INSERT INTO notification (title, content, publisher_user_id, publisher_unit) VALUES
('关于开展2026年一季度就业监测填报的通知', '请各企业于调查期内登录系统完成数据填报，如有问题请联系主管部门。', 1, '云南省人力资源和社会保障厅');
