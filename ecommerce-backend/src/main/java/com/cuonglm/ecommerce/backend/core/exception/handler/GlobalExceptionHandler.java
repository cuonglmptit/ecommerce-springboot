package com.cuonglm.ecommerce.backend.core.exception.handler;

import com.cuonglm.ecommerce.backend.core.exception.ValidationErrorsException;
import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - Bộ xử lý ngoại lệ toàn cục.
 *
 * <p>
 * Class này sử dụng {@link RestControllerAdvice} để bắt các exception được ném ra từ các controller
 * và chuyển đổi chúng thành một response {@link ApiResponse} chuẩn hóa.
 * Điều này giúp tập trung logic xử lý lỗi và làm cho code ở controller sạch sẽ hơn.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 06 November 2025
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý các lỗi validation (ví dụ: @Valid, @NotNull, @Size).
     * Trả về mã lỗi 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ApiResponse.error("VALIDATION_FAILED", "Dữ liệu không hợp lệ").addExtra("errors", errors);
    }

    @ExceptionHandler(ValidationErrorsException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(ValidationErrorsException ex) {
        // Tạo response lỗi
        ApiResponse<Object> errorResponse = ApiResponse.error(
                "VALIDATION_FAILED",
                ex.getMessage(), // Thông báo chung
                ex.getMessage()
        );

        // Thêm chi tiết lỗi vào trường 'extra'
        errorResponse.setExtra(ex.getErrors());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý các lỗi nghiệp vụ (ví dụ: user đã tồn tại, sản phẩm hết hàng).
     * Trả về mã lỗi 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Với lỗi nghiệp vụ, userMessage và devMessage có thể giống nhau
        // vì thông điệp thường đã an toàn để hiển thị.
        return ApiResponse.error("BUSINESS_LOGIC_ERROR", ex.getMessage(), ex.getMessage());
    }


    /**
     * Xử lý các trạng thái tài khoản không hợp lệ như bị khóa, vô hiệu hóa, bị cấm...
     * Bắt các exception chung của Spring Security: DisabledException, LockedException.
     * Trả về mã lỗi 403 Forbidden.
     */
    @ExceptionHandler({DisabledException.class, LockedException.class})
    public ResponseEntity<ApiResponse<?>> handleAccountStatusException(AuthenticationException ex) {
        String errorCode;
        HttpStatus httpStatus;

        if (ex instanceof LockedException) {
            // Case 2: SUSPENDED/LOCKED (Bị khóa bởi Admin hoặc do nhập sai nhiều lần)
            errorCode = "ACCOUNT_LOCKED";
            httpStatus = HttpStatus.UNAUTHORIZED; // 401 Unauthorized (Lỗi xác thực)

        } else if (ex instanceof DisabledException) {
            // Case 3: DEACTIVATED, BANNED, DELETED
            errorCode = "ACCOUNT_DISABLED";
            httpStatus = HttpStatus.UNAUTHORIZED;

        } else {
            // Lỗi trạng thái tài khoản không xác định khác
            errorCode = "ACCOUNT_STATUS_ERROR";
            httpStatus = HttpStatus.UNAUTHORIZED;
        }

        ApiResponse<?> apiResponse = ApiResponse.error(
                errorCode,
                ex.getMessage() // Thông điệp thân thiện đã được đặt trong AuthUserHandler
        );
        return new ResponseEntity<>(apiResponse, httpStatus);
    }


    /**
     * Bắt tất cả các lỗi hệ thống không mong muốn.
     * Trả về mã lỗi 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleGlobalException(Exception ex) {
        // Log lỗi chi tiết để đội ngũ phát triển xem
        ex.printStackTrace();

        // Tạo response với 2 loại message
        String userMessage = "Hệ thống đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.";
        String devMessage = ex.getMessage(); // Lấy thông điệp gốc của exception

        return ApiResponse.error("SYSTEM_ERROR", userMessage, devMessage);
    }
}