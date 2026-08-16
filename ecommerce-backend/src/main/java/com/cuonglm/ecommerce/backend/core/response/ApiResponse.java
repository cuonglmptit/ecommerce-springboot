package com.cuonglm.ecommerce.backend.core.response;

import com.cuonglm.ecommerce.backend.core.constants.CoreConstants;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * ApiResponse – Wrapper chuẩn hóa toàn bộ phản hồi API của hệ thống.
 *
 * @author cuonglmptit
 * @since Wednesday, 30 July 2025
 */
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String userMessage;
    private String devMessage;
    private T data;
    private Instant timestamp;
    private String requestId;
    private Map<String, Object> extra;

    public ApiResponse() {
        this.timestamp = Instant.now();
        this.extra = new HashMap<>();
        this.requestId = MDC.get(CoreConstants.MDC_KEY_REQUEST_ID);
    }

    public ApiResponse(boolean success, String code, String userMessage, T data, String devMessage) {
        this.success = success;
        this.code = code;
        this.userMessage = userMessage;
        this.data = data;
        this.devMessage = devMessage;
        this.timestamp = Instant.now();
        this.extra = new HashMap<>();
        this.requestId = MDC.get(CoreConstants.MDC_KEY_REQUEST_ID);
    }

    // Static factory methods - SUCCESS
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", "Thao tác thành công", data, null);
    }

    public static <T> ApiResponse<T> success(String userMessage, T data) {
        return new ApiResponse<>(true, "SUCCESS", userMessage, data, null);
    }

    public static <T> ApiResponse<T> success(String code, String userMessage, T data) {
        return new ApiResponse<>(true, code, userMessage, data, null);
    }

    // Static factory methods - ERROR
    public static <T> ApiResponse<T> error(String code, String userMessage) {
        return new ApiResponse<>(false, code, userMessage, null, null);
    }

    public static <T> ApiResponse<T> error(String code, String userMessage, String devMessage) {
        return new ApiResponse<>(false, code, userMessage, null, devMessage);
    }

    public static <T> ApiResponse<T> error(String code, String userMessage, Map<String, Object> extra) {
        ApiResponse<T> response = new ApiResponse<>(false, code, userMessage, null, null);
        if (extra != null) {
            response.setExtra(extra);
        }
        return response;
    }

    public ApiResponse<T> addExtra(String key, Object value) {
        this.extra.put(key, value);
        return this;
    }

    //<editor-fold desc="Getters/Setters">
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getDevMessage() {
        return devMessage;
    }

    public void setDevMessage(String devMessage) {
        this.devMessage = devMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = (extra != null) ? extra : new HashMap<>();
    }
    //</editor-fold>
}