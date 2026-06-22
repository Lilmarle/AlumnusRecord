# 校友管理系统 — 接口文档

> 基础路径：`http://localhost:8080`

---

## 1. 用户登录

### 1.1 接口说明

支持通过 **用户名** 或 **手机号** 配合 **密码** 进行登录认证，登录成功后返回 JWT Token。

### 1.2 请求信息

| 项目 | 内容 |
|------|------|
| **URL** | `/user/login` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |

### 1.3 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `account` | `String` | 是 | 用户名或手机号 |
| `password` | `String` | 是 | 密码（明文） |

**请求示例：**

```json
{
    "account": "zhangsan",
    "password": "123456"
}
```

或使用手机号登录：

```json
{
    "account": "13800138000",
    "password": "123456"
}
```

### 1.4 响应参数

#### 成功响应（HTTP 200）

| 参数名 | 类型 | 说明 |
|--------|------|------|
| `code` | `int` | 状态码，成功为 `200` |
| `message` | `String` | 提示信息，成功为 `"操作成功"` |
| `data` | `object` | 响应数据 |

**`data` 字段说明：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `token` | `String` | JWT 令牌，有效期 24 小时 |
| `userId` | `int` | 用户 ID |
| `username` | `String` | 用户名 |
| `phone` | `String` | 手机号 |
| `role` | `int` | 角色：1-学生，2-老师，3-管理员 |

**成功响应示例：**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6InpoYW5nc2FuIiwiZXhwIjoxNzQ4MzI5MjUyLCJpYXQiOjE3NDgyNDI4NTJ9.xxx",
        "userId": 1,
        "username": "zhangsan",
        "phone": "13800138000",
        "role": 1
    }
}
```

#### 错误响应

**账号不存在（HTTP 400）：**

```json
{
    "code": 400,
    "message": "用户名或手机号不存在",
    "data": null
}
```

**密码错误（HTTP 400）：**

```json
{
    "code": 400,
    "message": "密码错误",
    "data": null
}
```

**账号被禁用（HTTP 400）：**

```json
{
    "code": 400,
    "message": "账号已被禁用",
    "data": null
}
```

**参数缺失（HTTP 400）：**

```json
{
    "code": 400,
    "message": "请输入用户名或手机号",
    "data": null
}
```

### 1.5 状态码说明

| 状态码 | 说明 |
|--------|------|
| `200` | 登录成功 |
| `400` | 请求参数错误或业务校验失败 |
| `401` | 未授权（Token 无效或过期） |

---

## 2. 用户注册

### 2.1 接口说明

支持通过 **用户名**、**密码**、**手机号** 和 **角色** 进行注册，注册成功后返回用户基本信息。

### 2.2 请求信息

| 项目 | 内容 |
|------|------|
| **URL** | `/user/register` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |

### 2.3 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `username` | `String` | 是 | 用户名 |
| `password` | `String` | 是 | 密码（明文，至少6位） |
| `phone` | `String` | 是 | 手机号 |
| `role` | `int` | 是 | 角色：1-学生，2-老师，3-管理员 |

**请求示例：**

```json
{
    "username": "lisi",
    "password": "123456",
    "phone": "13900139000",
    "role": 1
}
```

### 2.4 响应参数

#### 成功响应（HTTP 200）

| 参数名 | 类型 | 说明 |
|--------|------|------|
| `code` | `int` | 状态码，成功为 `200` |
| `message` | `String` | 提示信息，成功为 `"操作成功"` |
| `data` | `object` | 响应数据 |

**`data` 字段说明：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `userId` | `int` | 用户 ID |
| `username` | `String` | 用户名 |
| `phone` | `String` | 手机号 |
| `role` | `int` | 角色：1-学生，2-老师，3-管理员 |

**成功响应示例：**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "userId": 2,
        "username": "lisi",
        "phone": "13900139000",
        "role": 1
    }
}
```

