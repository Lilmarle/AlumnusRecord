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
}
