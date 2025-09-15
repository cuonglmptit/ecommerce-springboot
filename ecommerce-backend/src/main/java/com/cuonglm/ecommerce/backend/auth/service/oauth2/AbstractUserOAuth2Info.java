package com.cuonglm.ecommerce.backend.auth.service.oauth2;

import com.cuonglm.ecommerce.backend.user.service.oauth2.UserOAuth2Info;

import java.util.Map;

/**
 * AbstractUserOAuth2Info – Cung cấp base cho việc ánh xạ Attributes OAuth2 thô.
 *
 * <p>
 * Implements UserOAuth2Info và cung cấp các phương thức chung.
 * <br>
 * Lớp trừu tượng này giúp giảm thiểu mã lặp lại trong các lớp cụ thể của nhà cung cấp OAuth2.
 * <br>
 * Mỗi provider cụ thể (Google, GitHub, Facebook,...)
 * sẽ kế thừa lớp này và chỉ cần triển khai các phương thức riêng biệt.
 * <br>
 * VD như GitHub thì sub sẽ là id, Google thì sub sẽ là sub,...
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 31 October 2025
 */
public abstract class AbstractUserOAuth2Info implements UserOAuth2Info {
    protected final Map<String, Object> attributes;

    public AbstractUserOAuth2Info(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * Phương thức tiện ích để lấy giá trị String.
     * Trả về null nếu thuộc tính không tồn tại hoặc giá trị là null.
     */
    protected String getStringAttribute(String key) {
        Object value = attributes.get(key);
        // Trả về String.valueOf(value) nếu muốn tránh lỗi (nếu value là số), nhưng toString() là an toàn hơn.
        return value != null ? value.toString() : null;
    }

    /**
     * Phương thức tiện ích để lấy giá trị Long.
     * Trả về null nếu thuộc tính không tồn tại hoặc không phải là số.
     */
    protected Long getLongAttribute(String key) {
        Object value = attributes.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.valueOf((String) value);
            } catch (NumberFormatException ignored) {
                // Bỏ qua nếu không thể chuyển đổi
            }
        }
        return null;
    }
}
