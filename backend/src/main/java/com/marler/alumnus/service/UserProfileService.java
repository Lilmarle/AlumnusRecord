package com.marler.alumnus.service;

import com.marler.alumnus.dto.UpdateUserProfileDTO;
import com.marler.alumnus.pojo.Result;

public interface UserProfileService {

    /**
     * 修改用户详情
     */
    Result updateUserProfile(UpdateUserProfileDTO updateUserProfileDTO);
}
