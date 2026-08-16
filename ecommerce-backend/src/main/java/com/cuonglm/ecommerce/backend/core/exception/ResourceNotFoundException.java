package com.cuonglm.ecommerce.backend.core.exception;

import java.util.UUID;

/**
 * ResourceNotFoundException – Lỗi không tìm thấy tài nguyên.
 * <p>
 * Map tới HTTP Status 404 (Not Found).
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 18 November 2025
 */
public class ResourceNotFoundException extends BaseRuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s với ID '%d' không tồn tại.", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, UUID id) {
        super(String.format("%s với ID '%s' không tồn tại.", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s với %s '%s' không tồn tại.", resourceName, fieldName, fieldValue));
    }
}