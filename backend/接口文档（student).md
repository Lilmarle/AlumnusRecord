# 校友管理系统 — 学生接口文档

> 基础路径：`http://localhost:8080`

---

## 1. 查询学生信息

### 1.1 接口说明

根据用户 ID 查询该学生的完整信息（包含学院、专业、班级、档案信息）。

**权限控制：**
- 学生（role=1）：只能查询自己的信息
- 老师（role=2）/ 管理员（role=3）：可查询任意学生的信息

### 1.2 请求信息

| 项目 | 内容 |
|------|------|
| **URL** | `/student/info` |
| **方法** | `GET` |
| **Content-Type** | `application/json` |
| **Authorization** | `Bearer <token>` |

### 1.3 请求参数

#### URL 参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `userId` | `int` | 否 | 用户 ID（学生角色可不传，默认查自己；教师/管理员传此参数查指定学生） |

#### 请求头

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `Authorization` | `String` | 是 | Bearer Token，格式：`Bearer <token>` |

**请求示例：**

```
GET /student/info?userId=1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 1.4 响应参数

#### 成功响应（HTTP 200）

| 参数名 | 类型 | 说明 |
|--------|------|------|
| `code` | `int` | 状态码，成功为 `200` |
| `message` | `String` | 提示信息，成功为 `"操作成功"` |
| `data` | `object` | 学生信息数据 |

**`data` 字段说明：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `studentId` | `int` | 学生记录 ID |
| `userId` | `int` | 用户 ID |
| `studentNo` | `String` | 学号 |
| `collegeId` | `int` | 学院 ID |
| `collegeName` | `String` | 学院名称 |
| `majorId` | `int` | 专业 ID |
| `majorName` | `String` | 专业名称 |
| `classId` | `int` | 班级 ID |
| `className` | `String` | 班级名称 |
| `grade` | `String` | 年级 |
| `name` | `String` | 姓名 |
| `gender` | `int` | 性别：0-未知，1-男，2-女 |
| `avatar` | `String` | 头像 URL |
| `idCard` | `String` | 身份证号 |
| `enrollDate` | `String` | 入校时间 |
| `graduateDate` | `String` | 毕业时间（可为空） |
| `createTime` | `String` | 创建时间 |
| `updateTime` | `String` | 更新时间 |

**成功响应示例：**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "studentId": 1,
        "userId": 1,
        "studentNo": "2024001",
        "collegeId": 1,
        "collegeName": "计算机科学与技术学院",
        "majorId": 1,
        "majorName": "软件工程",
        "classId": 1,
        "className": "软件工程2024-1班",
        "grade": "2024",
        "name": "张三",
        "gender": 1,
        "avatar": null,
        "idCard": "110101200001011234",
        "enrollDate": "2024-09-01",
        "graduateDate": null,
        "createTime": "2024-09-01 08:00:00",
        "updateTime": "2024-09-01 08:00:00"
    }
}
```

#### 错误响应

**未登录或 Token 无效（HTTP 401）：**

```json
{
    "code": 401,
    "message": "未登录或 Token 无效",
    "data": null
}
```

**学生无权查看他人信息（HTTP 400）：**

```json
{
    "code": 400,
    "message": "无权查看其他学生的信息",
    "data": null
}
```

**未找到学生信息（HTTP 400）：**

```json
{
    "code": 400,
    "message": "未找到该学生的信息",
    "data": null
}
```

### 1.5 状态码说明

| 状态码 | 说明 |
|--------|------|
| `200` | 查询成功 |
| `400` | 请求参数错误或业务校验失败 |
| `401` | 未授权（Token 无效或过期） |
---
## 2. 查询所有学生列表

### 2.1 接口说明

查询所有学生的完整信息列表，支持搜索筛选。

**权限控制：**
- 仅教师（role=2）和管理员（role=3）可用
- 学生角色调用会返回权限错误

### 2.2 请求信息

| 项目 | 内容 |
|------|------|
| **URL** | `/student/list` |
| **方法** | `GET` |
| **Content-Type** | `application/json` |
| **Authorization** | `Bearer <token>` |

### 2.3 请求参数

#### 请求头

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `Authorization` | `String` | 是 | Bearer Token，格式：`Bearer <token>` |

**请求示例：**

```
GET /student/list
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 2.4 响应参数

#### 成功响应（HTTP 200）

| 参数名 | 类型 | 说明 |
|--------|------|------|
| `code` | `int` | 状态码，成功为 `200` |
| `message` | `String` | 提示信息，成功为 `"操作成功"` |
| `data` | `array` | 学生信息列表 |

**`data` 数组元素字段说明（同查询单个学生接口）：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `studentId` | `int` | 学生记录 ID |
| `userId` | `int` | 用户 ID |
| `studentNo` | `String` | 学号 |
| `collegeName` | `String` | 学院名称 |
| `majorName` | `String` | 专业名称 |
| `className` | `String` | 班级名称 |
| `grade` | `String` | 年级 |
| `name` | `String` | 姓名 |
| `gender` | `int` | 性别：0-未知，1-男，2-女 |
| `enrollDate` | `String` | 入校时间 |
| `graduateDate` | `String` | 毕业时间（可为空） |

**成功响应示例：**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": [
        {
            "studentId": 1,
            "userId": 1,
            "studentNo": "2024001",
            "collegeName": "计算机科学与技术学院",
            "majorName": "软件工程",
            "className": "软件工程2024-1班",
            "grade": "2024",
            "name": "张三",
            "gender": 1,
            "enrollDate": "2024-09-01",
            "graduateDate": null
        },
        {
            "studentId": 2,
            "userId": 2,
            "studentNo": "2024002",
            "collegeName": "数学与统计学院",
            "majorName": "应用数学",
            "className": "应用数学2024-1班",
            "grade": "2024",
            "name": "李四",
            "gender": 2,
            "enrollDate": "2024-09-01",
            "graduateDate": null
        }
    ]
}
```

