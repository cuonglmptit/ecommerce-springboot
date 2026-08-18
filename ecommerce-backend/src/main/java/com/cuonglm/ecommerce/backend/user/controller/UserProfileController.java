package com.cuonglm.ecommerce.backend.user.controller;

import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.user.dto.external.UpdateUserProfileRequest;
import com.cuonglm.ecommerce.backend.user.dto.external.UserProfileResponse;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserProfileController – Controller cho user thông thường sửa thông tin cá nhân
 *
 * @author cuonglmptit
 * @since Tuesday, 18 August 2026
 */
@RestController
@RequestMapping("/api/v1/users/me")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile() {
        UserProfileResponse profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        UserProfileResponse updatedProfile = userService.updateCurrentUserProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin thành công", updatedProfile));
    }
}
