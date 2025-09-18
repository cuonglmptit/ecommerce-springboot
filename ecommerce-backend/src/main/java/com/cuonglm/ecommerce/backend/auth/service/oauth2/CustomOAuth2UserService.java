package com.cuonglm.ecommerce.backend.auth.service.oauth2;

import com.cuonglm.ecommerce.backend.auth.model.CustomOAuth2Principal;
import com.cuonglm.ecommerce.backend.auth.service.common.AuthUserHandler;
import com.cuonglm.ecommerce.backend.auth.service.oauth2.github.GitHubEmailFetcher;
import com.cuonglm.ecommerce.backend.auth.service.oauth2.github.GithubUserOAuth2Info;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserSecurityAndProfileDTO;
import com.cuonglm.ecommerce.backend.user.enums.OAuth2Provider;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * CustomOAuth2UserService – Lớp custom để thực hiện xử lý đăng nhập bằng Oauth2 thứ 3 như Github.
 * Vì Github, Facebook,... ngoại trừ Google không hỗ trợ OpenID chuẩn vì nó chỉ là OAuth2 thôi.
 *
 * <p>
 * Sử dụng mô hình Ủy quyền (Delegation) để tận dụng DefaultOAuth2UserService,
 * sau đó xử lý việc lưu trữ hoặc cập nhật thông tin người dùng local.
 * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 26 October 2025
 */
@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private static final String EMAIL_KEY = "email";

    // Ủy quyền cho DefaultOAuth2UserService để tận dụng chức năng loadUser mặc định
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    private final PasswordEncoder passwordEncoder;

    // Sử dụng UserService để lưu hoặc cập nhật thông tin người dùng local
    private final UserService userService;

    private final AuthUserHandler authUserHandler;


    /**
     * Sử dụng GitHubEmailFetcher để lấy email
     * GitHub chỉ trả về access token và sau đó DefaultOAuth2UserService chỉ load thông tin profile cơ bản
     * Nên ta cần gọi thêm GitHubEmailFetcher để lấy email bằng token đó.
     */
    private final GitHubEmailFetcher githubEmailFetcher;

    public CustomOAuth2UserService(PasswordEncoder passwordEncoder, UserService userService, AuthUserHandler authUserHandler, GitHubEmailFetcher githubEmailFetcher) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.authUserHandler = authUserHandler;
        this.githubEmailFetcher = githubEmailFetcher;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Bước 1: Ủy quyền cho DefaultOAuth2UserService xử lý luồng lấy UserInfo
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // Bước 2: Xử lý User Info và lưu trữ/cập nhật user local.
        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            // In ra lỗi để gỡ lỗi
            System.err.println("--- LỖI XỬ LÝ OAUTH2 USER (processOAuth2User FAILED) ---");
            ex.printStackTrace();
            System.err.println("-------------------------------------------------------");

            OAuth2Error oauth2Error = new OAuth2Error(
                    "oauth2_user_processing_error",
                    "Failed to process OAuth2 user: " + ex.getMessage(),
                    null
            );
            throw new OAuth2AuthenticationException(oauth2Error, ex);
        }
    }

    /**
     * Xử lý OAuth2User dựa trên nhà cung cấp (registrationId).
     *
     * @param userRequest Thông tin yêu cầu người dùng OAuth2
     * @param oauth2User  Người dùng OAuth2 được tải từ IdP
     * @return
     */
    private CustomOAuth2Principal processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // Bước 1: Chuyển đổi chuỗi thành Enum
        final OAuth2Provider provider;
        try {
            provider = OAuth2Provider.fromRegistrationId(registrationId);
        } catch (IllegalArgumentException ex) {
            // Nếu không tìm thấy Enum, ném ngoại lệ OAuth2AuthenticationException
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("unsupported_provider", ex.getMessage(), null)
            );
        }

        System.out.println("Chạy processOAuth2User cho: " + provider);
        // Bước 2: Dùng switch (enum)
        return switch (provider) {
            case GITHUB -> processGitHubUser(userRequest, oauth2User);
            case FACEBOOK -> processFacebookUser(userRequest, oauth2User);
            // LOCAL không bao giờ xảy ra trong luồng OAuth2, nhưng có thể dùng default để bảo vệ
            default -> throw new OAuth2AuthenticationException(
                    new OAuth2Error("logic_error", "Lỗi logic: Provider không được xử lý trong luồng OAuth2.", null)
            );
        };
    }

    //<editor-fold desc="LOGIC XỬ LÝ THEO TỪNG PROVIDER">
    private CustomOAuth2Principal processGitHubUser(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        String email = (String) attributes.get(EMAIL_KEY);

        // 1. Xử lý email: Nếu email là NULL, gọi GitHubEmailFetcher
        if (email == null || email.isBlank()) {
            System.out.println("Email thiếu cho GitHub, đang gọi GitHubEmailFetcher...");
            String token = userRequest.getAccessToken().getTokenValue();

            // GỌI HELPER SERVICE và GÁN LẠI VÀO ATTRIBUTES
            email = githubEmailFetcher.fetchPrimaryVerifiedEmailAddress(token);
            attributes.put(EMAIL_KEY, email); // <-- Gán email đã fetch vào attributes
        }

        if (email == null || email.isBlank()) {
            // Nếu không thể lấy được email, không thể tạo tài khoản local
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_missing", "Không thể lấy email đã xác minh từ GitHub.", null)
            );
        }

        // 2. Tạo đối tượng chuẩn hóa thông tin
        // Tận dụng GithubUserOAuth2Info để chuẩn hóa: providerUserId, name, avatarUrl
        GithubUserOAuth2Info oauth2Info = new GithubUserOAuth2Info(attributes);

        // 3. AUTH SERVICE: Tự sinh và Hash mật khẩu ngẫu nhiên
        String randomPassword = UUID.randomUUID().toString();
        String initialRandomPasswordHash = passwordEncoder.encode(randomPassword);

        // 4. Gọi UserService để tìm hoặc tạo người dùng local
        UserSecurityAndProfileDTO userDTO =
                userService.findOrCreateUserByOAuth2(oauth2Info, initialRandomPasswordHash)
                        .orElseThrow(() -> new OAuth2AuthenticationException(
                                new OAuth2Error("user_creation_failed", "Không thể tạo hoặc tìm người dùng từ GitHub.", null)
                        ));

        // 5. Kiểm tra trạng thái của người dùng
        userDTO = authUserHandler.checkAndHandleUserStatus(userDTO);

        // 6. Trả về Principal dạng CustomOAuth2Principal
        return new CustomOAuth2Principal(userDTO, oauth2User);
    }

    private CustomOAuth2Principal processFacebookUser(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        // Chưa có logic đặc thù cho Facebook
        return null;
    }
    //</editor-fold>
}
