package com.cuonglm.ecommerce.backend.core.exception;

import java.util.Map;

/**
 * ValidationErrorsException – Exception tùy chỉnh để chứa nhiều lỗi validation.
 *
 * <p>
 * Được sử dụng khi cần gom lỗi (thu thập tất cả các lỗi) trong logic nghiệp vụ
 * (ví dụ: kiểm tra trùng lặp username VÀ email) thay vì dừng lại ở lỗi đầu tiên.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 12 November 2025
 */
public class ValidationErrorsException extends RuntimeException {
    private final Map<String, Object> errors;

    public ValidationErrorsException(String message, Map<String, Object> errors) {
        super(message);
        this.errors = errors;
    }

    public Map<String, Object> getErrors() {
        return errors;
    }
}
