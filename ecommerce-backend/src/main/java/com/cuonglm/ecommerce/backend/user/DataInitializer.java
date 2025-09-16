package com.cuonglm.ecommerce.backend.user;

import com.cuonglm.ecommerce.backend.auth.service.registeredclient.DatabaseRegisteredClientRepository;
import com.cuonglm.ecommerce.backend.user.entity.User;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;
import com.cuonglm.ecommerce.backend.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DataInitializer – Khởi tạo dữ liệu người dùng và OAuth2 Clients mặc định.
 *
 * <p>
 * Đảm bảo các tài khoản quan trọng và cấu hình OAuth2 luôn sẵn sàng khi ứng dụng khởi động.
 * Đồng thời, tạo tài khoản SYSTEM_AUDITOR để phục vụ JPA Auditing trong các tác vụ nền.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 16 October 2025
 */

@Configuration
public class DataInitializer {

    // ✅ Tên SYSTEM AUDITOR phải khớp với tên trong SecurityAuditorAware
    private static final String SYSTEM_AUDITOR_USERNAME = "SYSTEM_AUDITOR";

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               DatabaseRegisteredClientRepository registeredClientRepository) {
        return args -> {

            // ----------------------------
            // 1. Khởi tạo SYSTEM AUDITOR (MANDATORY cho Auditing)
            // ----------------------------
            User systemAuditor = userRepository.findByUsername(SYSTEM_AUDITOR_USERNAME).orElseGet(() -> {
                System.out.println("⏳ Creating SYSTEM_AUDITOR user...");
                User system = new User();
                system.setUsername(SYSTEM_AUDITOR_USERNAME);
                // Mật khẩu không quan trọng, nhưng phải có giá trị NOT NULL
                system.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
                system.setEmail("system@example.com");
                system.setPhoneNumber(null); // Không cần số điện thoại
                system.setLocalPasswordSet(true);
                system.setRoles(Set.of(UserRole.ADMIN)); // Vai trò đặc biệt
                system.setStatus(UserStatus.ACTIVE);

                // Lưu user này trước, nó sẽ có createdBy/modifiedBy là NULL (vì chưa có auditor)
                // Hoặc bạn có thể tự set id cho nó là 0 nếu dùng GenerationType.IDENTITY, nhưng tốt nhất là dùng AUTO.
                // Nếu dùng AUTO, hệ thống sẽ tự cấp ID.
                return userRepository.save(system);
            });

            // ----------------------------
            // 2. Khởi tạo User mặc định (Audit sẽ dùng SYSTEM_AUDITOR)
            // ----------------------------
            if (userRepository.findByUsername("admin").isEmpty()) {
                System.out.println("⏳ Creating default 'admin' user...");
                User admin = new User();
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode("123456"));
                admin.setLocalPasswordSet(true);
                admin.setEmail("admin@example.com");
                admin.setPhoneNumber("+84123456789");
                admin.setAvatarUrl("default_avatar_url");
                admin.setRoles(Set.of(UserRole.ADMIN, UserRole.CUSTOMER)); // Thêm CUSTOMER nếu ADMIN cũng là khách hàng
                admin.setStatus(UserStatus.ACTIVE);
                userRepository.save(admin);
            }
            if (userRepository.findByUsername("cuong").isEmpty()) {
                System.out.println("⏳ Creating default 'cuong' user...");
                User user = new User();
                user.setUsername("cuong");
                user.setPasswordHash(passwordEncoder.encode("123456"));
                user.setLocalPasswordSet(true);
                user.setEmail("cuongcodervippro200x@gmail.com");
                user.setEmailVerified(true);
                user.setPhoneNumber("+84987654321");
                user.setRoles(Set.of(UserRole.CUSTOMER));
                user.setStatus(UserStatus.ACTIVE);
                userRepository.save(user);
            }
            if (userRepository.findByUsername("cuong1").isEmpty()) {
                System.out.println("⏳ Creating default 'cuong' user...");
                User user = new User();
                user.setUsername("cuong1");
                user.setPasswordHash(passwordEncoder.encode("123456"));
                user.setLocalPasswordSet(true);
                user.setEmail("cuongcodervippro100x@gmail.com");
                user.setEmailVerified(true);
                user.setPhoneNumber("+84987654328");
                user.setRoles(Set.of(UserRole.CUSTOMER));
                user.setStatus(UserStatus.BANNED);
                userRepository.save(user);
            }
            if (userRepository.findByUsername("banned").isEmpty()) {
                System.out.println("⏳ Creating default 'banned' user...");
                User user = new User();
                user.setUsername("banned");
                user.setPasswordHash(passwordEncoder.encode("123456"));
                user.setLocalPasswordSet(true);
                user.setEmail("bannedcodervippro200x1@gmail.com");
                user.setPhoneNumber("+84987654322");
                user.setRoles(Set.of(UserRole.CUSTOMER));
                user.setStatus(UserStatus.BANNED);
                userRepository.save(user);
            }
            if (userRepository.findByUsername("deleted").isEmpty()) {
                System.out.println("⏳ Creating default 'deleted' user...");
                User user = new User();
                user.setUsername("deleted");
                user.setPasswordHash(passwordEncoder.encode("123456"));
                user.setLocalPasswordSet(true);
                user.setEmail("deletedcodervippro200x1@gmail.com");
                user.setPhoneNumber("+84987654323");
                user.setRoles(Set.of(UserRole.CUSTOMER));
                user.setStatus(UserStatus.DELETED);
                userRepository.save(user);
            }
            if (userRepository.findByUsername("suspended").isEmpty()) {
                System.out.println("⏳ Creating default 'suspended' user...");
                User user = new User();
                user.setUsername("suspended");
                user.setPasswordHash(passwordEncoder.encode("123456"));
                user.setLocalPasswordSet(true);
                user.setEmail("suspendedcodervippro200x1@gmail.com");
                user.setPhoneNumber("+84987654324");
                user.setRoles(Set.of(UserRole.CUSTOMER));
                user.setStatus(UserStatus.SUSPENDED);
                userRepository.save(user);
            }
            if (userRepository.findByUsername("deactivated").isEmpty()) {
                System.out.println("⏳ Creating default 'deactivated' user...");
                User user = new User();
                user.setUsername("deactivated");
                user.setPasswordHash(passwordEncoder.encode("123456"));
                user.setLocalPasswordSet(true);
                user.setEmail("deactivatedcodervippro200x1@gmail.com");
                user.setPhoneNumber("+84987654325");
                user.setRoles(Set.of(UserRole.CUSTOMER));
                user.setStatus(UserStatus.DEACTIVATED);
                userRepository.save(user);
            }

            // ----------------------------
            // 3. Khởi tạo RegisteredClient
            // ----------------------------
            // (Giữ nguyên logic khởi tạo OAuth2 Clients của bạn)

            if (registeredClientRepository.findByClientId("client") == null) {
                // ... (Logic tạo client 1)
                RegisteredClient client1 = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("client")
                        .clientName("client")
                        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("https://www.manning.com/authorized")
                        .scope(OidcScopes.OPENID)
                        .scope("read")
                        .scope("write")
                        .clientIdIssuedAt(Instant.now())
                        .clientSettings(ClientSettings.builder()
                                .requireProofKey(true)
                                .requireAuthorizationConsent(true)
                                .build())
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(30))
                                .refreshTokenTimeToLive(Duration.ofHours(2))
                                .build())
                        .build();
                registeredClientRepository.save(client1);
            }

            if (registeredClientRepository.findByClientId("client123") == null) {
                // ... (Logic tạo client 2)
                RegisteredClient client1 = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("client123")
                        .clientSecret(passwordEncoder.encode("123456"))
                        .clientName("client123")
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("https://www.manning.com/authorized")
                        .scope(OidcScopes.OPENID)
                        .scope("read")
                        .scope("write")
                        .clientIdIssuedAt(java.time.Instant.now())
                        .clientSettings(ClientSettings.builder()
                                .requireAuthorizationConsent(true)
                                .build())
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(30))
                                .refreshTokenTimeToLive(Duration.ofHours(2))
                                .build())
                        .build();

                registeredClientRepository.save(client1);
            }

            if (registeredClientRepository.findByClientId("client2") == null) {
                // ... (Logic tạo client 3)
                RegisteredClient client2 = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("client2")
                        .clientName("client2")
                        .clientSecret(passwordEncoder.encode("123456"))
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .scope("admin.read")
                        .scope("admin.write")
                        .clientIdIssuedAt(java.time.Instant.now())
                        .clientSettings(ClientSettings.builder()
                                .requireAuthorizationConsent(false)
                                .build())
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(60))
                                .build())
                        .build();

                registeredClientRepository.save(client2);
            }

            System.out.println("✅ DataInitializer: Default users and OAuth2 clients have been initialized.");
        };
    }
}