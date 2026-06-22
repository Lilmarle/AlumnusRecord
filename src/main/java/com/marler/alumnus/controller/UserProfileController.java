package com.marler.alumnus.controller;

import com.marler.alumnus.dto.UpdateUserProfileDTO;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/profile")
public class UserProfileController {

    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userProfileService;

    /**
     * 修改用户详情接口
     * POST /user/profile/update
     */
    @PostMapping("/update")
    public Result updateUserProfile(@RequestBody UpdateUserProfileDTO updateUserProfileDTO) {
        log.info("修改用户详情请求 - userId: {}", updateUserProfileDTO.getUserId());
        Result result = userProfileService.updateUserProfile(updateUserProfileDTO);
        log.info("修改用户详情结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }
}