#### 错误响应

**用户名已存在（HTTP 400）：**

```json
{
    "code": 400,
    "message": "用户名已存在",
    "data": null
}
```

**手机号已被注册（HTTP 400）：**

```json
{
    "code": 400,
    "message": "手机号已被注册",
    "data": null
}
```

---

## 3. 修改密码

### 3.1 接口说明

用户登录后，通过提供 **旧密码** 验证身份，然后设置 **新密码**。新密码使用 BCrypt 加密存储。

### 3.2 请求信息

| 项目 | 内容 |
|------|------|
| **URL** | `/user/changePassword` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |

### 3.3 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `userId` | `int` | 是 | 用户 ID |
| `oldPassword` | `String` | 是 | 旧密码（明文） |
| `newPassword` | `String` | 是 | 新密码（明文，至少6位） |

**请求示例：**

```json
{
    "userId": 1,
    "oldPassword": "123456",
    "newPassword": "654321"
}
```

### 3.4 响应参数

#### 成功响应（HTTP 200）

| 参数名 | 类型 | 说明 |
|--------|------|------|
| `code` | `int` | 状态码，成功为 `200` |
| `message` | `String` | 提示信息，成功为 `"密码修改成功"` |
| `data` | `null` | 响应数据为 null |

**成功响应示例：**

```json
{
    "code": 200,
    "message": "密码修改成功",
    "data": null
}
```

#### 错误响应

**旧密码错误（HTTP 400）：**

```json
{
    "code": 400,
    "message": "旧密码错误",
    "data": null
}
```

**用户不存在（HTTP 400）：**

```json
{
    "code": 400,
    "message": "用户不存在",
    "data": null
}
```

**新密码长度不足（HTTP 400）：**

```json
{
    "code": 400,
    "message": "新密码长度不能少于6位",
    "data": null
}
```

**参数缺失（HTTP 400）：**

```json
{
    "code": 400,
    "message": "请输入旧密码",
    "data": null
}
```

### 3.5 状态码说明

| 状态码 | 说明 |
|--------|------|
| `200` | 密码修改成功 |
| `400` | 请求参数错误或业务校验失败 |

---

## 4. 注销登录

### 4.1 接口说明

将当前用户的 Token 加入 Redis 黑名单，使其失效，实现注销登录。

### 4.2 请求信息

| 项目 | 内容 |
|------|------|
| **URL** | `/user/logout` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |
| **Authorization** | `Bearer <token>` |

### 4.3 请求头

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `Authorization` | `String` | 是 | Bearer Token，格式：`Bearer <token>` |

### 4.4 响应参数

#### 成功响应（HTTP 200）

| 参数名 | 类型 | 说明 |
|--------|------|------|
| `code` | `int` | 状态码，成功为 `200` |
| `message` | `String` | 提示信息，成功为 `"注销成功"` |
| `data` | `null` | 响应数据为 null |

**成功响应示例：**

```json
{
    "code": 200,
    "message": "注销成功",
    "data": null
}
```

#### 错误响应

**Token 无效（HTTP 400）：**

```json
{
    "code": 400,
    "message": "Token 无效或已过期",
    "data": null
}
```

**Token 为空（HTTP 400）：**

```json
{
    "code": 400,
    "message": "Token 不能为空",
    "data": null
}
```

### 4.5 状态码说明

| 状态码 | 说明 |
|--------|------|
| `200` | 注销成功 |
| `400` | 请求参数错误或业务校验失败 |

---

## 5. Token 使用说明

> **注意：** 修改密码和注销登录接口需要携带登录后获取的 Token 进行身份验证。请先在登录接口获取 Token，然后在请求头中添加 `Authorization: Bearer <token>`。

登录成功后返回的 `token` 需要在后续需要认证的请求中通过请求头传递：

```
Authorization: Bearer <token>
```

### Token 解析内容

Token 中包含以下声明（Claims）：

