// File: com/cuonglm/ecommerce/backend/user/DataInitializer.java

package com.cuonglm.ecommerce.backend.user;

import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.attribute.repository.AttributeOptionRepository;
import com.cuonglm.ecommerce.backend.attribute.repository.AttributeRepository;
import com.cuonglm.ecommerce.backend.auth.service.registeredclient.DatabaseRegisteredClientRepository;
import com.cuonglm.ecommerce.backend.category.entity.Category;
import com.cuonglm.ecommerce.backend.category.entity.CategoryAttribute;
import com.cuonglm.ecommerce.backend.category.enums.FilterType;
import com.cuonglm.ecommerce.backend.category.repository.CategoryAttributeRepository;
import com.cuonglm.ecommerce.backend.category.repository.CategoryRepository;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.media.entity.Media;
import com.cuonglm.ecommerce.backend.media.enums.MediaFormat;
import com.cuonglm.ecommerce.backend.media.enums.MediaProvider;
import com.cuonglm.ecommerce.backend.media.enums.MediaType;
import com.cuonglm.ecommerce.backend.media.repository.MediaRepository;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import com.cuonglm.ecommerce.backend.shop.enums.ShopStatus;
import com.cuonglm.ecommerce.backend.shop.repository.ShopRepository;
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
                               DatabaseRegisteredClientRepository registeredClientRepository,
                               // Thêm Repositories cho Catalog
                               CategoryRepository categoryRepository,
                               AttributeRepository attributeRepository,
                               AttributeOptionRepository attributeOptionRepository,
                               CategoryAttributeRepository categoryAttributeRepository,
                               ShopRepository shopRepository,
                               MediaRepository mediaRepository) {
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
            // ... (Giữ nguyên logic tạo các user khác
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

            // Lấy User admin đã được tạo để gán làm Owner cho Shop và Uploader cho Media
            User adminUser = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("Admin user not found, cannot proceed with data initialization."));

            // ----------------------------
            // 3. Khởi tạo Data Catalog (Category & Attribute)
            // ----------------------------
            initCatalogData(categoryRepository, attributeRepository, attributeOptionRepository, categoryAttributeRepository, shopRepository);

            // ----------------------------
            // 4. Khởi tạo Media mặc định
            // ----------------------------
            initDefaultMedia(mediaRepository, adminUser); // 👈 GỌI PHƯƠNG THỨC KHỞI TẠO MEDIA


            // ----------------------------
            // 5. Khởi tạo RegisteredClient
            // ----------------------------
            // ... (Giữ nguyên logic khởi tạo OAuth2 Clients của bạn)

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
                                .accessTokenTimeToLive(Duration.ofMinutes(100))
                                .refreshTokenTimeToLive(Duration.ofHours(200))
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

    /**
     * Khởi tạo 3 Media mặc định nếu chưa tồn tại.
     *
     * @param mediaRepository Repository của Media.
     * @param uploader        User thực hiện upload.
     */
    private void initDefaultMedia(MediaRepository mediaRepository, User uploader) {
        System.out.println("⏳ Initializing Default Media...");

        // Media 1: Ảnh sản phẩm demo (ID: 000...0001)
        if (mediaRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001")).isEmpty()) {
            Media media1 = createMedia(
                    "t-shirt-image-1",
                    "https://cloudinary.com/product/t-shirt.jpg",
                    "ecommerce/products/user-1/t-shirt1-12345",
                    "Ảnh áo thun đỏ",
                    "Áo Thun Đỏ Demo",
                    MediaType.IMAGE,
                    MediaProvider.CLOUDINARY,
                    MediaFormat.JPG,
                    uploader
            );
            media1.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
            mediaRepository.save(media1);
        }

        // Media 2: Ảnh avatar demo (ID: 000...0002)
        if (mediaRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000002")).isEmpty()) {
            Media media2 = createMedia(
                    "t-shirt-image-2",
                    "https://cloudinary.com/product/t-shirt.jpg",
                    "ecommerce/products/user-1/t-shirt2-12345",
                    "Ảnh áo thun đỏ",
                    "Áo Thun Đỏ Demo",
                    MediaType.IMAGE,
                    MediaProvider.CLOUDINARY,
                    MediaFormat.JPG,
                    uploader
            );
            media2.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
            mediaRepository.save(media2);
        }

        // Media 2: Ảnh avatar demo (ID: 000...0002)
        if (mediaRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000003")).isEmpty()) {
            Media media3 = createMedia(
                    "t-shirt-image-3",
                    "https://cloudinary.com/product/t-shirt.jpg",
                    "ecommerce/products/user-1/t-shirt3-12345",
                    "Ảnh áo thun đỏ",
                    "Áo Thun Đỏ Demo",
                    MediaType.IMAGE,
                    MediaProvider.CLOUDINARY,
                    MediaFormat.JPG,
                    uploader
            );
            media3.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
            mediaRepository.save(media3);
        }

        // Ảnh cho user id3 để test
        User cuong = new User(); cuong.setId(3L);
        if (mediaRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000004")).isEmpty()) {
            Media media3 = createMedia(
                    "t-shirt-image-4",
                    "https://cloudinary.com/product/t-shirt4.jpg",
                    "ecommerce/products/user-2/t-shirt3-12345",
                    "Img3",
                    "Img3",
                    MediaType.IMAGE,
                    MediaProvider.CLOUDINARY,
                    MediaFormat.JPG,
                    cuong
            );
            media3.setId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
            mediaRepository.save(media3);
        }

        System.out.println("✅ Default Media initialized successfully.");
    }


    /**
     * Helper method để tạo Media.
     */
    private Media createMedia(String idPlaceholder, String url, String externalId, String alt, String title, MediaType type, MediaProvider provider, MediaFormat format, User uploader) {
        Media media = new Media();
        media.setUrl(url);
        media.setExternalId(externalId);
        media.setAlt(alt);
        media.setTitle(title);
        media.setType(type);
        media.setProvider(provider);
        media.setFormat(format);
        media.setUploader(uploader);
        media.setStatus(BasicStatus.ACTIVE);
        return media;
    }

    /**
     * Khởi tạo dữ liệu mẫu cho Category và Attribute.
     */
    private void initCatalogData(CategoryRepository categoryRepository,
                                 AttributeRepository attributeRepository,
                                 AttributeOptionRepository attributeOptionRepository,
                                 CategoryAttributeRepository categoryAttributeRepository,
                                 ShopRepository shopRepository) {

        System.out.println("⏳ Initializing FULL Catalog Data (Restoring All Categories & Attributes)...");

        // -------------------------------------------------------------------------
        // 1. Khởi tạo/Tìm Shops
        // -------------------------------------------------------------------------
        Shop defaultShop = shopRepository.findById(1L).orElseGet(() -> {
            Shop shop = new Shop();
            shop.setName("Shop Demo A");
            User admin = new User(); admin.setId(2L);
            shop.setOwner(admin);
            shop.setStatus(ShopStatus.ACTIVE);
            return shopRepository.save(shop);
        });

        Shop secondShop = shopRepository.findById(2L).orElseGet(() -> {
            Shop shop = new Shop();
            shop.setName("Shop Demo B");
            User cuong = new User(); cuong.setId(3L);
            shop.setOwner(cuong);
            shop.setStatus(ShopStatus.ACTIVE);
            return shopRepository.save(shop);
        });

        // -------------------------------------------------------------------------
        // 2. Thuộc tính BIẾN THỂ (VARIATION Attributes)
        // -------------------------------------------------------------------------
        Attribute colorAttribute = attributeRepository.findByCode("MAU_SAC").orElseGet(() -> {
            Attribute attr = createAttribute("Màu Sắc", "MAU_SAC", AttributeScope.GLOBAL, AttributeType.VARIATION, null);
            return attributeRepository.save(attr);
        });

        Attribute sizeAttribute = attributeRepository.findByShopIdAndCode(defaultShop.getId(), "KICH_CO").orElseGet(() -> {
            Attribute attr = createAttribute("Kích Cỡ (Shop)", "KICH_CO", AttributeScope.SHOP, AttributeType.VARIATION, defaultShop);
            return attributeRepository.save(attr);
        });

        Attribute sizeGlobalAttribute = attributeRepository.findByCode("KICH_THUOC").orElseGet(() -> {
            Attribute attr = createAttribute("Kích Thước", "KICH_THUOC", AttributeScope.GLOBAL, AttributeType.VARIATION, null);
            return attributeRepository.save(attr);
        });

        Attribute storageVarAttribute = attributeRepository.findByCode("DUNG_LUONG_VAR").orElseGet(() -> {
            Attribute attr = createAttribute("Dung Lượng Bộ Nhớ", "DUNG_LUONG_VAR", AttributeScope.GLOBAL, AttributeType.VARIATION, null);
            return attributeRepository.save(attr);
        });

        Attribute shippingMethodAttribute = attributeRepository.findByShopIdAndCode(secondShop.getId(), "CHIEU_DAI").orElseGet(() -> {
            Attribute attr = createAttribute("Chiều dài", "CHIEU_DAI", AttributeScope.SHOP, AttributeType.VARIATION, secondShop);
            return attributeRepository.save(attr);
        });

        // Options cho VARIATION
        createOptionIfAbsent(attributeOptionRepository, colorAttribute, "Đỏ", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000001");
        createOptionIfAbsent(attributeOptionRepository, colorAttribute, "Xanh Dương", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000002");
        createOptionIfAbsent(attributeOptionRepository, colorAttribute, "Đen", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000005");

        createOptionIfAbsent(attributeOptionRepository, sizeAttribute, "S", AttributeScope.SHOP, defaultShop, "00000000-0000-0000-0000-000000000003");
        createOptionIfAbsent(attributeOptionRepository, sizeAttribute, "M", AttributeScope.SHOP, defaultShop, "00000000-0000-0000-0000-000000000004");

        createOptionIfAbsent(attributeOptionRepository, sizeGlobalAttribute, "S", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000060");
        createOptionIfAbsent(attributeOptionRepository, sizeGlobalAttribute, "M", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000061");
        createOptionIfAbsent(attributeOptionRepository, sizeGlobalAttribute, "L", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000062");
        createOptionIfAbsent(attributeOptionRepository, sizeGlobalAttribute, "XL", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000063");

        createOptionIfAbsent(attributeOptionRepository, storageVarAttribute, "128GB", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000070");
        createOptionIfAbsent(attributeOptionRepository, storageVarAttribute, "256GB", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000071");
        createOptionIfAbsent(attributeOptionRepository, storageVarAttribute, "512GB", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000072");

        createOptionIfAbsent(attributeOptionRepository, shippingMethodAttribute, "50CM", AttributeScope.SHOP, secondShop, "00000000-0000-0000-0000-000000000008");
        createOptionIfAbsent(attributeOptionRepository, shippingMethodAttribute, "100CM", AttributeScope.SHOP, secondShop, "00000000-0000-0000-0000-000000000009");


        // -------------------------------------------------------------------------
        // 3. Thuộc tính KỸ THUẬT (SPECIFICATION Attributes)
        // -------------------------------------------------------------------------
        Attribute brandAttribute = attributeRepository.findByCode("THUONG_HIEU").orElseGet(() -> {
            Attribute attr = createAttribute("Thương Hiệu", "THUONG_HIEU", AttributeScope.GLOBAL, AttributeType.SPECIFICATION, null);
            return attributeRepository.save(attr);
        });

        Attribute materialAttribute = attributeRepository.findByCode("CHAT_LIEU").orElseGet(() -> {
            Attribute attr = createAttribute("Chất Liệu", "CHAT_LIEU", AttributeScope.GLOBAL, AttributeType.SPECIFICATION, null);
            return attributeRepository.save(attr);
        });

        Attribute originAttribute = attributeRepository.findByCode("XUAT_XU").orElseGet(() -> {
            Attribute attr = createAttribute("Xuất Xứ", "XUAT_XU", AttributeScope.GLOBAL, AttributeType.SPECIFICATION, null);
            return attributeRepository.save(attr);
        });

        Attribute storageSpecAttribute = attributeRepository.findByCode("DUNG_LUONG").orElseGet(() -> {
            Attribute attr = createAttribute("Dung Lượng Mặc Định", "DUNG_LUONG", AttributeScope.GLOBAL, AttributeType.SPECIFICATION, null);
            return attributeRepository.save(attr);
        });

        Attribute powerAttribute = attributeRepository.findByCode("CONG_SUAT").orElseGet(() -> {
            Attribute attr = createAttribute("Công Suất", "CONG_SUAT", AttributeScope.GLOBAL, AttributeType.SPECIFICATION, null);
            return attributeRepository.save(attr);
        });

        // Options cho SPECIFICATION Attributes
        createOptionIfAbsent(attributeOptionRepository, brandAttribute, "Nike", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000010");
        createOptionIfAbsent(attributeOptionRepository, brandAttribute, "Adidas", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000011");
        createOptionIfAbsent(attributeOptionRepository, brandAttribute, "Apple", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000012");
        createOptionIfAbsent(attributeOptionRepository, brandAttribute, "Samsung", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000013");
        createOptionIfAbsent(attributeOptionRepository, brandAttribute, "No Brand / OEM", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000014");

        createOptionIfAbsent(attributeOptionRepository, materialAttribute, "Cotton 100%", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000020");
        createOptionIfAbsent(attributeOptionRepository, materialAttribute, "Polyester", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000021");
        createOptionIfAbsent(attributeOptionRepository, materialAttribute, "Lụa / Silk", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000022");
        createOptionIfAbsent(attributeOptionRepository, materialAttribute, "Kaki", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000023");

        createOptionIfAbsent(attributeOptionRepository, originAttribute, "Việt Nam", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000030");
        createOptionIfAbsent(attributeOptionRepository, originAttribute, "Trung Quốc", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000031");
        createOptionIfAbsent(attributeOptionRepository, originAttribute, "Hàn Quốc", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000032");

        createOptionIfAbsent(attributeOptionRepository, storageSpecAttribute, "64GB", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000040");
        createOptionIfAbsent(attributeOptionRepository, storageSpecAttribute, "128GB", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000041");

        createOptionIfAbsent(attributeOptionRepository, powerAttribute, "45W", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000050");
        createOptionIfAbsent(attributeOptionRepository, powerAttribute, "60W", AttributeScope.GLOBAL, null, "00000000-0000-0000-0000-000000000051");


        // -------------------------------------------------------------------------
        // 4. TOÀN BỘ CÂY DANH MỤC (Restoring 100% All 14 Categories)
        // -------------------------------------------------------------------------

        // --- CẤP 0 (ROOTS) ---
        Category fashionCategory = categoryRepository.findByName("Thời Trang Nam").orElseGet(() ->
                categoryRepository.save(createCategory(1L, "Thời Trang Nam", "Các loại sản phẩm thời trang nam.", "/1/", 0, null))
        );

        Category electronicsCategory = categoryRepository.findByName("Thiết Bị Điện Tử").orElseGet(() ->
                categoryRepository.save(createCategory(2L, "Thiết Bị Điện Tử", "Điện thoại, máy tính, thiết bị nghe nhìn.", "/2/", 0, null))
        );

        Category homeApplianceCategory = categoryRepository.findByName("Thiết Bị Gia Dụng").orElseGet(() ->
                categoryRepository.save(createCategory(3L, "Thiết Bị Gia Dụng", "Đồ gia dụng lớn nhỏ cho gia đình.", "/3/", 0, null))
        );

        Category globalOthersCategory = categoryRepository.findByName("Danh Mục Khác").orElseGet(() ->
                categoryRepository.save(createCategory(4L, "Danh Mục Khác", "Các sản phẩm chưa phân loại thuộc ngành hàng khác.", "/4/", 0, null))
        );


        // --- CẤP 1 (CHILDREN OF THỜI TRANG NAM) ---
        Category shirtCategory = categoryRepository.findByName("Áo Nam").orElseGet(() ->
                categoryRepository.save(createCategory(5L, "Áo Nam", "Áo sơ mi, áo thun, áo khoác nam.", "/1/5/", 1, fashionCategory))
        );

        Category pantsCategory = categoryRepository.findByName("Quần Nam").orElseGet(() ->
                categoryRepository.save(createCategory(6L, "Quần Nam", "Quần jeans, quần tây, quần đùi nam.", "/1/6/", 1, fashionCategory))
        );

        Category fashionOthersCategory = categoryRepository.findByName("Thời Trang Nam Khác").orElseGet(() ->
                categoryRepository.save(createCategory(7L, "Thời Trang Nam Khác", "Các sản phẩm thời trang nam khác.", "/1/7/", 1, fashionCategory))
        );


        // --- CẤP 2 (LEAF NODES OF ÁO NAM) ---
        Category longSleeveShirt = categoryRepository.findByName("Sơ Mi Dài Tay").orElseGet(() ->
                categoryRepository.save(createCategory(8L, "Sơ Mi Dài Tay", "Áo sơ mi dài tay công sở, kiểu.", "/1/5/8/", 2, shirtCategory))
        );

        Category tShirtCategory = categoryRepository.findByName("Áo Thun Nam").orElseGet(() ->
                categoryRepository.save(createCategory(9L, "Áo Thun Nam", "Áo thun cổ tròn, polo nam.", "/1/5/9/", 2, shirtCategory))
        );


        // --- CẤP 1 & 2 (CHILDREN OF THIẾT BỊ ĐIỆN TỬ) ---
        Category mobileCategory = categoryRepository.findByName("Điện Thoại & Phụ Kiện").orElseGet(() ->
                categoryRepository.save(createCategory(10L, "Điện Thoại & Phụ Kiện", "Điện thoại thông minh, tai nghe, sạc dự phòng.", "/2/10/", 1, electronicsCategory))
        );

        Category smartphoneCategory = categoryRepository.findByName("Điện Thoại Di Động").orElseGet(() ->
                categoryRepository.save(createCategory(11L, "Điện Thoại Di Động", "Smartphone iOS, Android.", "/2/10/11/", 2, mobileCategory))
        );

        Category electronicsOthersCategory = categoryRepository.findByName("Thiết Bị Điện Tử Khác").orElseGet(() ->
                categoryRepository.save(createCategory(12L, "Thiết Bị Điện Tử Khác", "Các thiết bị điện tử kỹ thuật số khác.", "/2/12/", 1, electronicsCategory))
        );


        // --- CẤP 1 & 2 (CHILDREN OF THIẾT BỊ GIA DỤNG) ---
        Category largeApplianceCategory = categoryRepository.findByName("Đồ Gia Dụng Lớn").orElseGet(() ->
                categoryRepository.save(createCategory(13L, "Đồ Gia Dụng Lớn", "Tủ lạnh, máy giặt, máy sấy.", "/3/13/", 1, homeApplianceCategory))
        );

        Category coolingCategory = categoryRepository.findByName("Quạt & Máy Làm Mát").orElseGet(() ->
                categoryRepository.save(createCategory(14L, "Quạt & Máy Làm Mát", "Quạt đứng, quạt hơi nước, quạt trần.", "/3/13/14/", 2, largeApplianceCategory))
        );


        // -------------------------------------------------------------------------
        // 5. LIÊN KẾT SPECIFICATION ATTRIBUTES
        // -------------------------------------------------------------------------
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, shirtCategory, brandAttribute, 1, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, shirtCategory, materialAttribute, 2, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, shirtCategory, originAttribute, 3, true, FilterType.CHECKBOX);

        linkCategoryAttributeIfAbsent(categoryAttributeRepository, tShirtCategory, brandAttribute, 1, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, tShirtCategory, materialAttribute, 2, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, tShirtCategory, originAttribute, 3, true, FilterType.CHECKBOX);

        linkCategoryAttributeIfAbsent(categoryAttributeRepository, smartphoneCategory, brandAttribute, 1, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, smartphoneCategory, storageSpecAttribute, 2, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, smartphoneCategory, originAttribute, 3, true, FilterType.CHECKBOX);

        linkCategoryAttributeIfAbsent(categoryAttributeRepository, coolingCategory, brandAttribute, 1, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, coolingCategory, powerAttribute, 2, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, coolingCategory, originAttribute, 3, true, FilterType.CHECKBOX);

        // Gán cho cả Root Category Thiết Bị Điện Tử (ID 2)
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, electronicsCategory, brandAttribute, 1, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, electronicsCategory, originAttribute, 2, true, FilterType.CHECKBOX);


        // -------------------------------------------------------------------------
        // 6. LIÊN KẾT VARIATION ATTRIBUTES (DÙNG CHO GỢI Ý PHÂN LOẠI SALE INFO)
        // -------------------------------------------------------------------------
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, shirtCategory, colorAttribute, 10, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, shirtCategory, sizeGlobalAttribute, 11, true, FilterType.CHECKBOX);

        linkCategoryAttributeIfAbsent(categoryAttributeRepository, tShirtCategory, colorAttribute, 10, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, tShirtCategory, sizeGlobalAttribute, 11, true, FilterType.CHECKBOX);

        linkCategoryAttributeIfAbsent(categoryAttributeRepository, smartphoneCategory, colorAttribute, 10, true, FilterType.CHECKBOX);
        linkCategoryAttributeIfAbsent(categoryAttributeRepository, smartphoneCategory, storageVarAttribute, 11, true, FilterType.CHECKBOX);

        System.out.println("✅ RESTORED ALL 14 CATEGORIES & ALL SPECIFICATION / VARIATION ATTRIBUTES SUCCESSFULLY!");
    }


    // --- Helper methods to simplify object creation ---
    private Attribute createAttribute(String name, String code, AttributeScope scope, AttributeType type, Shop shop) {
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setCode(code);
        attribute.setScope(scope);
        attribute.setType(type);
        attribute.setShop(shop);
        attribute.setStatus(AttributeStatus.ACTIVE);
        return attribute;
    }

    private void createOptionIfAbsent(AttributeOptionRepository repo, Attribute attr, String value, AttributeScope scope, Shop shop, String defaultUuid) {
        if (repo.findByAttributeAndValue(attr, value).isEmpty()) {
            AttributeOption option = createAttributeOption(attr, value, scope, shop);
            option.setId(UUID.fromString(defaultUuid));
            repo.save(option);
        }
    }

    private void linkCategoryAttributeIfAbsent(CategoryAttributeRepository repo, Category category, Attribute attribute, Integer sortOrder, Boolean isFilterable, FilterType filterType) {
        if (repo.findByCategoryAndAttribute(category, attribute).isEmpty()) {
            CategoryAttribute categoryAttribute = createCategoryAttribute(category, attribute, sortOrder, isFilterable, filterType);
            repo.save(categoryAttribute);
        }
    }

    private Category createCategory(Long id, String name, String description, String path, Integer depth, Category parent) {
        Category category = new Category();
        // category.setId(id); // Dùng cho ví dụ, nhưng đã comment
        category.setName(name);
        category.setDescription(description);
        category.setPath(path);
        category.setDepth(depth);
        category.setParent(parent);
        category.setSortOrder(0);
        category.setStatus(BasicStatus.ACTIVE);
        return category;
    }

    private Attribute createAttribute(String name, String code, AttributeScope scope, Shop shop) {
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setCode(code);
        attribute.setScope(scope);
        attribute.setShop(shop);
        attribute.setStatus(AttributeStatus.ACTIVE);
        return attribute;
    }

    private AttributeOption createAttributeOption(Attribute attribute, String value, AttributeScope scope, Shop shop) {
        AttributeOption option = new AttributeOption();
        option.setAttribute(attribute);
        option.setValue(value);
        option.setScope(scope);
        option.setShop(shop);
        option.setStatus(AttributeStatus.ACTIVE);
        return option;
    }

    private CategoryAttribute createCategoryAttribute(Category category, Attribute attribute, Integer sortOrder, Boolean isFilterable, FilterType filterType) {
        CategoryAttribute categoryAttribute = new CategoryAttribute();
        categoryAttribute.setCategory(category);
        categoryAttribute.setAttribute(attribute);
        categoryAttribute.setSortOrder(sortOrder);
        categoryAttribute.setFilterable(isFilterable);
        categoryAttribute.setFilterType(filterType);
        return categoryAttribute;
    }
}