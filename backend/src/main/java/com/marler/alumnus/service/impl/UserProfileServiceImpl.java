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

import java.util.HashMap;
import java.util.Map;

@Service
public class UserProfileServiceImpl implements UserProfileService
{

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

            // 重新查询更新后的完整档案数据
            UserProfile updatedProfile = userProfileMapper.findByUserId(userId);

            // 构造详细返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("profileId", updatedProfile.getId());
            data.put("name", updatedProfile.getName());
            data.put("gender", updatedProfile.getGender());
            data.put("avatar", updatedProfile.getAvatar());
            data.put("idCard", updatedProfile.getIdCard());
            data.put("createTime", updatedProfile.getCreateTime());
            data.put("updateTime", updatedProfile.getUpdateTime());

            // 记录哪些字段被更新了
            Map<String, Object> changes = new HashMap<>();
            boolean hasChanges = false;
            if (updateUserProfileDTO.getName() != null) {
                changes.put("name", updateUserProfileDTO.getName());
                hasChanges = true;
            }
            if (updateUserProfileDTO.getGender() != null) {
                changes.put("gender", updateUserProfileDTO.getGender());
                hasChanges = true;
            }
            if (updateUserProfileDTO.getAvatar() != null) {
                changes.put("avatar", updateUserProfileDTO.getAvatar());
                hasChanges = true;
            }
            if (updateUserProfileDTO.getIdCard() != null) {
                changes.put("idCard", updateUserProfileDTO.getIdCard());
                hasChanges = true;
            }
            data.put("updatedFields", hasChanges ? changes : "无字段更新");

            log.info("修改用户详情成功 - userId: {}, 更新字段: {}", userId, changes.keySet());
            return Result.success("用户详情修改成功", data);
        } else {
            log.error("修改用户详情失败 - 数据库更新异常: userId={}", userId);
            return Result.error("修改用户详情失败，请稍后重试");
        }
    }
}
