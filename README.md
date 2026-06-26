# 英语四六级服务平台（CET Prep Platform）

## 项目简介

基于我自己在备考四六级时遇到的一些问题，在通过使用主流的背单词软件之后的一些痛点，打算自己开发一个线上的备考辅助平台。

首先是最基础的背单词功能，就使用大部分背单词软件的模式。

然后我对于这个项目的要求是，我们自己线下有相应的纸质版题目，这个平台只是辅助。

从听力开始，我可以选择某一套听力的题，可以调节倍速，选择是否打开原文，是否打开相应的译文，听完之后选择答案并提交，后台自动核对成绩。

对于阅读，也是提供跟纸质版完全相同的原文和题目，题目可以选择答案，最主要的是，我在做完题之后总有一些单词不认识而且特别影响判断，对完答案虽然有原文解析，但是在句子里找单词太麻烦，还要去单词软件里查，想回顾还需要用手写抄在笔记本上，太浪费时间，所以我想在所有能看见单词的地方，都可以通过点击此单词立即查看对应的意思，并且可以加入生词本，然后对于所有部分，我们练习完之后都可以一键筛选这篇文章的重点单词（已经提前添加过的可以跳过），然后自己选择觉得重要的不认识的一并加入生词本，以后第二天练习的时候就可以在原有背单词的基础上，着重看自己的生词本，这都是自己实战做题遇到的重点词，很大概率会在考试中能用到

对于写作和翻译，提供上传图片的功能，然后提供简单的AI自主识别功能，并跟原文相对比，指出最基础的单词或语法错误，再给出需要改进的语句情况


## 技术栈

### 前端
- **框架**：Vue 3（Composition API）
- **构建工具**：Vite 5
- **路由**：Vue Router 4
- **状态管理**：Pinia
- **HTTP 客户端**：Axios
- **UI 组件库**：
  - 用户端：`Vant 4`（移动端/PC 响应式）
  - 管理端：`Element Plus`
- **其他**：
  - `vue3-audio-player`：听力音频播放
  - `echarts`：数据可视化（成绩趋势、看板）

### 后端
- **框架**：Spring Boot 2.7.18
- **持久层**：MyBatis + MyBatis-Plus
- **数据库**：MySQL 8.0
- **缓存**：Redis（预留配置，用于热点数据缓存）
- **安全**：Spring Security + JWT
- **接口文档**：Knife4j（Swagger 增强）
- **工具库**：Hutool、Lombok、MapStruct
- **消息队列**：Spring 异步 + 线程池（预留 MQ 扩展点）

### AI / 文件
- **AI 集成**：预留大模型 HTTP 调用封装（可对接 OpenAI / 智谱 / 通义千问）
- **文件存储**：本地磁盘存储（`uploads/` 目录），预留阿里云 OSS 配置

## 项目结构

```
CET-Platform/
├── README.md                      # 本文件
├── database/
│   └── schema.sql                 # MySQL 建表脚本 + 初始数据
├── backend/                       # Spring Boot 后端
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/cetprep/
│           │   ├── CetPrepApplication.java
│           │   ├── config/        # Security、Web、MyBatis、Knife4j 配置
│           │   ├── controller/    # Controller 层（用户、题库、试卷、单词、订单、管理）
│           │   ├── service/       # Service 层
│           │   ├── mapper/        # MyBatis Mapper 接口
│           │   ├── entity/        # 实体类（对应数据库表）
│           │   ├── dto/           # 请求/响应 DTO
│           │   ├── vo/            # 视图对象
│           │   ├── security/      # JWT 过滤器、Token 工具、认证入口
│           │   ├── exception/     # 全局异常处理、业务异常
│           │   ├── advice/        # 统一响应封装（Result）
│           │   ├── task/          # 异步任务（AI 词汇提取等）
│           │   └── util/          # 工具类
│           └── resources/
│               ├── application.yml
│               ├── application-dev.yml
│               └── mapper/*.xml   # MyBatis XML
├── frontend/                      # Vue 3 前端（用户端 + 管理端双入口）
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── main.js                # 用户端入口
│       ├── admin-main.js          # 管理端入口
│       ├── api/                   # Axios 封装的接口请求
│       ├── stores/                # Pinia 状态管理
│       ├── router/                # 路由配置
│       ├── views/
│       │   ├── user/              # 用户端页面（登录、注册、首页、刷题、单词、个人中心）
│       │   └── admin/             # 管理端页面（登录、看板、用户、题库、订单）
│       ├── components/            # 公共组件
│       └── utils/                 # 工具函数
└── uploads/                       # 本地文件上传目录（音频、图片、PDF）
```

## 数据库设计（核心表）

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户基础信息、学习目标、学习时长 |
| `sys_word` | 词库（词根、释义、等级、音频 URL） |
| `user_vocabulary` | 用户生词本（复习状态、下次复习时间） |
| `question` | 题目表（题型、题干、选项、答案、解析） |
| `exam_paper` | 试卷表（真题/模拟、建议用时、难度） |
| `paper_question` | 试卷-题目关联表 |
| `user_answer_record` | 用户做题记录（答案、耗时、对错） |
| `word_suggestion` | 题目/文章关联的建议词汇表（权重） |
| `composition_translation` | 作文/翻译提交记录（文本、AI 批改、人工批改状态） |
| `cet_score` | 四六级成绩（手动录入或接口查询） |
| `mall_product` | 商城商品（实体书、电子资料、课程） |
| `mall_order` | 商城订单 / 人工精批订单 |
| `daily_study_plan` | 每日背单词计划（艾宾浩斯曲线） |

## 快速启动

### 1. 环境准备
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 18+
- Redis

### 2. 初始化数据库
```bash
# 登录 MySQL，创建数据库并执行脚本
mysql -u root -p
CREATE DATABASE cet_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cet_platform;
SOURCE database/schema.sql;
```

### 3. 启动后端
```bash
cd backend
# 修改 application-dev.yml 中的数据库账号密码
mvn clean install
mvn spring-boot:run
# 后端默认运行在 http://localhost:8080
# 接口文档：http://localhost:8080/doc.html （Knife4j）
```

### 4. 启动前端
```bash
cd frontend
npm install
npm run dev
# 用户端：http://localhost:5173
# 管理端：http://localhost:5173/admin.html
```

## 默认账号
- **用户端**：注册即可使用
- **管理端**：账号 `admin`，密码 `123456`（角色 ROLE_ADMIN）

## 功能清单

- [x] 用户注册/登录（JWT）
- [x] 个人中心（信息修改、学习统计）
- [x] 成绩查询与手动录入
- [x] 阅读模块刷题（计时、暂存、提交、解析、错题本）
- [x] 听力模块播放器（音频分段、倍速、原文高亮）
- [x] 写作 & 翻译（AI 基础批改预留接口、人工精批订单）
- [x] 生词本 & 智能词汇推荐
- [x] 背单词（艾宾浩斯遗忘曲线、卡片学习、听音选义）
- [x] 商城与订单（积分兑换、模拟支付）
- [x] 后台管理（用户、题库导入、试卷、订单、看板）

## 注意事项
1. **AI 模块**：当前已封装 HTTP 调用工具类和预留接口，实际对接时需填入大模型 API Key。
2. **OSS / 支付**：本地存储模式已可用，生产环境可在 `application.yml` 切换为阿里云 OSS 和真实支付配置。
3. **Redis**：已配置但非强依赖，未启动 Redis 时系统会降级为直接查库。
4. **语音评测**：口语录音上传已可用，语音识别与评分需接入对应云厂商 API（如阿里云智能语音）。
