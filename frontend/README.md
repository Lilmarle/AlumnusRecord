# 校友管理系统 — 前端

基于 **Vue 3** + **Vite** 构建的前端项目，使用 Vue Router 进行路由管理。

## 技术栈

| 技术 | 说明 |
|------|------|
| [Vue 3](https://vuejs.org/) | 渐进式 JavaScript 框架（Composition API + `<script setup>`） |
| [Vite](https://vitejs.dev/) | 前端构建工具 |
| [Vue Router](https://router.vuejs.org/) | 官方路由管理 |

## 项目结构

```
frontend/
├── index.html              # 入口 HTML
├── package.json            # 依赖配置
├── vite.config.js          # Vite 配置
├── public/
│   └── favicon.ico         # 网站图标
└── src/
    ├── main.js             # 应用入口
    ├── App.vue             # 根组件
    ├── api/
    │   └── index.js        # HTTP 请求封装（GET/POST/PUT/DELETE）
    ├── assets/
    │   ├── base.css        # 全局基础样式（CSS 变量、重置）
    │   ├── main.css        # 全局主样式（入口加载）
    │   ├── home.css        # 首页/管理端/教师端共享样式
    │   └── login.css       # 登录页样式
    ├── composables/
    │   └── useLogin.js     # 登录逻辑组合式函数
    ├── router/
    │   └── index.js        # 路由配置及导航守卫
    └── views/
        ├── Login.vue       # 登录页
        ├── Home.vue        # 学生首页
        ├── AdminHome.vue   # 管理员首页
        └── TeacherHome.vue # 教师首页
```

### 目录说明

- **`api/`** — 封装了 `get`、`post`、`put`、`del` 四个请求方法，自动携带 JWT Token
- **`assets/`** — 存放独立的 CSS 样式文件（CSS、HTML、JS 分离）
- **`composables/`** — Vue 3 组合式函数，封装可复用的业务逻辑
- **`router/`** — 路由定义及 `beforeEach` 导航守卫（权限校验、自动跳转）
- **`views/`** — 页面级组件，每个文件包含 `<template>`（HTML）和 `<script setup>`（JS），样式通过 `import` 引入外部 CSS

## 快速开始

```sh
# 安装依赖
npm install

# 启动开发服务器（热重载，默认端口 5173）
npm run dev

# 构建生产版本
npm run build
```

> 开发服务器默认运行在 `http://localhost:5173`，API 请求通过 Vite 代理转发至后端 `http://localhost:8080`。

## 路由说明

| 路径 | 路由名称 | 页面 | 权限 |
|------|---------|------|------|
| `/` | `Login` | 登录页 | 公开 |
| `/home` | `Home` | 学生首页 | 需登录，角色 1 |
| `/teacher` | `TeacherHome` | 教师首页 | 需登录，角色 2 |
| `/admin` | `AdminHome` | 管理端首页 | 需登录，角色 3 |

### 导航守卫行为

- 未登录访问需认证页面 → 跳转 `/`
- 已登录访问 `/` → 根据角色自动跳转对应首页
- 访问无权限的角色页面 → 跳转对应角色首页

## 样式分离规范

本项目遵循 **CSS、HTML、JS 分离** 原则：

- **HTML** — 写在 `.vue` 文件的 `<template>` 中
- **CSS** — 提取到 `src/assets/` 下的独立 `.css` 文件，在 `<script setup>` 中用 `import` 引入
- **JS** — 写在 `.vue` 文件的 `<script setup>` 中，或抽离到 `src/composables/`、`src/api/`、`src/router/` 等独立模块