#### 错误响应

**权限不足（HTTP 400）：**

```json
{
    "code": 400,
    "message": "权限不足，仅教师和管理员可查看所有学生信息",
    "data": null
}
```

**未登录或 Token 无效（HTTP 401）：**

```json
{
    "code": 401,
    "message": "未登录或 Token 无效",
    "data": null
}
```

### 2.5 状态码说明

| 状态码 | 说明 |
|--------|------|
| `200` | 查询成功 |
| `400` | 请求参数错误或业务校验失败（权限不足） |
| `401` | 未授权（Token 无效或过期） |

---

## 3. 修改学生信息

### 3.1 接口说明

修改学生的基本信息，支持同时更新 `student` 表字段（学号、学院、专业、班级、入校/毕业时间）和 `user_profile` 表字段（姓名、性别、头像、身份证号）。

**权限控制：**
- 学生（role=1）：只能修改自己的信息
- 老师（role=2）/ 管理员（role=3）：可修改任意学生的信息

### 3.2 请求信息

| 项目 | 内容 |
|------|------|
| **URL** | `/student/update` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |
| **Authorization** | `Bearer <token>` |

### 3.3 请求参数

#### 请求头

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `Authorization` | `String` | 是 | Bearer Token，格式：`Bearer <token>` |

#### 请求体

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | `int` | 是 | 学生记录 ID（studentId） |
| `userId` | `int` | 否 | 用户 ID（关联 user 表） |
| `studentNo` | `String` | 否 | 学号 |
| `collegeId` | `int` | 否 | 学院 ID |
| `majorId` | `int` | 否 | 专业 ID |
| `classId` | `int` | 否 | 班级 ID |
| `enrollDate` | `String` | 否 | 入校时间（格式：yyyy-MM-dd） |
| `graduateDate` | `String` | 否 | 毕业时间（格式：yyyy-MM-dd，可为空） |
| `name` | `String` | 否 | 姓名（更新 user_profile 表） |
| `gender` | `int` | 否 | 性别（更新 user_profile 表）：1-男，2-女，0-未知 |
| `avatar` | `String` | 否 | 头像 URL（更新 user_profile 表） |
| `idCard` | `String` | 否 | 身份证号（更新 user_profile 表） |

**请求示例：**

```json
{
    "id": 1,
    "studentNo": "2024001",
    "collegeId": 1,
    "majorId": 1,
    "classId": 1,
    "enrollDate": "2024-09-01",
    "graduateDate": "2028-06-30",
    "name": "张三",
    "gender": 1,
    "idCard": "110101200001011234"
}
```

### 3.4 响应参数

#### 成功响应（HTTP 200）

| 参数名 | 类型 | 说明 |
|--------|------|------|
| `code` | `int` | 状态码，成功为 `200` |
| `message` | `String` | 提示信息，成功为 `"学生信息修改成功"` |
| `data` | `null` | 响应数据为 null |

**成功响应示例：**

```json
{
    "code": 200,
    "message": "学生信息修改成功",
    "data": null
}
```

#### 错误响应

**未登录或 Token 无效（HTTP 401）：**

```json
{
    "code": 401,
    "message": "未登录或 Token 无效",
    "data": null
}
```

**学生记录 ID 为空（HTTP 400）：**

```json
{
    "code": 400,
    "message": "学生记录ID不能为空",
    "data": null
}
```

**学生记录不存在（HTTP 400）：**

```json
{
    "code": 400,
    "message": "学生记录不存在",
    "data": null
}
```

**学生无权修改他人信息（HTTP 400）：**

```json
{
    "code": 400,
    "message": "无权修改其他学生的信息",
    "data": null
}
```

**数据库更新异常（HTTP 400）：**

```json
{
    "code": 400,
    "message": "修改学生信息失败，请稍后重试",
    "data": null
}
```

### 3.5 状态码说明

| 状态码 | 说明 |
|--------|------|
| `200` | 修改成功 |
| `400` | 请求参数错误或业务校验失败 |
| `401` | 未授权（Token 无效或过期） |

---

## 附录：数据字典

### Role 角色枚举

| 值 | 说明 |
|----|------|
| `1` | 学生 |
| `2` | 老师 |
| `3` | 管理员 |

### Gender 性别枚举

| 值 | 说明 |
|----|------|
| `0` | 未知 |
| `1` | 男 |
| `2` | 女 |
