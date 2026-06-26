-- ==========================================
-- CET-Platform 数据库完整初始化脚本
-- 包含：建表 + 测试用户
-- ==========================================

-- 1. 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(64) NOT NULL COMMENT '账号',
    password VARCHAR(128) NOT NULL COMMENT '加密密码',
    nickname VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    school VARCHAR(128) DEFAULT NULL COMMENT '学校',
    grade VARCHAR(32) DEFAULT NULL COMMENT '年级',
    target_score INT DEFAULT 425 COMMENT '目标分数',
    study_duration INT DEFAULT 0 COMMENT '学习时长(分钟)',
    points INT DEFAULT 0 COMMENT '积分',
    role ENUM('USER','ADMIN','TEACHER') DEFAULT 'USER' COMMENT '角色',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 单词表
CREATE TABLE IF NOT EXISTS sys_word (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '单词ID',
    word VARCHAR(64) NOT NULL COMMENT '单词',
    phonetic VARCHAR(128) DEFAULT NULL COMMENT '音标',
    meaning TEXT NOT NULL COMMENT '释义',
    example_sentence TEXT DEFAULT NULL COMMENT '例句',
    root VARCHAR(64) DEFAULT NULL COMMENT '词根',
    level ENUM('CET4','CET6') DEFAULT 'CET4' COMMENT '等级',
    audio_url VARCHAR(255) DEFAULT NULL COMMENT '音频URL',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_word_level (word, level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单词表';

-- 3. 用户生词本
CREATE TABLE IF NOT EXISTS user_vocabulary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    word_id BIGINT NOT NULL COMMENT '单词ID',
    status ENUM('NEW','REVIEW','MASTERED') DEFAULT 'NEW' COMMENT '状态',
    proficiency TINYINT DEFAULT 0 COMMENT '熟练度 0-2',
    next_review_date DATE DEFAULT NULL COMMENT '下次复习日期',
    add_source ENUM('MANUAL','QUESTION','RECOMMEND') DEFAULT 'MANUAL' COMMENT '添加来源',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_word (user_id, word_id),
    KEY idx_user_review (user_id, next_review_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户生词本';

-- 4. 题目表
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '题目ID',
    type ENUM('LISTENING','READING','WRITING','TRANSLATION') NOT NULL COMMENT '题型',
    sub_type VARCHAR(32) DEFAULT NULL COMMENT '子类型 如短篇新闻/长对话/仔细阅读',
    content TEXT NOT NULL COMMENT '题干内容',
    options JSON DEFAULT NULL COMMENT '选项 JSON',
    answer VARCHAR(255) DEFAULT NULL COMMENT '正确答案',
    analysis TEXT DEFAULT NULL COMMENT '解析',
    listening_text TEXT DEFAULT NULL COMMENT '听力原文',
    audio_url VARCHAR(255) DEFAULT NULL COMMENT '音频URL',
    audio_segments JSON DEFAULT NULL COMMENT '音频分段时间点 JSON',
    key_words TEXT DEFAULT NULL COMMENT '关键词',
    difficulty TINYINT DEFAULT 3 COMMENT '难度 1-5',
    score INT DEFAULT 2 COMMENT '分值',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_type (type, sub_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 5. 试卷表
CREATE TABLE IF NOT EXISTS exam_paper (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '试卷ID',
    title VARCHAR(128) NOT NULL COMMENT '试卷标题',
    paper_type ENUM('REAL','SIMULATION') DEFAULT 'SIMULATION' COMMENT '真题/模拟',
    year INT DEFAULT NULL COMMENT '年份',
    month ENUM('6','12') DEFAULT NULL COMMENT '月份',
    level ENUM('CET4','CET6') NOT NULL COMMENT '等级',
    listen_time INT DEFAULT 25 COMMENT '听力建议用时(分钟)',
    read_time INT DEFAULT 40 COMMENT '阅读建议用时(分钟)',
    write_time INT DEFAULT 30 COMMENT '写作建议用时(分钟)',
    trans_time INT DEFAULT 30 COMMENT '翻译建议用时(分钟)',
    total_score INT DEFAULT 710 COMMENT '总分',
    status TINYINT DEFAULT 1 COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- 6. 试卷-题目关联表
CREATE TABLE IF NOT EXISTS paper_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    sort_no INT DEFAULT 0 COMMENT '排序号',
    part ENUM('LISTENING','READING','WRITING','TRANSLATION') NOT NULL COMMENT '所属部分',
    UNIQUE KEY uk_paper_question (paper_id, question_id),
    KEY idx_paper_sort (paper_id, part, sort_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关联';

-- 7. 用户做题记录
CREATE TABLE IF NOT EXISTS user_answer_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    paper_id BIGINT DEFAULT NULL COMMENT '试卷ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    user_answer TEXT DEFAULT NULL COMMENT '用户答案',
    is_correct TINYINT DEFAULT NULL COMMENT '是否正确 0-错 1-对',
    spend_time INT DEFAULT 0 COMMENT '耗时(秒)',
    answer_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
    KEY idx_user_paper (user_id, paper_id),
    KEY idx_user_question (user_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户做题记录';

-- 8. 题目建议词汇表
CREATE TABLE IF NOT EXISTS word_suggestion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    word_id BIGINT NOT NULL COMMENT '单词ID',
    weight INT DEFAULT 1 COMMENT '权重',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注 如核心词/解题关键',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_question_word (question_id, word_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目建议词汇';

-- 9. 作文翻译提交记录
CREATE TABLE IF NOT EXISTS composition_translation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type ENUM('WRITING','TRANSLATION') NOT NULL COMMENT '类型',
    title VARCHAR(255) DEFAULT NULL COMMENT '题目',
    content TEXT NOT NULL COMMENT '文本内容',
    image_url VARCHAR(255) DEFAULT NULL COMMENT '拍照图片URL',
    ai_score INT DEFAULT NULL COMMENT 'AI评分',
    ai_feedback TEXT DEFAULT NULL COMMENT 'AI批改意见',
    manual_status ENUM('NONE','PENDING','ASSIGNED','COMPLETED') DEFAULT 'NONE' COMMENT '人工批改状态',
    manual_feedback TEXT DEFAULT NULL COMMENT '人工批改意见',
    manual_image VARCHAR(255) DEFAULT NULL COMMENT '人工批改图片',
    teacher_id BIGINT DEFAULT NULL COMMENT '批改教师ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作文翻译提交';

-- 10. 成绩记录
CREATE TABLE IF NOT EXISTS cet_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    exam_type ENUM('CET4','CET6') NOT NULL COMMENT '考试类型',
    exam_date DATE DEFAULT NULL COMMENT '考试日期',
    total_score INT DEFAULT NULL COMMENT '总分',
    listen_score INT DEFAULT NULL COMMENT '听力分',
    read_score INT DEFAULT NULL COMMENT '阅读分',
    write_score INT DEFAULT NULL COMMENT '写作翻译分',
    is_manual TINYINT DEFAULT 1 COMMENT '是否手动录入',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_user (user_id, exam_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩记录';

-- 11. 商城商品
CREATE TABLE IF NOT EXISTS mall_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    name VARCHAR(128) NOT NULL COMMENT '商品名',
    type ENUM('PHYSICAL','DIGITAL','COURSE') NOT NULL COMMENT '类型',
    description TEXT DEFAULT NULL COMMENT '描述',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    points INT DEFAULT 0 COMMENT '所需积分',
    stock INT DEFAULT 0 COMMENT '库存',
    status TINYINT DEFAULT 1 COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城商品';

-- 12. 订单表
CREATE TABLE IF NOT EXISTS mall_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(64) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type ENUM('PRODUCT','CORRECTION','COURSE') NOT NULL COMMENT '订单类型',
    product_id BIGINT DEFAULT NULL COMMENT '商品ID',
    composition_id BIGINT DEFAULT NULL COMMENT '关联作文/翻译ID',
    amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '实付金额',
    points_used INT DEFAULT 0 COMMENT '使用积分',
    pay_type ENUM('WECHAT','ALIPAY','POINTS') DEFAULT NULL COMMENT '支付方式',
    status ENUM('UNPAID','PAID','SHIPPED','COMPLETED','CANCELLED') DEFAULT 'UNPAID' COMMENT '状态',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 13. 每日学习计划
CREATE TABLE IF NOT EXISTS daily_study_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    plan_date DATE NOT NULL COMMENT '计划日期',
    new_words INT DEFAULT 0 COMMENT '新词数量',
    review_words INT DEFAULT 0 COMMENT '复习数量',
    completed TINYINT DEFAULT 0 COMMENT '是否完成',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_date (user_id, plan_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日学习计划';

-- 14. 签到记录
CREATE TABLE IF NOT EXISTS user_sign_in (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    sign_date DATE NOT NULL COMMENT '签到日期',
    continuous_days INT DEFAULT 1 COMMENT '连续天数',
    points_get INT DEFAULT 5 COMMENT '获得积分',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_date (user_id, sign_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录';

-- =================== 测试用户数据 ===================
-- 管理员账号 密码: 123456
INSERT IGNORE INTO sys_user (username, password, nickname, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '管理员', 'ADMIN', 1);

-- 测试用户 zhangsan 密码: 123456
INSERT IGNORE INTO sys_user (username, password, nickname, school, grade, target_score, role, status) VALUES
('zhangsan', '$2b$10$DKKo5WEAiG3fMMeE/FfTMOePLmTbPSguf4sC4OUpbNjqxl0QFq1Ey', '张三', '示例大学', '大二', 500, 'USER', 1);
