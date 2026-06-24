-- 英语四六级服务平台数据库脚本
-- 数据库: cet_platform
-- 字符集: utf8mb4

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

-- =================== 初始数据 ===================

-- 管理员账号 密码: 123456 (BCrypt加密)
INSERT INTO sys_user (username, password, nickname, role, status) VALUES ('admin', 'SIMPLE_HASH', '管理员', 'ADMIN', 1);

-- 初始单词数据（部分 CET4 高频词）
INSERT INTO sys_word (word, phonetic, meaning, example_sentence, root, level) VALUES
('abandon', '/əˈbændən/', 'v. 放弃，抛弃', 'He abandoned his car and ran for help.', 'a-', 'CET4'),
('ability', '/əˈbɪləti/', 'n. 能力，才能', 'She has the ability to solve complex problems.', '-ility', 'CET4'),
('absolute', '/ˈæbsəluːt/', 'adj. 绝对的，完全的', 'I have absolute confidence in you.', 'ab-', 'CET4'),
('academic', '/ˌækəˈdemɪk/', 'adj. 学术的，学院的', 'The university offers various academic programs.', 'academ-', 'CET4'),
('access', '/ˈækses/', 'n. 通道，入口；v. 存取', 'Students have access to the library.', 'ac-', 'CET4'),
('accompany', '/əˈkʌmpəni/', 'v. 陪伴，伴随', 'She accompanied me to the hospital.', 'ac-', 'CET4'),
('accomplish', '/əˈkʌmplɪʃ/', 'v. 完成，实现', 'We need to accomplish this task by Friday.', 'ac-', 'CET4'),
('account', '/əˈkaʊnt/', 'n. 账户，解释', 'I opened a bank account yesterday.', 'count', 'CET4'),
('accumulate', '/əˈkjuːmjəleɪt/', 'v. 积累，积聚', 'Dust began to accumulate on the shelves.', 'cumul', 'CET4'),
('accurate', '/ˈækjərət/', 'adj. 准确的，精确的', 'The report was accurate and well-researched.', 'cur', 'CET4');

-- 初始阅读题示例（2道仔细阅读）
INSERT INTO question (type, sub_type, content, options, answer, analysis, difficulty, score) VALUES
('READING', 'READING_COMPREHENSION',
'Passage: In recent years, the concept of lifelong learning has gained significant attention...\n\nQuestion: What is the main idea of the passage?',
'["A. Education should only happen in schools.","B. Learning is a continuous process throughout life.","C. Technology has replaced traditional learning methods.","D. Older people cannot learn new skills."]',
'B',
'本文主要讲述了终身学习的概念，强调学习是一个贯穿一生的持续过程，因此选B。A与文意相反，C和D文中未提及。',
3, 2),
('READING', 'READING_COMPREHENSION',
'Passage: Scientists have discovered a new species of frog in the Amazon rainforest...\n\nQuestion: Where was the new species discovered?',
'["A. In Africa.","B. In the Amazon rainforest.","C. In Australia.","D. In the Arctic."]',
'B',
'细节理解题。根据文章第一句“Scientists have discovered a new species of frog in the Amazon rainforest”可知答案为B。',
2, 2);

-- 初始试卷示例
INSERT INTO exam_paper (title, paper_type, year, month, level, listen_time, read_time, write_time, trans_time) VALUES
('2023年6月大学英语四级真题（模拟）', 'REAL', 2023, '6', 'CET4', 25, 40, 30, 30);

-- 关联试卷题目
INSERT INTO paper_question (paper_id, question_id, sort_no, part) VALUES
(1, 1, 1, 'READING'),
(1, 2, 2, 'READING');

-- 初始商品
INSERT INTO mall_product (name, type, description, price, points, stock) VALUES
('历年四级真题卷（实体书）', 'PHYSICAL', '包含近5年真题及详细解析', 39.90, 390, 100),
('六级高频词汇PDF', 'DIGITAL', '精选2000个六级高频词汇及例句', 9.90, 99, 999),
('写作高分技巧录播课', 'COURSE', '10节课程，涵盖各类写作题型', 59.90, 599, 999);