| 声明 | 说明 |
|------|------|
| `sub` | 用户 ID（字符串形式） |
| `userId` | 用户 ID（整数形式） |
| `username` | 用户名 |
| `iat` | 签发时间 |
| `exp` | 过期时间（24 小时后） |

---

## 6. 数据库表结构

### `user` 表

```sql
CREATE TABLE `user` (
    `id`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    varchar(50)  NOT NULL COMMENT '用户名',
    `password`    varchar(255) NOT NULL COMMENT '密码（BCrypt加密存储）',
    `phone`       varchar(20)  DEFAULT NULL COMMENT '手机号',
    `role`        tinyint(1)   NOT NULL DEFAULT 1 COMMENT '角色：1-学生，2-老师，3-管理员',
    `status`      tinyint(1)   NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

> **注意：** 密码使用 BCrypt 加密算法存储，不可逆。

---

## 7. Postman 测试集合

> 以下内容为 Postman Collection v2.1 格式，可直接复制并导入 Postman 进行接口测试。

### 7.1 导入方法

1. 打开 Postman
2. 点击 **Import** → **Raw text**
3. 将下方 **Postman Collection** 代码块中的内容粘贴进去 → **Continue** → **Import**
4. 再将下方 **Postman Environment** 代码块中的内容粘贴导入

### 7.2 Postman Collection

将以下 JSON 复制并导入 Postman：

```json
{
	"info": {
		"_postman_id": "alumnus-api-collection",
		"name": "校友管理系统 API",
		"description": "校友管理系统接口文档 - 用于 Postman 测试\n\n> 基础路径：`http://localhost:8080`\n> 数据库：`alumnus_db`（MySQL）",
		"schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
	},
	"item": [
		{
			"name": "用户登录",
			"description": "支持通过 **用户名** 或 **手机号** 配合 **密码** 进行登录认证，登录成功后返回 JWT Token。\n\n### 请求参数\n\n| 参数名 | 类型 | 必填 | 说明 |\n|--------|------|------|------|\n| `account` | `String` | 是 | 用户名或手机号 |\n| `password` | `String` | 是 | 密码（明文） |\n\n### 响应说明\n\n| 状态码 | 说明 |\n|--------|------|\n| `200` | 登录成功 |\n| `400` | 请求参数错误或业务校验失败 |\n| `401` | 未授权（Token 无效或过期） |\n\n### 成功响应 data 字段\n\n| 字段名 | 类型 | 说明 |\n|--------|------|------|\n| `token` | `String` | JWT 令牌，有效期 24 小时 |\n| `userId` | `int` | 用户 ID |\n| `username` | `String` | 用户名 |\n| `phone` | `String` | 手机号 |\n| `role` | `int` | 角色：1-学生，2-老师，3-管理员 |",
			"item": [
				{
					"name": "登录 - 用户名登录",
					"event": [
						{
							"listen": "test",
							"script": {
								"exec": [
									"var jsonData = pm.response.json();",
									"",
									"pm.test(\"状态码为 200\", function () {",
									"    pm.response.to.have.status(200);",
									"});",
									"",
									"pm.test(\"返回 code 为 200\", function () {",
									"    pm.expect(jsonData.code).to.eql(200);",
									"});",
									"",
									"pm.test(\"返回 message 为 '操作成功'\", function () {",
									"    pm.expect(jsonData.message).to.eql(\"操作成功\");",
									"});",
									"",
									"pm.test(\"返回 data 不为 null\", function () {",
									"    pm.expect(jsonData.data).to.not.eql(null);",
									"});",
									"",
									"pm.test(\"返回数据包含 token、userId、username、phone、role\", function () {",
									"    pm.expect(jsonData.data).to.have.property(\"token\");",
									"    pm.expect(jsonData.data).to.have.property(\"userId\");",
									"    pm.expect(jsonData.data).to.have.property(\"username\");",
									"    pm.expect(jsonData.data).to.have.property(\"phone\");",
									"    pm.expect(jsonData.data).to.have.property(\"role\");",
									"});",
									"",
									"if (jsonData.code === 200 && jsonData.data && jsonData.data.token) {",
									"    pm.environment.set(\"token\", jsonData.data.token);",
									"    pm.environment.set(\"userId\", String(jsonData.data.userId));",
									"    pm.environment.set(\"username\", jsonData.data.username);",
									"    console.log(\"Token 已设置: \" + jsonData.data.token);",
									"}"
								],
								"type": "text/javascript"
							}
						}
					],
					"request": {
						"method": "POST",
						"header": [
							{
								"key": "Content-Type",
								"value": "application/json"
							}
						],
						"body": {
							"mode": "raw",
							"raw": "{\n    \"account\": \"zhangsan\",\n    \"password\": \"123456\"\n}"
						},
						"url": {
							"raw": "{{base_url}}/user/login",
							"host": ["{{base_url}}"],
							"path": ["user", "login"]
						},
						"description": "使用用户名登录"
					},
					"response": [
						{
							"name": "登录成功",
							"status": "OK",
							"code": 200,
							"header": [
								{
									"key": "Content-Type",
									"value": "application/json"
								}
							],
							"body": "{\n    \"code\": 200,\n    \"message\": \"操作成功\",\n    \"data\": {\n        \"token\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6InpoYW5nc2FuIiwiZXhwIjoxNzQ4MzI5MjUyLCJpYXQiOjE3NDgyNDI4NTJ9.xxx\",\n        \"userId\": 1,\n        \"username\": \"zhangsan\",\n        \"phone\": \"13800138000\",\n        \"role\": 1\n    }\n}"
						},
						{
							"name": "账号不存在",
							"status": "Bad Request",
							"code": 400,
							"body": "{\n    \"code\": 400,\n    \"message\": \"用户名或手机号不存在\",\n    \"data\": null\n}"
						},
						{
							"name": "密码错误",
							"status": "Bad Request",
							"code": 400,
							"body": "{\n    \"code\": 400,\n    \"message\": \"密码错误\",\n    \"data\": null\n}"
						},
						{
							"name": "账号被禁用",
							"status": "Bad Request",
							"code": 400,
							"body": "{\n    \"code\": 400,\n    \"message\": \"账号已被禁用\",\n    \"data\": null\n}"
						},
						{
							"name": "参数缺失",
							"status": "Bad Request",
							"code": 400,
							"body": "{\n    \"code\": 400,\n    \"message\": \"请输入用户名或手机号\",\n    \"data\": null\n}"
						}
					]
				},
				{
					"name": "登录 - 手机号登录",
					"event": [
						{
							"listen": "test",
							"script": {
								"exec": [
									"var jsonData = pm.response.json();",
									"",
									"pm.test(\"状态码为 200\", function () {",
									"    pm.response.to.have.status(200);",
									"});",
									"",
									"pm.test(\"返回 code 为 200\", function () {",
									"    pm.expect(jsonData.code).to.eql(200);",
									"});",
									"",
									"pm.test(\"返回 data 包含 token\", function () {",
									"    pm.expect(jsonData.data).to.have.property(\"token\");",
									"});",
									"",
									"if (jsonData.code === 200 && jsonData.data && jsonData.data.token) {",
									"    pm.environment.set(\"token\", jsonData.data.token);",
									"    pm.environment.set(\"userId\", String(jsonData.data.userId));",
									"    pm.environment.set(\"username\", jsonData.data.username);",
									"}"
								],
								"type": "text/javascript"
							}
						}
					],
					"request": {
						"method": "POST",
						"header": [
							{
								"key": "Content-Type",
								"value": "application/json"
							}
						],
						"body": {
							"mode": "raw",
							"raw": "{\n    \"account\": \"13800138000\",\n    \"password\": \"123456\"\n}"
						},
						"url": {
							"raw": "{{base_url}}/user/login",
							"host": ["{{base_url}}"],
							"path": ["user", "login"]
						},
						"description": "使用手机号登录"
					},
					"response": []
				}
			]
		},
		{
			"name": "修改密码",
			"description": "用户登录后，通过提供 **旧密码** 验证身份，然后设置 **新密码**。新密码使用 BCrypt 加密存储。\n\n### 请求参数\n\n| 参数名 | 类型 | 必填 | 说明 |\n|--------|------|------|------|\n| `userId` | `int` | 是 | 用户 ID |\n| `oldPassword` | `String` | 是 | 旧密码（明文） |\n| `newPassword` | `String` | 是 | 新密码（明文，至少6位） |\n\n### 响应说明\n\n| 状态码 | 说明 |\n|--------|------|\n| `200` | 密码修改成功 |\n| `400` | 请求参数错误或业务校验失败 |",
			"item": [
				{
					"name": "修改密码",
					"request": {
						"method": "POST",
						"header": [
							{
								"key": "Content-Type",
								"value": "application/json"
							}
						],
						"body": {
							"mode": "raw",
							"raw": "{\n    \"userId\": 1,\n    \"oldPassword\": \"123456\",\n    \"newPassword\": \"654321\"\n}"
						},
						"url": {
							"raw": "{{base_url}}/user/changePassword",
							"host": ["{{base_url}}"],
							"path": ["user", "changePassword"]
						},
						"description": "修改当前登录用户的密码"
					},
					"response": [
						{
							"name": "修改密码成功",
							"status": "OK",
							"code": 200,
							"header": [
								{
									"key": "Content-Type",
									"value": "application/json"
								}
							],
							"body": "{\n    \"code\": 200,\n    \"message\": \"密码修改成功\",\n    \"data\": null\n}"
						},
						{
							"name": "旧密码错误",
							"status": "Bad Request",
							"code": 400,
							"body": "{\n    \"code\": 400,\n    \"message\": \"旧密码错误\",\n    \"data\": null\n}"
						},
						{
							"name": "用户不存在",
							"status": "Bad Request",
							"code": 400,
							"body": "{\n    \"code\": 400,\n    \"message\": \"用户不存在\",\n    \"data\": null\n}"
						}
					]
				}
			]
		},
		{
			"name": "Token 认证示例（自动携带Token）",
			"description": "登录成功后返回的 `token` 需要在后续需要认证的请求中通过请求头传递：\n\n```\nAuthorization: Bearer <token>\n```\n\n> **注意：** 本集合已配置自动从登录响应中提取 Token 并设置为环境变量 `{{token}}`，后续请求可通过 `{{authorization}}` 变量自动携带 Token。",
			"item": [
				{
					"name": "获取当前用户信息（示例）",
					"event": [
						{
							"listen": "prerequest",
							"script": {
								"exec": [
									"var token = pm.environment.get(\"token\");",
									"if (token) {",
									"    pm.request.headers.add({",
									"        key: \"Authorization\",",
									"        value: \"Bearer \" + token",
									"    });",
									"}"
								],
								"type": "text/javascript"
							}
						},
						{
							"listen": "test",
							"script": {
								"exec": [
									"pm.test(\"状态码为 200\", function () {",
									"    pm.response.to.have.status(200);",
									"});",
									"",
									"var jsonData = pm.response.json();",
									"pm.test(\"返回 code 为 200\", function () {",
									"    pm.expect(jsonData.code).to.eql(200);",
									"});"
								],
								"type": "text/javascript"
							}
						}
					],
					"request": {
						"method": "GET",
						"header": [
							{
								"key": "Authorization",
								"value": "Bearer {{token}}",
								"type": "text"
							}
						],
						"url": {
							"raw": "{{base_url}}/user/info",
							"host": ["{{base_url}}"],
							"path": ["user", "info"]
						},
						"description": "示例：携带 Token 访问需要认证的接口（当前接口需后端实现）"
					},
					"response": []
				}
			],
			"auth": {
				"type": "bearer",
				"bearer": [
					{
						"key": "token",
						"value": "{{token}}",
						"type": "string"
					}
				]
			}
		},
		{
			"name": "注销登录",
			"description": "将当前用户的 Token 加入 Redis 黑名单，使其失效，实现注销登录。\n\n### 请求头\n\n| 参数名 | 类型 | 必填 | 说明 |\n|--------|------|------|------|\n| `Authorization` | `String` | 是 | Bearer Token，格式：`Bearer <token>` |\n\n### 响应说明\n\n| 状态码 | 说明 |\n|--------|------|\n| `200` | 注销成功 |\n| `400` | 请求参数错误或业务校验失败 |",
			"item": [
				{
					"name": "注销登录",
					"event": [
						{
							"listen": "prerequest",
							"script": {
								"exec": [
									"var token = pm.environment.get(\"token\");",
									"if (token) {",
									"    pm.request.headers.add({",
									"        key: \"Authorization\",",
									"        value: \"Bearer \" + token",
									"    });",
									"}"
								],
								"type": "text/javascript"
							}
						},
						{
							"listen": "test",
							"script": {
								"exec": [
									"pm.test(\"状态码为 200\", function () {",
									"    pm.response.to.have.status(200);",
									"});",
									"",
									"var jsonData = pm.response.json();",
									"pm.test(\"返回 code 为 200\", function () {",
									"    pm.expect(jsonData.code).to.eql(200);",
									"});",
									"",
									"pm.test(\"返回 message 为 '注销成功'\", function () {",
									"    pm.expect(jsonData.message).to.eql(\"注销成功\");",
									"});"
								],
								"type": "text/javascript"
							}
						}
					],
					"request": {
						"method": "POST",
						"header": [
							{
								"key": "Content-Type",
								"value": "application/json"
							}
						],
						"url": {
							"raw": "{{base_url}}/user/logout",
							"host": ["{{base_url}}"],
							"path": ["user", "logout"]
						},
						"description": "注销当前登录，将 Token 加入黑名单"
					},
					"response": [
						{
							"name": "注销成功",
							"status": "OK",
							"code": 200,
							"header": [
								{
									"key": "Content-Type",
									"value": "application/json"
								}
							],
							"body": "{\n    \"code\": 200,\n    \"message\": \"注销成功\",\n    \"data\": null\n}"
						},
						{
							"name": "Token 无效",
							"status": "Bad Request",
							"code": 400,
							"body": "{\n    \"code\": 400,\n    \"message\": \"Token 无效或已过期\",\n    \"data\": null\n}"
						}
					]
				}
			]
		}
	],
	"variable": [
		{
			"key": "base_url",
			"value": "http://localhost:8080",
			"type": "string"
		},
		{
			"key": "token",
			"value": "",
			"type": "string"
		},
		{
			"key": "userId",
			"value": "",
			"type": "string"
		},
		{
			"key": "username",
			"value": "",
			"type": "string"
		}
	]
}
```

### 7.3 Postman Environment

将以下 JSON 复制并导入 Postman 作为环境变量：

```json
{
	"name": "校友管理系统 - 本地开发环境",
	"values": [
		{
			"key": "base_url",
			"value": "http://localhost:8080",
			"type": "default",
			"enabled": true
		},
		{
			"key": "token",
			"value": "",
			"type": "default",
			"enabled": true
		},
		{
			"key": "userId",
			"value": "",
			"type": "default",
			"enabled": true
		},
		{
			"key": "username",
			"value": "",
			"type": "default",
			"enabled": true
		}
	],
	"_postman_variable_scope": "environment",
	"_postman_exported_at": "2026-06-22T02:00:00.000Z",
	"_postman_exported_using": "Alumnus API"
}
```
