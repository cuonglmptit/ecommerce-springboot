package com.cuonglm.ecommerce.backend.auth.controller;

import com.cuonglm.ecommerce.backend.auth.dto.external.otp.ResendOtpRequestDTO;
import com.cuonglm.ecommerce.backend.auth.dto.external.user.RegisterAndVerifyRequestDTO;
import com.cuonglm.ecommerce.backend.auth.dto.external.user.UserRegisterRequestDTO;
import com.cuonglm.ecommerce.backend.auth.service.AuthService;
import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController – Controller cho các thao tác liên quan đến xác thực người dùng.
 *
 * <p>
 * API endpoint cho các thao tác xác thực người dùng như đăng ký, đăng nhập, quên mật khẩu, otp,...
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 08 August 2025
 */
@RestController
@RequestMapping("/auth/public")
public class AuthController {
    private final AuthService authService; // Chỉ cần inject AuthService

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    
    @PostMapping("/register/send-otp")
    public ResponseEntity<ApiResponse<?>> sendOtpForRegister(@Valid @RequestBody UserRegisterRequestDTO request) {
        authService.requestRegistrationOtp(request);

        String message = "Mã OTP đã được gửi đến " + request.email() + ". Vui lòng kiểm tra email và nhập mã để hoàn tất đăng ký.";
        return ResponseEntity.ok(ApiResponse.success("200", message, null));
    }

    @PostMapping("/register/verify")
    public ResponseEntity<ApiResponse<?>> verifyAndRegister(@Valid @RequestBody RegisterAndVerifyRequestDTO request) {
        authService.verifyOtpAndRegisterLocalUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("201", "Đăng ký tài khoản thành công. Bạn có thể đăng nhập ngay.", null));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<?>> resendOtp(@Valid @RequestBody ResendOtpRequestDTO request) {
        authService.resendOtp(request);

        // Xử lý thông báo tùy thuộc vào mục đích
        String action = switch (request.purpose()) {
            case REGISTER -> "kích hoạt tài khoản";
            case RESET_PASSWORD -> "đặt lại mật khẩu";
            case LOGIN -> "đăng nhập";
        };

        String message = String.format("Mã OTP mới cho mục đích '%s' đã được gửi lại đến %s. Vui lòng kiểm tra.",
                action, request.target());
        return ResponseEntity.ok(ApiResponse.success("200", message, null));
    }
}
