package com.cuonglm.ecommerce.backend.core.exception;

/**
 /**
 * ConflictException – Lỗi xung đột dữ liệu (ví dụ: trùng lặp).
 * <p>
 * Map tới HTTP Status 409 (Conflict).
 * </p>
 *
 *
 * @author cuonglmptit
 * @since Thursday, 20 November 2025
 */
public class ConflictException extends BaseRuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s với %s '%s' đã tồn tại.", resourceName, fieldName, fieldValue));
    }
}