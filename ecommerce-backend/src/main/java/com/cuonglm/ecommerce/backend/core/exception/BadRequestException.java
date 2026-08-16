package com.cuonglm.ecommerce.backend.core.exception;

/**
 * BadRequestException – Lỗi vi phạm tiền điều kiện hoặc logic nghiệp vụ (HTTP 400).
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
public class BadRequestException extends BaseRuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}