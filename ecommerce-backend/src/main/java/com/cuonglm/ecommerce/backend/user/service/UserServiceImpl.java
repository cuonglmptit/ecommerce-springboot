package com.cuonglm.ecommerce.backend.user.service;

import com.cuonglm.ecommerce.backend.core.exception.ResourceNotFoundException;
import com.cuonglm.ecommerce.backend.core.exception.UnauthenticatedException;
import com.cuonglm.ecommerce.backend.core.utils.SecurityUtils;
import com.cuonglm.ecommerce.backend.user.dto.internal.*;
import com.cuonglm.ecommerce.backend.user.entity.User;
import com.cuonglm.ecommerce.backend.user.entity.UserOAuth2Account;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;
import com.cuonglm.ecommerce.backend.user.repository.UserOAuth2AccountRepository;
import com.cuonglm.ecommerce.backend.user.repository.UserRepository;
import com.cuonglm.ecommerce.backend.user.service.oauth2.UserOAuth2Info;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * UserServiceImpl – Triển khai logic cho UserService.
 *
 * <p>
 * Các phương thức này sẽ tương tác với UserRepository để thực hiện các thao tác CRUD.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 30 July 2025
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserOAuth2AccountRepository userOAuth2AccountRepository;

    public UserServiceImpl(UserRepository userRepository, UserOAuth2AccountRepository userOAuth2AccountRepository) {
        this.userRepository = userRepository;
        this.userOAuth2AccountRepository = userOAuth2AccountRepository;
    }

    //<editor-fold desc="Tạo người dùng qua Oauth2">
    @Override
    @Transactional
    public Optional<UserSecurityAndProfileDTO> findOrCreateUserByOAuth2(
            UserOAuth2Info userOauth2Info, String initialRandomPasswordHash) {
        // 1. PRIMARY CHECK: Tìm kiếm UserOAuth2Account liên kết với User bằng Provider và ProviderUserId
        Optional<UserOAuth2Account> existingOAuth2Account = userOAuth2AccountRepository
                .findByProviderAndProviderUserId(userOauth2Info.getProvider(), userOauth2Info.getProviderUserId());
        // Nếu như User đã từng đăng nhập bằng Provider này thì trả về thông tin User
        if (existingOAuth2Account.isPresent()) {
            return Optional.of(new UserSecurityAndProfileDTO(existingOAuth2Account.get().getUser()));
        }

        // 2. SECONDARY CHECK: Nếu chưa có liên kết, tìm User Local/tồn tại bằng Email
        Optional<User> existingUserByEmail = userRepository.findByEmail(userOauth2Info.getEmail());
        if (existingUserByEmail.isPresent()) {
            User userToLink = existingUserByEmail.get();
            // Chỉ liên kết nếu User Local đã xác minh Email để ngăn chiếm đoạt tài khoản
            if (userToLink.isEmailVerified()) {
                // Tìm thấy User tồn tại và đã xác minh email thì liên kết
                linkOAuth2Account(userToLink, userOauth2Info);
                return Optional.of(new UserSecurityAndProfileDTO(userToLink));
            }
            // Nếu chưa xác minh, logic sẽ bỏ qua khối if này và chuyển xuống bước 3 (tạo mới)
            // để bảo vệ tài khoản Local.
        }

        // 3. CREATE NEW: Nếu không tìm thấy hoặc tài khoản local chưa xác minh, tạo mới hoàn toàn
        User newUser = createNewUserFromOAuth2(userOauth2Info, initialRandomPasswordHash);
        linkOAuth2Account(newUser, userOauth2Info);
        return Optional.of(new UserSecurityAndProfileDTO(newUser));
    }

    // Helper Methods

    /**
     * Tạo User mới từ thông tin đăng nhập qua Oauth2
     *
     * @param info         Thông tin kiểu {@link UserOAuth2Info} về người dùng
     * @param passwordHash Mật khẩu ngẫu nhiên đã được hash để gán cho User mới đăng ký qua OAuth2
     * @return Thực thể {@link User} mới đã được lưu vào Database (nhưng vẫn trong Transaction)
     */
    private User createNewUserFromOAuth2(UserOAuth2Info info, String passwordHash) {
        User user = new User();
        // Giả định username là email (có thể cần logic tạo username ngẫu nhiên nếu email là null)
        user.setUsername(generateUniqueUsername(info));
        user.setEmail(info.getEmail());
        user.setFullName(info.getFullName());
        user.setAvatarUrl(info.getAvatarUrl());

        // Hashing và Trạng thái Mật khẩu
        user.setPasswordHash(passwordHash);
        user.setLocalPasswordSet(false);

        // Cấu hình Trạng thái và Xác minh
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true); // Email được coi là đã xác minh bởi nhà cung cấp

        // Gán role mặc định là Customer
        user.addRole(UserRole.CUSTOMER);

        // Trả về User đã được tạo mới
        return userRepository.save(user);
    }

    /**
     * Tạo {@link UserOAuth2Account} và liên kết với một {@link User} thông qua thông tin của {@link UserOAuth2Info}.
     *
     * @param user Người dùng muốn liên kết với oauth2
     * @param info Thông tin oauth2 của người dùng
     */
    private void linkOAuth2Account(User user, UserOAuth2Info info) {
        UserOAuth2Account newAccount = new UserOAuth2Account();
        newAccount.setProvider(info.getProvider());
        newAccount.setProviderUserId(info.getProviderUserId());
        user.addOAuthAccount(newAccount); // Dùng Helper method trong User entity
        // Không cần userOauth2Repository.save(newAccount); nếu User đã có cascade
        // Tuy nhiên cứ gọi cho chắc ăn? Mà có cần ko nhỉ, kệ ko cần gọi cho chuyên nghiệp
//        userOAuth2AccountRepository.save(newAccount);
    }

    /**
     * Tạo username hợp lệ và duy nhất.
     * Sử dụng tiền tố email/fullname, sau đó kiểm tra tính duy nhất bằng UserRepository.
     *
     * @param info Thông tin của người dùng kiểu {@link UserOAuth2Info} để tạo ra Username duy nhất
     * @return Username chưa được sử dụng trong Database
     */
    private String generateUniqueUsername(UserOAuth2Info info) {
        String baseUsername;

        // Ưu tiên: 1. Tiền tố Email -> 2. Tên đầy đủ (FullName) -> 3. Fallback ID
        if (info.getEmail() != null && !info.getEmail().isBlank()) {
            // Lấy tiền tố email và loại bỏ ký tự không hợp lệ (giữ lại a-zA-Z0-9_.)
            baseUsername = info.getEmail().split("@")[0].replaceAll("[^a-zA-Z0-9_.]", "");
        } else if (info.getFullName() != null && !info.getFullName().isBlank()) {
            // Lấy FullName, chuyển thành slug và loại bỏ ký tự không hợp lệ
            baseUsername = info.getFullName().replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9_.]", "");
        } else {
            // Fallback: provider_id cuối (chỉ 4 ký tự cuối)
            String providerIdSuffix = info.getProviderUserId().substring(Math.max(0, info.getProviderUserId().length() - 4));
            baseUsername = info.getProvider().name().toLowerCase() + "_" + providerIdSuffix;
        }

        // Đảm bảo username không rỗng sau khi chuẩn hóa
        if (baseUsername.isBlank()) {
            baseUsername = info.getProvider().name().toLowerCase() + "_" + UUID.randomUUID().toString().substring(0, 6);
        }

        // Xử lý tính duy nhất (Thêm số đếm nếu trùng)
        String finalUsername = baseUsername;
        int counter = 0;

        // Vòng lặp kiểm tra tính duy nhất
        while (userRepository.existsByUsername(finalUsername)) {
            counter++;
            finalUsername = baseUsername + "_" + counter;
            System.out.println("Số lần thử username có trùng hay không: " + counter);
            // Giới hạn vòng lặp để tránh lỗi vô hạn (chỉ là biện pháp đề phòng)
            if (counter > 100) {
                finalUsername = info.getProvider().name().toLowerCase() + "_" + UUID.randomUUID().toString().substring(0, 8);
                break;
            }
        }

        return finalUsername;
    }

    //</editor-fold>
    //<editor-fold desc="Tạo người dùng qua Form Login">
    @Override
    @Transactional
    public UserCreationResultDTO createLocalUser(UserCreationDTO dto) {
        // 1. Kiểm tra tính duy nhất (tránh lỗi duplicate key sau)
        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("Username already exists: " + dto.username());
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already exists: " + dto.email());
        }

        // 2. Tạo Entity User
        User newUser = new User();
        newUser.setUsername(dto.username());
        newUser.setEmail(dto.email());

        // 3. Xử lý Mật khẩu Local
        // Sử dụng trực tiếp chuỗi hash đã được cung cấp từ Auth Service
        newUser.setPasswordHash(dto.passwordHash());
        newUser.setLocalPasswordSet(true);

        // 4. Cấu hình Trạng thái & Vai trò
        newUser.addRole(dto.initialRole() != null ? dto.initialRole() : UserRole.CUSTOMER);
        newUser.setStatus(dto.userStatus() != null ? dto.userStatus() : UserStatus.ACTIVE);
        newUser.setEmailVerified(dto.emailVerified());
        newUser.setPhoneVerified(false);

        // 5. Lưu User vào DB
        User savedUser = userRepository.save(newUser);
        // Trả về DTO kết quả, không trả về Entity User
        return new UserCreationResultDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }
    //</editor-fold>


    @Override
    public List<UserConflictInfoDTO> findConflictUsers(String username, String email) {
        List<User> conflictUsers = userRepository.findAllByUsernameOrEmail(username, email);

        // Chuyển đổi (Mapping) Entity User sang DTO UserConflictInfoDTO trước khi trả về
        return conflictUsers.stream()
                .map(user -> new UserConflictInfoDTO(user.getUsername(), user.getEmail()))
                .toList();
    }

    @Override
    public Optional<UserSecurityAndProfileDTO> findSecurityDetailsByUsernameOrEmail(String usernameOrEmail) {
        // 1. Gọi repository để tìm User theo username hoặc email
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);

        // 2. Sử dụng Optional.map() để xử lý chuyển đổi
        // Nếu userOptional.isPresent(), map() sẽ áp dụng hàm lambda
        // Nếu userOptional.isEmpty(), map() sẽ trả về Optional.empty()
        return userOptional.map(UserSecurityAndProfileDTO::new);
    }

    /**
     * Helper method để ánh xạ (map) từ UserInfoView Projection sang UserInfoDTO.
     *
     * @param view View Projection từ UserRepository
     * @return DTO chứa thông tin User
     */
    private UserInfoDTO mapToUserInfoDTO(UserInfoView view) {
        return new UserInfoDTO(
                view.getId(),
                view.getEmail(),
                view.getStatus(),
                view.isEmailVerified(),
                view.isPhoneVerified(),
                view.getRoles()
        );
    }

    @Override
    @Transactional(readOnly = true) // Nên có readOnly = true cho các method get
    public UserInfoDTO getCurrentAuthenticatedUserInfo() {
        // 1. Gọi Core để lấy chuỗi định danh (Username/Email)
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthenticatedException("Người dùng chưa đăng nhập hoặc phiên làm việc hết hạn."));

        // 2. Lấy Interface View từ DB
        UserInfoView view = userRepository.findUserInfoById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin người dùng: " + userId));

        // 3. MAPPING: Chuyển đổi từ Entity User sang Internal DTO
        // (Bước này bị thiếu trong code cũ của bạn)
        return mapToUserInfoDTO(view);
    }

    @Override
    public UserInfoDTO findUserInfoById(Long userId) {
        UserInfoView view = userRepository.findUserInfoById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin người dùng: " + userId));

        return mapToUserInfoDTO(view);
    }

    @Override
    @Transactional
    public void grantRoleToUser(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User ID " + userId + " không tồn tại."));

        // Thêm Role. Set sẽ tự động đảm bảo tính duy nhất
        if (user.getRoles().add(role)) {
            userRepository.save(user); // Chỉ cần lưu nếu có thay đổi
        }
    }


    @Override
    public User updateUser(Long id, User user) {
        return null;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

}
