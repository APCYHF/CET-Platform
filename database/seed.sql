-- =================== 测试用户 ===================
-- 用户: zhangsan 密码: 123456 (BCrypt加密)
INSERT IGNORE INTO sys_user (username, password, nickname, school, grade, target_score, role, status) VALUES
('zhangsan', '$2b$10$DKKo5WEAiG3fMMeE/FfTMOePLmTbPSguf4sC4OUpbNjqxl0QFq1Ey', '张三', '示例大学', '大二', 500, 'USER', 1);
