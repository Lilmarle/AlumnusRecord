package com.marler.alumnus.service.impl;

import com.marler.alumnus.dto.ChangePasswordDTO;
import com.marler.alumnus.dto.RegisterDTO;
import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.UserService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result login(String account, String password) {
        log.debug("开始登录 - account: {}", account);
        // 1. 参数校验
        if (account == null || account.trim().isEmpty()) {
            log.warn("登录失败 - 账号为空");
            return Result.error("请输入用户名或手机号");
        }
        if (password == null || password.trim().isEmpty()) {
            log.warn("登录失败 - 密码为空");
            return Result.error("请输入密码");
        }

        // 2. 根据用户名或手机号查询用户
        User user = userMapper.findByUsernameOrPhone(account.trim());
        if (user == null) {
            log.warn("登录失败 - 用户不存在: {}", account);
            return Result.error("用户名或手机号不存在");
        }

        // 3. 校验密码（支持 BCrypt 加密和明文两种方式）
        String storedPassword = user.getPassword();
        boolean passwordMatch;

        // 判断是否为 BCrypt 加密密文（以 $2a$、$2b$ 或 $2y$ 开头）
        if (storedPassword != null && (storedPassword.startsWith("$2a$")
                || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$"))) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            passwordMatch = encoder.matches(password, storedPassword);
        } else {
            // 明文密码直接比较
            passwordMatch = password.equals(storedPassword);
        }

        if (!passwordMatch) {
            log.warn("登录失败 - 密码错误: {}", account);
            return Result.error("密码错误");
        }

        // 4. 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("登录失败 - 账号已被禁用: id={}", user.getId());
            return Result.error("账号已被禁用");
        }

        // 5. 生成 JWT Token
        String token = JwtUtils.generateToken(user.getId(), user.getUsername());

        // 6. 返回登录成功结果
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("phone", user.getPhone());
        data.put("role", user.getRole());

        log.info("登录成功 - userId: {}, username: {}", user.getId(), user.getUsername());
        return Result.success(data);
    }

    @Override
    public Result register(RegisterDTO registerDTO) {
        log.debug("开始注册 - username: {}, phone: {}", registerDTO.getUsername(), registerDTO.getPhone());
        // 1. 参数校验
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        String phone = registerDTO.getPhone();
        Integer role = registerDTO.getRole();

        if (username == null || username.trim().isEmpty()) {
            log.warn("注册失败 - 用户名为空");
            return Result.error("请输入用户名");
        }
        if (password == null || password.trim().isEmpty()) {
            log.warn("注册失败 - 密码为空");
            return Result.error("请输入密码");
        }
        if (phone == null || phone.trim().isEmpty()) {
            log.warn("注册失败 - 手机号为空");
            return Result.error("请输入手机号");
        }
        if (role == null || role < 1 || role > 3) {
            log.warn("注册失败 - 无效角色: {}", role);
            return Result.error("请选择有效的角色（1-学生，2-老师，3-管理员）");
        }

        // 2. 检查用户名是否已存在
        User existUser = userMapper.findByUsername(username.trim());
        if (existUser != null) {
            log.warn("注册失败 - 用户名已存在: {}", username);
            return Result.error("用户名已存在");
        }

        // 3. 检查手机号是否已存在
        User existPhone = userMapper.findByPhone(phone.trim());
        if (existPhone != null) {
            log.warn("注册失败 - 手机号已被注册: {}", phone);
            return Result.error("手机号已被注册");
        }

        // 4. BCrypt 加密密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);

        // 5. 创建用户
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(encodedPassword);
        user.setPhone(phone.trim());
        user.setRole(role);
        user.setStatus(1); // 默认状态：在校

        userMapper.insert(user);

        // 6. 返回注册成功
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("phone", user.getPhone());
        data.put("role", user.getRole());

        log.info("注册成功 - userId: {}, username: {}", user.getId(), user.getUsername());
        return Result.success(data);
    }

    @Override
    public Result changePassword(ChangePasswordDTO changePasswordDTO) {
        log.debug("开始修改密码 - userId: {}", changePasswordDTO.getUserId());

        // 1. 参数校验
        Integer userId = changePasswordDTO.getUserId();
        String oldPassword = changePasswordDTO.getOldPassword();
        String newPassword = changePasswordDTO.getNewPassword();

        if (userId == null) {
            log.warn("修改密码失败 - 用户ID为空");
            return Result.error("用户ID不能为空");
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            log.warn("修改密码失败 - 旧密码为空");
            return Result.error("请输入旧密码");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            log.warn("修改密码失败 - 新密码为空");
            return Result.error("请输入新密码");
        }
        if (newPassword.length() < 6) {
            log.warn("修改密码失败 - 新密码长度不足6位");
            return Result.error("新密码长度不能少于6位");
        }

        // 2. 查询用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            log.warn("修改密码失败 - 用户不存在: userId={}", userId);
            return Result.error("用户不存在");
        }

        // 3. 校验旧密码
        String storedPassword = user.getPassword();
        boolean passwordMatch;

        if (storedPassword != null && (storedPassword.startsWith("$2a$")
                || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$"))) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            passwordMatch = encoder.matches(oldPassword, storedPassword);
        } else {
            passwordMatch = oldPassword.equals(storedPassword);
        }

        if (!passwordMatch) {
            log.warn("修改密码失败 - 旧密码错误: userId={}", userId);
            return Result.error("旧密码错误");
        }

        // 4. 加密新密码并更新
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedNewPassword = encoder.encode(newPassword);

        int rows = userMapper.updatePassword(userId, encodedNewPassword);
        if (rows > 0) {
            log.info("修改密码成功 - userId: {}", userId);
            return Result.success("密码修改成功");
        } else {
            log.error("修改密码失败 - 数据库更新异常: userId={}", userId);
            return Result.error("修改密码失败，请稍后重试");
        }
    }

    @Override
    public Result logout(String token) {
        log.debug("开始注销 - token: {}...", token.substring(0, Math.min(20, token.length())));

        // 1. 校验 Token 是否为空
        if (token == null || token.trim().isEmpty()) {
            log.warn("注销失败 - Token 为空");
            return Result.error("Token 不能为空");
        }

        // 2. 解析 Token
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token);
        } catch (Exception e) {
            log.warn("注销失败 - Token 无效: {}", e.getMessage());
            return Result.error("Token 无效或已过期");
        }

        // 3. 获取 Token 过期时间，计算剩余有效时间（秒）
        Date expiration = claims.getExpiration();
        long remainingTtl = (expiration.getTime() - System.currentTimeMillis()) / 1000;

        if (remainingTtl <= 0) {
            log.warn("注销失败 - Token 已过期");
            return Result.error("Token 已过期");
        }

        // 4. 将 Token 加入 Redis 黑名单，TTL 与 Token 剩余有效期一致
        String blacklistKey = "token:blacklist:" + claims.getSubject();
        stringRedisTemplate.opsForValue().set(blacklistKey, token, remainingTtl, java.util.concurrent.TimeUnit.SECONDS);

        log.info("注销成功 - userId: {}, token 已加入黑名单，剩余有效期: {}秒", claims.getSubject(), remainingTtl);
        return Result.success("注销成功");
    }
}
