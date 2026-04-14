# 前端（Vue 3）

## 开发

```bash
npm install
npm run dev
```

通过 Vite 将 `/api` 代理到 `http://localhost:8080`，请先启动后端并导入数据库初始化脚本。

## 构建

```bash
npm run build
```

静态资源输出到 `dist/`，可由 Nginx 等反向代理到后端 API。
