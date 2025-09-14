package com.cuonglm.ecommerce.backend.user.service.oauth2;

import com.cuonglm.ecommerce.backend.user.enums.OAuth2Provider;

/**
 * UserOAuth2Info – Contract chuẩn hóa dữ liệu User từ các nhà cung cấp OAuth2/OIDC bên ngoài.
 *
 * <p>
 * UserService chỉ làm việc với Interface này, không cần biết chi tiết về Google, GitHub, v.v.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 31 October 2025
 */
public interface UserOAuth2Info {
    /**
     * ID duy nhất của User tại nhà cung cấp (Ví dụ: 'sub' của Google, 'id' của GitHub).
     *
     * @return ID duy nhất của User tại nhà cung cấp.
     */
    String getProviderUserId();

    /**
     * Tên của nhà cung cấp, dưới dạng Enum để đảm bảo Type Safety.
     *
     * @return Nhà cung cấp OAuth2 VD Oauth2Provider.GOOGLE, OAuth2Provider.GITHUB,...
     */
    OAuth2Provider getProvider(); // Thay đổi ở đây

    /**
     * Email của User.
     */
    String getEmail();

    /**
     * Tên đầy đủ/tên hiển thị của User.
     */
    String getFullName();

    /**
     * URL ảnh đại diện của User.
     * url này là từ nhà cung cấp,
     * có thể User Service sẽ tự fetch và lưu trữ ảnh này vào hệ thống lưu trữ riêng như Cloudinary, AWS S3,...
     *
     * @return URL ảnh đại diện của User với nhà cung cấp OAuth2
     */
    String getAvatarUrl();
}
