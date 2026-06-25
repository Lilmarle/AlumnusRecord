package com.marler.alumnus.controller;

import com.marler.alumnus.dto.UpdateUserProfileDTO;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * 修改用户详情接口
     * POST /user/profile/update
     */
    @PostMapping("/user/profile/update")
    public Result updateUserProfile(@RequestBody UpdateUserProfileDTO updateUserProfileDTO) {
        try {
            log.info("修改用户详情请求 - userId: {}", updateUserProfileDTO.getUserId());
            Result result = userProfileService.updateUserProfile(updateUserProfileDTO);
            log.info("修改用户详情结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("修改用户详情失败", e);
            return Result.error("修改用户详情失败：" + e.getMessage());
        }
    }
}
