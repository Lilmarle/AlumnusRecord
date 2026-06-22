package com.marler.alumnus.service.impl;

import com.marler.alumnus.dto.UpdateUserProfileDTO;
import com.marler.alumnus.mapper.UserProfileMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.UserProfile;
import com.marler.alumnus.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    public Result updateUserProfile(UpdateUserProfileDTO updateUserProfileDTO) {
        log.debug("开始修改用户详情 - userId: {}", updateUserProfileDTO.getUserId());

        // 1. 参数校验
        Integer userId = updateUserProfileDTO.getUserId();
        if (userId == null) {
            log.warn("修改用户详情失败 - 用户ID为空");
            return Result.error("用户ID不能为空");
        }

        // 2. 查询档案是否存在（注册时由触发器自动创建）
        UserProfile existingProfile = userProfileMapper.findByUserId(userId);
        if (existingProfile == null) {
            log.warn("修改用户详情失败 - 用户档案不存在: userId={}", userId);
            return Result.error("用户档案不存在");
        }

        // 3. 构造更新对象（只更新非空字段）
        UserProfile updateProfile = new UserProfile();
        updateProfile.setUserId(userId);
        updateProfile.setName(updateUserProfileDTO.getName());
        updateProfile.setGender(updateUserProfileDTO.getGender());
        updateProfile.setAvatar(updateUserProfileDTO.getAvatar());
        updateProfile.setIdCard(updateUserProfileDTO.getIdCard());

        // 4. 执行更新
        int rows = userProfileMapper.updateUserProfile(updateProfile);
        if (rows > 0) {
            log.info("修改用户详情成功 - userId: {}", userId);
            return Result.success("用户详情修改成功");
        } else {
            log.error("修改用户详情失败 - 数据库更新异常: userId={}", userId);
            return Result.error("修改用户详情失败，请稍后重试");
        }
    }
}
