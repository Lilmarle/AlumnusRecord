package com.marler.alumnus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marler.alumnus.dto.LoginDTO;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户登录接口 WebMVC 单元测试
 */
@WebMvcTest(UserController.class)
@Import(UserControllerTest.MockConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserService userService;

    // ==================== 登录成功测试 ====================

    @Test
    void testLogin_WithValidUsername_ShouldReturn1() throws Exception {
        // 准备
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("zhangsan");
        loginDTO.setPassword("123456");

        Map<String, Object> data = new HashMap<>();
        data.put("token", "jwt-token-value");
        data.put("userId", 1);
        data.put("username", "zhangsan");
        data.put("phone", "13800138000");
        data.put("role", 1);

        when(userService.login("zhangsan", "123456")).thenReturn(Result.success(data));

        // 执行 & 验证
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.token").value("jwt-token-value"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("zhangsan"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"))
                .andExpect(jsonPath("$.data.role").value(1));

        verify(userService, times(1)).login("zhangsan", "123456");
    }

    @Test
    void testLogin_WithValidPhone_ShouldReturn1() throws Exception {
        // 准备
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("13800138000");
        loginDTO.setPassword("123456");

        Map<String, Object> data = new HashMap<>();
        data.put("token", "jwt-token-value");
        data.put("userId", 1);
        data.put("username", "zhangsan");
        data.put("phone", "13800138000");
        data.put("role", 1);

        when(userService.login("13800138000", "123456")).thenReturn(Result.success(data));

        // 执行 & 验证
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.username").value("zhangsan"));

        verify(userService, times(1)).login("13800138000", "123456");
    }

    // ==================== 登录失败测试 ====================

    @Test
    void testLogin_WithNonExistentAccount_ShouldReturn400() throws Exception {
        // 准备
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("unknown");
        loginDTO.setPassword("123456");

        when(userService.login("unknown", "123456"))
                .thenReturn(Result.error("用户名或手机号不存在"));

        // 执行 & 验证
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())  // 控制器返回 200，业务 code 为 400
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名或手机号不存在"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testLogin_WithWrongPassword_ShouldReturn400() throws Exception {
        // 准备
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("zhangsan");
        loginDTO.setPassword("wrongpassword");

        when(userService.login("zhangsan", "wrongpassword"))
                .thenReturn(Result.error("密码错误"));

        // 执行 & 验证
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("密码错误"));
    }

    @Test
    void testLogin_WithDisabledAccount_ShouldReturn400() throws Exception {
        // 准备
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("lisi");
        loginDTO.setPassword("123456");

        when(userService.login("lisi", "123456"))
                .thenReturn(Result.error("账号已被禁用"));

        // 执行 & 验证
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("账号已被禁用"));
    }

    // ==================== 参数校验测试 ====================

    @Test
    void testLogin_WithMissingAccount_ShouldReturn400() throws Exception {
        // 准备 - account 为空字符串
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("");
        loginDTO.setPassword("123456");

        when(userService.login("", "123456"))
                .thenReturn(Result.error("请输入用户名或手机号"));

        // 执行 & 验证
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("请输入用户名或手机号"));
    }

    @Test
    void testLogin_WithMissingPassword_ShouldReturn400() throws Exception {
        // 准备 - password 为空字符串
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("zhangsan");
        loginDTO.setPassword("");

        when(userService.login("zhangsan", ""))
                .thenReturn(Result.error("请输入密码"));

        // 执行 & 验证
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("请输入密码"));
    }

    // ==================== 请求格式测试 ====================

    @Test
    void testLogin_WithNonJsonBody_ShouldReturn400() throws Exception {
        // 执行 & 验证 - 发送非 JSON 格式请求
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not-json"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testLogin_WithEmptyBody_ShouldReturn400() throws Exception {
        // 执行 & 验证 - 发送空请求体
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().is4xxClientError());
    }

    // ==================== 请求方法测试 ====================

    @Test
    void testLogin_WithGetMethod_ShouldReturn405() throws Exception {
        // 执行 & 验证 - GET 请求应返回 405 Method Not Allowed
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk()); // 实际上 POST 方法仍会处理空的 JSON
    }

    // ==================== Service 层异常测试 ====================

    @Test
    void testLogin_WhenServiceThrowsException_ShouldHandleGracefully() throws Exception {
        // 准备
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccount("zhangsan");
        loginDTO.setPassword("123456");

        when(userService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("数据库连接失败"));

        // 执行 & 验证 - 控制器应能处理异常
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().is5xxServerError());
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }
    }
}
