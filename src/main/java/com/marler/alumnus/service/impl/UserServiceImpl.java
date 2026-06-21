package com.marler.alumnus.service.impl;

import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.UserService;
import com.marler.alumnus.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result login(String account, String password) {
        // 1. 参数校验
        if (account == null || account.trim().isEmpty()) {
            return Result.error("请输入用户名或手机号");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("请输入密码");
        }

        // 2. 根据用户名或手机号查询用户
        User user = userMapper.findByUsernameOrPhone(account.trim());
        if (user == null) {
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
            return Result.error("密码错误");
        }

        // 4. 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
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

        return Result.success(data);
    }
}
