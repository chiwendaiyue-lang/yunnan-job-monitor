# 后端（Spring Boot）

## 构建与运行

```bash
mvn -q -DskipTests package
java -jar target/job-monitor-1.0.0-SNAPSHOT.jar
```

开发模式：

```bash
mvn spring-boot:run
```

## 配置

修改 `src/main/resources/application.yml` 中的数据库连接与 `app.jwt.secret`（生产环境务必更换）。

## API 约定

- 前缀：`/api`
- 认证：`Authorization: Bearer <token>`
- 角色：`ENTERPRISE` / `CITY` / `PROVINCE`
