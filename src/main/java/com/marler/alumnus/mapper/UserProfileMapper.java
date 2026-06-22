package com.marler.alumnus.mapper;

import com.marler.alumnus.pojo.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {

    /**
     * 根据用户ID查询档案
     */
    UserProfile findByUserId(@Param("userId") Integer userId);

    /**
     * 更新用户档案
     */
    int updateUserProfile(UserProfile userProfile);
}
