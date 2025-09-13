package com.cuonglm.ecommerce.backend.core.response;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
/**
 * ApiResponse – Định nghĩa chuẩn response trả về từ các API của hệ thống.
 *
 * <p>
 * Lớp này đóng vai trò là wrapper chung cho mọi API response.
 * Giúp chuẩn hóa cấu trúc dữ liệu trả về để client dễ dàng xử lý.
 * </p>
 *
 * <h3>Các thành phần chính:</h3>
 * <ul>
 *     <li><b>success</b>: Trạng thái thành công hay thất bại của request.</li>
 *     <li><b>code</b>: Mã trạng thái hoặc mã lỗi (ví dụ: 200, USER_NOT_FOUND).</li>
 *     <li><b>userMessage</b>: Thông điệp trả về cho người dùng.</li>
 *     <li><b>devMessage</b>: Thông điệp trả về cho lập trình viên FE.</li>
 *     <li><b>data</b>: Dữ liệu trả về (generic, có thể là bất kỳ object nào).</li>
 *     <li><b>timestamp</b>: Thời điểm server tạo response.</li>
 *     <li><b>requestId</b>: Định danh request để trace log/debug (nếu có).</li>
 *     <li><b>extra</b>: Thông tin bổ sung động (key-value), ví dụ metadata, pagination.</li>
 * </ul>
 *
 * <h3>Ví dụ:</h3>
 * <pre>
 * ApiResponse.success(user);
 *
 * ApiResponse.error("USER_NOT_FOUND", "Không tìm thấy user");
 *
 * ApiResponse.success(list)
 *             .addExtra("total", 100)
 *             .addExtra("page", 1);
 * </pre>
 *
 * @author cuonglmptit
 * @since Wednesday, 30 July 2025
 */

public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String userMessage; // Thông điệp cho người dùng cuối
    private T data;
    private String devMessage; // Thông điệp cho lập trình viên FE
    private Instant timestamp;
    private String requestId;
    private Map<String, Object> extra;

    // Constructors
    public ApiResponse() {
        this.timestamp = Instant.now();
        this.extra = new HashMap<>();
    }

    public ApiResponse(boolean success, String code, String userMessage, T data, String devMessage) {
        this.success = success;
        this.code = code;
        this.userMessage = userMessage;
        this.data = data;
        this.devMessage = devMessage;
        this.timestamp = Instant.now();
        this.extra = new HashMap<>();
    }

    // Static factory methods
    public static <T> ApiResponse<T> success(String code, String userMessage, T data) {
        return new ApiResponse<>(true, code, userMessage, data, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "200", "Thành công", data, null);
    }

    public static <T> ApiResponse<T> success(String userMessage, T data) {
        return new ApiResponse<>(true, "200", userMessage, data, null);
    }

    public static <T> ApiResponse<T> error(String code, String userMessage) {
        return new ApiResponse<>(false, code, userMessage, null, null);
    }

    public static <T> ApiResponse<T> error(String code, String userMessage, String devMessage) {
        return new ApiResponse<>(false, code, userMessage, null, devMessage);
    }

    // Thêm extra field động (giống .NET)
    public ApiResponse<T> addExtra(String key, Object value) {
        this.extra.put(key, value);
        return this;
    }

    //<editor-fold desc="Getters & Setters">
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDevMessage() {
        return devMessage;
    }

    public void setDevMessage(String devMessage) {
        this.devMessage = devMessage;
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
        this.extra = extra;
    }
    //</editor-fold>
}
