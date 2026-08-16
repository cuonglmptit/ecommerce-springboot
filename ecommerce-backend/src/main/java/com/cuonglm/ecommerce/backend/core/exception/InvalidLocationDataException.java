package com.cuonglm.ecommerce.backend.core.exception;

/**
 * InvalidLocationDataException – Lỗi dữ liệu địa chỉ / vị trí không hợp lệ (HTTP 400).
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
public class InvalidLocationDataException extends BaseRuntimeException {
    public InvalidLocationDataException(String message) {
        super(message);
    }
}