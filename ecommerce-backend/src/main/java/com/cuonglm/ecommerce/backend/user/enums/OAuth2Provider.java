package com.cuonglm.ecommerce.backend.user.enums;

import java.util.stream.Stream;

/**
 * OAuth2Provider – Provider của đăng nhập bằng bên thứ 3.
 *
 * <p>
 * Enum đại diện cho Provider của UserOAuth2.
 * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 20 July 2025
 */
public enum OAuth2Provider {
    // Giá trị phải khớp với registrationId trong application.yml (github, facebook, google)
    GOOGLE("google"),
    FACEBOOK("facebook"),
    GITHUB("github"),
    LOCAL("local"); // Thêm LOCAL cho user đăng ký truyền thống

    private final String value;

    OAuth2Provider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Phương thức utility để chuyển đổi chuỗi registrationId thành đối tượng Enum.
     *
     * @param registrationId Chuỗi ID từ Spring Security.
     * @return Đối tượng OAuth2Provider tương ứng.
     * @throws IllegalArgumentException Nếu không tìm thấy Provider.
     */
    public static OAuth2Provider fromRegistrationId(String registrationId) {
        return Stream.of(OAuth2Provider.values())
                .filter(provider -> provider.value.equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp OAuth2 không hợp lệ: " + registrationId));
    }
}
