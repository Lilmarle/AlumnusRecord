# 校友管理系统 — 用户档案接口文档

> 基础路径：`http://localhost:8080`

---

## 1. 修改用户详情

### 1.1 接口说明

修改用户的基本档案信息（姓名、性别、头像、身份证号）。

**权限控制：**
- 所有已登录用户均可使用，但只能修改自己的档案信息

### 1.2 请求信息

| 项目 | 内容 |
|------|------|
| **URL** | `/user/profile/update` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |

### 1.3 请求参数

#### 请求体

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `userId` | `int` | 是 | 用户 ID |
| `name` | `String` | 否 | 真实姓名 |
| `gender` | `int` | 否 | 性别：1-男，2-女，0-未知 |
| `avatar` | `String` | 否 | 头像 URL |
| `idCard` | `String` | 否 | 身份证号 |

**请求示例：**

```json
{
    "userId": 1,
    "name": "张三",
    "gender": 1,
    "avatar": "http://example.com/avatar.jpg",
    "idCard": "110101200001011234"
}
```

### 1.4 响应参数

#### 成功响应（HTTP 200）

| 参数名 | 类型 | 说明 |
|--------|------|------|
| `code` | `int` | 状态码，成功为 `200` |
| `message` | `String` | 提示信息，成功为 `"用户详情修改成功"` |
| `data` | `null` | 响应数据为 null |

**成功响应示例：**

```json
{
    "code": 200,
    "message": "用户详情修改成功",
    "data": null
}
```

#### 错误响应

**用户 ID 缺失（HTTP 400）：**

```json
{
    "code": 400,
    "message": "用户ID不能为空",
    "data": null
}
```

### 1.5 状态码说明

| 状态码 | 说明 |
|--------|------|
| `200` | 修改成功 |
| `400` | 请求参数错误或业务校验失败 |

---

## 2. Postman 测试

### 2.1 导入完整 Postman 集合

完整的 Postman 集合文件位于项目根目录：**`接口文档（完整API）-Postman集合.json`**

导入方法：
1. 打开 Postman
2. 点击 **Import** → **File** / **Raw text**
3. 选择或粘贴 `接口文档（完整API）-Postman集合.json` 的内容
4. 点击 **Continue** → **Import**

### 2.2 使用 Postman 变量

导入后，在 Postman 的 **Environments** 中设置以下变量：

| 变量名 | 说明 |
|--------|------|
| `base_url` | 基础路径：`http://localhost:8080` |
| `token` | 登录后自动填充的 JWT Token |
| `userId` | 当前用户 ID |
| `role` | 当前用户角色：1-学生，2-老师，3-管理员 |

> 登录接口的 Test 脚本会自动将 `token`、`userId`、`username`、`role` 保存到环境变量中，后续接口可直接使用 `{{token}}`、`{{userId}}` 等变量。
