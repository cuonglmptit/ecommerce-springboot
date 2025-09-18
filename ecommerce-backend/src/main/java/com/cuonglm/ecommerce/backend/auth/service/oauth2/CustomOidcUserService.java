package com.cuonglm.ecommerce.backend.auth.service.oauth2;

import com.cuonglm.ecommerce.backend.auth.model.CustomOidcPrincipal;
import com.cuonglm.ecommerce.backend.auth.service.common.AuthUserHandler;
import com.cuonglm.ecommerce.backend.auth.service.oauth2.google.GoogleUserOAuth2Info;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserSecurityAndProfileDTO;
import com.cuonglm.ecommerce.backend.user.enums.OAuth2Provider;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import com.cuonglm.ecommerce.backend.user.service.oauth2.UserOAuth2Info;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Lớp này triển khai OIDC (OpenID Connect) UserService để xử lý thông tin người dùng từ IdP chuẩn full OIDC như Google.
 *
 * <p>
 * Xử lý thông tin khi nhận về openid của một IdP cung cấp full chuẩn OIDC.
 * </p>
 *
 * @author cuonglmptit
 * @since Monday, 27 October 2025
 */
@Component
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    // Ủy quyền cho OidcUserService để tận dụng chức năng loadUser mặc định của OIDC
    private final OidcUserService delegate = new OidcUserService();
    private final PasswordEncoder passwordEncoder;
    private final AuthUserHandler authUserHandler;

    // Sử dụng UserService để lưu hoặc cập nhật thông tin người dùng local
    private final UserService userService;

    public CustomOidcUserService(PasswordEncoder passwordEncoder, AuthUserHandler authUserHandler, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.authUserHandler = authUserHandler;
        this.userService = userService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Bước 1: Ủy quyền cho OidcUserService xử lý luồng lấy UserInfo
        // OidcUserService sẽ tự động xử lý và xác thực ID Token.
        OidcUser oidcUser = delegate.loadUser(userRequest);

        // Bước 2: Xử lý User Info và lưu trữ/cập nhật user local.
        try {
            return processOidcUser(userRequest, oidcUser);
        } catch (Exception ex) {
            // In ra lỗi để gỡ lỗi
            System.err.println("--- LỖI XỬ LÝ OIDC USER (processOidcUser FAILED) ---");
            ex.printStackTrace();
            System.err.println("-------------------------------------------------------");

            // Đóng gói lỗi gốc vào một OAuth2AuthenticationException để Spring Security xử lý
            OAuth2Error oauth2Error = new OAuth2Error(
                    "oidc_user_processing_error",
                    "Failed to process OIDC user: " + ex.getMessage(),
                    null
            );

            throw new OAuth2AuthenticationException(oauth2Error, ex);
        }
    }

    /**
     * Phương thức xử lý logic tạo hoặc cập nhật user local từ OidcUser.
     *
     * @param userRequest - OidcUserRequest
     * @param oidcUser    - OidcUser
     * @return - OidcUser đã xử lý
     */
    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        // 1. Xác định Provider
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        final OAuth2Provider provider;
        try {
            provider = OAuth2Provider.fromRegistrationId(registrationId);
            // Có thể bỏ qua kiểm tra này nếu bạn chỉ có Google là OIDC
        } catch (IllegalArgumentException ex) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("unsupported_provider", ex.getMessage(), null)
            );
        }

        // 2. Chuẩn hóa thông tin bằng lớp GoogleOidcUserOAuth2Info đã định nghĩa
        // KHÔNG CẦN COPY MAP vì chúng ta không sửa đổi và Google luôn trả về dữ liệu chuẩn
        UserOAuth2Info oauth2Info = new GoogleUserOAuth2Info(oidcUser.getAttributes());

        // 3. Kiểm tra thông tin bắt buộc
        if (oauth2Info.getEmail() == null || oauth2Info.getProviderUserId() == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("missing_info", "OIDC User thiếu Email hoặc Subject ID (sub).", null)
            );
        }

        // 4. Tạo Hash mật khẩu ngẫu nhiên
        String randomPassword = UUID.randomUUID().toString();
        String initialRandomPasswordHash = passwordEncoder.encode(randomPassword); // Dùng PasswordEncoder

        // 5. Gọi UserService để tìm hoặc tạo người dùng local
        UserSecurityAndProfileDTO userDTO =
                userService.findOrCreateUserByOAuth2(oauth2Info, initialRandomPasswordHash)
                        .orElseThrow(() -> new OAuth2AuthenticationException(
                                new OAuth2Error("user_creation_failed", "Không thể tạo hoặc tìm người dùng từ OIDC Provider.", null)
                        ));

        // 5. Kiểm tra trạng thái của người dùng
        userDTO = authUserHandler.checkAndHandleUserStatus(userDTO);

        // 7. Trả về Principal dạng CustomOidcPrincipal
        return new CustomOidcPrincipal(userDTO, oidcUser);
    }
}
