package com.marler.alumnus.pojo;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Result 响应封装类单元测试
 */
class ResultTest {

    // ==================== success() ====================

    @Test
    void testSuccess_WithData_ShouldReturn200AndMessage() {
        // 准备
        Map<String, Object> data = new HashMap<>();
        data.put("key", "value");

        // 执行
        Result result = Result.success(data);

        // 验证
        assertEquals(200, result.getCode(), "成功状态码应为 200");
        assertEquals("操作成功", result.getMessage(), "成功消息应为 '操作成功'");
        assertNotNull(result.getData(), "返回数据不应为 null");
        assertEquals("value", ((Map<String, Object>) result.getData()).get("key"));
    }

    @Test
    void testSuccess_WithNullData_ShouldReturn200() {
        // 执行
        Result result = Result.success(null);

        // 验证
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData(), "传入 null 时 data 应为 null");
    }

    @Test
    void testSuccess_NoArgs_ShouldReturn200AndNullData() {
        // 执行
        Result result = Result.success();

        // 验证
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData(), "无参 success() 的 data 应为 null");
    }

    // ==================== error(code, message) ====================

    @Test
    void testError_WithCodeAndMessage_ShouldReturnCorrectValues() {
        // 执行
        Result result = Result.error(400, "自定义错误消息");

        // 验证
        assertEquals(400, result.getCode(), "错误状态码应匹配传入值");
        assertEquals("自定义错误消息", result.getMessage(), "错误消息应匹配传入值");
        assertNull(result.getData(), "错误的 data 应为 null");
    }

    @Test
    void testError_WithCode400() {
        // 执行
        Result result = Result.error(400, "参数错误");

        // 验证
        assertEquals(400, result.getCode());
        assertEquals("参数错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testError_WithCode401() {
        // 执行
        Result result = Result.error(401, "未授权");

        // 验证
        assertEquals(401, result.getCode());
        assertEquals("未授权", result.getMessage());
    }

    @Test
    void testError_WithCode500() {
        // 执行
        Result result = Result.error(500, "服务器内部错误");

        // 验证
        assertEquals(500, result.getCode());
        assertEquals("服务器内部错误", result.getMessage());
    }

    // ==================== error(message) ====================

    @Test
    void testError_WithMessageOnly_ShouldDefaultTo400() {
        // 执行
        Result result = Result.error("业务校验失败");

        // 验证
        assertEquals(400, result.getCode(), "仅传消息时默认状态码应为 400");
        assertEquals("业务校验失败", result.getMessage());
        assertNull(result.getData());
    }

    // ==================== unauthorized() ====================

    @Test
    void testUnauthorized_ShouldReturn401() {
        // 执行
        Result result = Result.unauthorized("Token 已过期");

        // 验证
        assertEquals(401, result.getCode(), "未授权状态码应为 401");
        assertEquals("Token 已过期", result.getMessage());
        assertNull(result.getData());
    }

    // ==================== 业务场景测试 ====================

    @Test
    void testLoginSuccessScenario() {
        // 模拟登录成功返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", "jwt-token-value");
        data.put("userId", 1);
        data.put("username", "zhangsan");
        data.put("role", 1);

        Result result = Result.success(data);

        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());

        Map<String, Object> resultData = (Map<String, Object>) result.getData();
        assertEquals("jwt-token-value", resultData.get("token"));
        assertEquals(1, resultData.get("userId"));
        assertEquals("zhangsan", resultData.get("username"));
        assertEquals(1, resultData.get("role"));
    }

    @Test
    void testLoginErrorScenarios() {
        // 账号不存在
        Result notFound = Result.error("用户名或手机号不存在");
        assertEquals(400, notFound.getCode());
        assertEquals("用户名或手机号不存在", notFound.getMessage());

        // 密码错误
        Result wrongPwd = Result.error("密码错误");
        assertEquals(400, wrongPwd.getCode());
        assertEquals("密码错误", wrongPwd.getMessage());

        // 账号被禁用
        Result disabled = Result.error("账号已被禁用");
        assertEquals(400, disabled.getCode());
        assertEquals("账号已被禁用", disabled.getMessage());

        // 参数缺失
        Result missingParam = Result.error("请输入用户名或手机号");
        assertEquals(400, missingParam.getCode());
        assertEquals("请输入用户名或手机号", missingParam.getMessage());
    }
}
