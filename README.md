# 云南省企业就业失业数据采集系统

基于工作说明书实现的企业就业失业数据采集与多级审核 Web 应用：Spring Boot 3 后端、Vue 3 前端、MySQL 数据库。

## 目录说明

| 目录 | 说明 |
|------|------|
| `docs/` | 项目计划、需求摘要、用户手册 |
| `backend/` | Java / Spring Boot API |
| `frontend/` | Vue 3 + Vite + Element Plus |
| `database/` | 表结构、初始化数据、迁移目录 |
| `scripts/` | 部署与备份示例脚本 |

## 快速启动

### 1. 数据库

创建数据库并导入脚本（需已安装 MySQL 8+）：

```bash
mysql -u root -p -e "CREATE DATABASE yunnan_job_monitor DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p yunnan_job_monitor < database/schema.sql
mysql -u root -p yunnan_job_monitor < database/init_data.sql
```

### 2. 后端

编辑 `backend/src/main/resources/application.yml` 中的数据源账号密码，然后：

```bash
cd backend
mvn spring-boot:run
```

默认端口：`8080`，API 前缀：`/api`。

### 3. 前端

```bash
cd frontend
npm install
npm run dev
```

浏览器访问开发服务器地址（一般为 `http://localhost:5173`），通过 Vite 代理访问后端。

## 演示账号（初始化数据）

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 省级 | `province` | `password123` |
| 市级 | `city` | `password123` |
| 企业 | `enterprise` | `password123` |

## 功能范围说明

当前版本覆盖工作说明书中的核心链路：企业备案与信息维护、调查期与上报时限、就业数据填报与条件校验、市/省审核与退回、省级数据修正留痕、通知发布与浏览、基础汇总与导出、用户与角色管理、系统监控。部级数据交换与国家系统对接在文档中预留接口说明，可按实际规范扩展。
