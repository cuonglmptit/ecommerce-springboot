package com.cuonglm.ecommerce.backend.user.controller;

import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserInfoDTO;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController - Controller cho admin thao tác với user
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
@RestController
@RequestMapping("/api/v1/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Lấy thông tin user theo ID (Chỉ Admin mới có quyền)
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserInfoDTO>> getUserById(@PathVariable Long userId) {
        return userService.findUserInfoById(userId)
                .map(info -> ResponseEntity.ok(ApiResponse.success(info)))
                .orElse(ResponseEntity.notFound().build());
    }
}

