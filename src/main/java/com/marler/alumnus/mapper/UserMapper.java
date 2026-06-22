package com.marler.alumnus.mapper;

import com.marler.alumnus.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名或手机号查询用户
     */
    User findByUsernameOrPhone(@Param("account") String account);

    /**
     * 根据用户名查询
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据手机号查询
     */
    User findByPhone(@Param("phone") String phone);

    /**
     * 新增用户
     */
    int insert(User user);

    /**
     * 根据ID查询用户
     */
    User findById(@Param("id") Integer id);

    /**
     * 更新用户密码
     */
    int updatePassword(@Param("id") Integer id, @Param("password") String password);
}
