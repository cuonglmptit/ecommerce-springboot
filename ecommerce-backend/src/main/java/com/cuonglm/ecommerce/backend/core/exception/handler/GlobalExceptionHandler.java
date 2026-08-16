package com.cuonglm.ecommerce.backend.core.exception.handler;

import com.cuonglm.ecommerce.backend.core.exception.*;
import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Lỗi Validation từ Annotation (@Valid, @NotNull...) -> 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ApiResponse<Object> response = ApiResponse.error("VALIDATION_FAILED", "Dữ liệu gửi lên không hợp lệ.", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 2. Lỗi Validation gom nhiều lỗi từ Business Service -> 400
    @ExceptionHandler(ValidationErrorsException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(ValidationErrorsException ex) {
        ApiResponse<Object> response = ApiResponse.error("VALIDATION_FAILED", ex.getMessage(), ex.getErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 3. Lỗi Nghiệp vụ chung (BadRequestException, InvalidLocationDataException, IllegalArgumentException) -> 400
    @ExceptionHandler({BadRequestException.class, InvalidLocationDataException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(RuntimeException ex) {
        ApiResponse<Object> response = ApiResponse.error("BAD_REQUEST", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 4. Lỗi định dạng Request (JSON lỗi cú pháp, sai kiểu dữ liệu PathVariable) -> 400
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ApiResponse<Object>> handleMalformedRequest(Exception ex) {
        ApiResponse<Object> response = ApiResponse.error("MALFORMED_REQUEST", "Yêu cầu không hợp lệ hoặc sai định dạng dữ liệu.", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 5. Lỗi Chưa Đăng nhập / Sai thông tin xác thực -> 401
    @ExceptionHandler({UnauthenticatedException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Object>> handleUnauthenticated(Exception ex) {
        ApiResponse<Object> response = ApiResponse.error("UNAUTHORIZED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 6. Lỗi Tài khoản bị Khóa / Vô hiệu hóa -> 401
    @ExceptionHandler({LockedException.class, DisabledException.class})
    public ResponseEntity<ApiResponse<Object>> handleAccountDisabled(AuthenticationException ex) {
        String code = (ex instanceof LockedException) ? "ACCOUNT_LOCKED" : "ACCOUNT_DISABLED";
        ApiResponse<Object> response = ApiResponse.error(code, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 7. Lỗi Không đủ quyền (PermissionDeniedException, Spring AccessDeniedException) -> 403
    @ExceptionHandler({PermissionDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(Exception ex) {
        ApiResponse<Object> response = ApiResponse.error("FORBIDDEN", "Bạn không có quyền truy cập hoặc thực hiện thao tác này.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 8. Lỗi Không tìm thấy tài nguyên -> 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiResponse<Object> response = ApiResponse.error("RESOURCE_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 9. Lỗi Sai HTTP Method (POST vào endpoint GET) -> 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ApiResponse<Object> response = ApiResponse.error("METHOD_NOT_ALLOWED", "Phương thức HTTP '" + ex.getMethod() + "' không được hỗ trợ cho API này.");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    // 10. Lỗi Trùng lặp dữ liệu (Unique Constraint / Conflict) -> 409
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(ConflictException ex) {
        ApiResponse<Object> response = ApiResponse.error("DATA_CONFLICT", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 11. Lỗi Vượt quá kích thước file upload -> 413
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        ApiResponse<Object> response = ApiResponse.error("FILE_TOO_LARGE", "Kích thước file tải lên vượt quá giới hạn tối đa cho phép của hệ thống.");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    // 12. Lỗi Hệ thống bất ngờ (Fallback cuối cùng) -> 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUncaughtException(Exception ex) {
        log.error("Internal Server Error: ", ex);
        ApiResponse<Object> response = ApiResponse.error(
                "INTERNAL_SERVER_ERROR",
                "Hệ thống đang gặp sự cố. Vui lòng thử lại sau.",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}