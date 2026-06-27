package com.marler.alumnus.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT 工具类单元测试
 */
class JwtUtilsTest {

    @Test
    void testGenerateToken_ShouldReturnNonNullToken() {
        // 执行
        String token = JwtUtils.generateToken(1, "zhangsan");

        // 验证
        assertNotNull(token, "生成的 Token 不应为 null");
        assertFalse(token.isEmpty(), "生成的 Token 不应为空字符串");
    }

    @Test
    void testParseToken_ShouldContainCorrectClaims() {
        // 准备
        Integer userId = 1;
        String username = "zhangsan";

        // 执行
        String token = JwtUtils.generateToken(userId, username);
        Claims claims = JwtUtils.parseToken(token);

        // 验证
        assertNotNull(claims, "解析后的 Claims 不应为 null");
        assertEquals(String.valueOf(userId), claims.getSubject(), "sub 应为用户 ID 的字符串形式");
        assertEquals(userId, claims.get("userId", Integer.class), "userId 声明应正确");
        assertEquals(username, claims.get("username", String.class), "username 声明应正确");
        assertNotNull(claims.getIssuedAt(), "iat 签发时间不应为 null");
        assertNotNull(claims.getExpiration(), "exp 过期时间不应为 null");
    }

    @Test
    void testValidateToken_WithValidToken_ShouldReturnTrue() {
        // 准备
        String token = JwtUtils.generateToken(2, "lisi");

        // 执行 & 验证
        assertTrue(JwtUtils.validateToken(token), "有效 Token 应验证通过");
    }

    @Test
    void testValidateToken_WithInvalidToken_ShouldReturnFalse() {
        // 执行 & 验证
        assertFalse(JwtUtils.validateToken("invalid.token.here"), "无效 Token 应验证失败");
        assertFalse(JwtUtils.validateToken(""), "空字符串应验证失败");
        assertFalse(JwtUtils.validateToken(null), "null 应验证失败");
    }

    @Test
    void testParseToken_WithInvalidToken_ShouldThrowException() {
        // 执行 & 验证
        assertThrows(Exception.class, () -> JwtUtils.parseToken("invalid.token.here"),
                "解析无效 Token 应抛出异常");
    }

    @Test
    void testTokenExpiration_ShouldBe24Hours() {
        // 准备
        String token = JwtUtils.generateToken(1, "test");
        Claims claims = JwtUtils.parseToken(token);

        // 验证过期时间在合理范围内（接近24小时）
        long issuedAt = claims.getIssuedAt().getTime();
        long expiration = claims.getExpiration().getTime();
        long diffMillis = expiration - issuedAt;

        // 24小时 = 86400000 毫秒，允许 ±5秒误差
        assertEquals(86400000L, diffMillis, 5000,
                "Token 有效期应为 24 小时");
    }
}
