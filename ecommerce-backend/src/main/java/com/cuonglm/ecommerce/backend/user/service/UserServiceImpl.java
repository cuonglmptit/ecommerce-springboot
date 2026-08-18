package com.cuonglm.ecommerce.backend.user.service;

import com.cuonglm.ecommerce.backend.core.exception.ConflictException;
import com.cuonglm.ecommerce.backend.core.exception.ResourceNotFoundException;
import com.cuonglm.ecommerce.backend.core.utils.SecurityUtils;
import com.cuonglm.ecommerce.backend.user.dto.external.UpdateUserProfileRequest;
import com.cuonglm.ecommerce.backend.user.dto.external.UserProfileResponse;
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
    public Optional<UserSecurityAndProfileDTO> findOrCreateUserByOAuth2(
            UserOAuth2Info userOauth2Info, String initialRandomPasswordHash) {
        // 1. Kiểm tra liên kết OAuth2 đã tồn tại
        Optional<UserOAuth2Account> existingOAuth2Account = userOAuth2AccountRepository
                .findByProviderAndProviderUserId(userOauth2Info.getProvider(), userOauth2Info.getProviderUserId());

        if (existingOAuth2Account.isPresent()) {
            return Optional.of(UserSecurityAndProfileDTO.fromEntity(existingOAuth2Account.get().getUser()));
        }

        // 2. Nếu chưa liên kết, kiểm tra Email local đã verify chưa (Chống ATO)
        Optional<User> existingUserByEmail = userRepository.findByEmail(userOauth2Info.getEmail());
        if (existingUserByEmail.isPresent()) {
            User userToLink = existingUserByEmail.get();
            // Chỉ liên kết nếu User Local đã xác minh Email để ngăn chiếm đoạt tài khoản
            if (userToLink.isEmailVerified()) {
                // Tìm thấy User tồn tại và đã xác minh email thì liên kết
                linkOAuth2Account(userToLink, userOauth2Info);
                return Optional.of(UserSecurityAndProfileDTO.fromEntity(userToLink));
            }
        }

        // 3. Tạo mới User hoàn toàn
        User newUser = new User();
        newUser.setUsername(generateUniqueUsername(userOauth2Info));
        newUser.setEmail(userOauth2Info.getEmail());
        newUser.setFullName(userOauth2Info.getFullName());
        newUser.setAvatarUrl(userOauth2Info.getAvatarUrl());
        newUser.setPasswordHash(initialRandomPasswordHash);
        newUser.setLocalPasswordSet(false);
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setEmailVerified(true);
        newUser.addRole(UserRole.CUSTOMER);

        User savedUser = userRepository.save(newUser);
        linkOAuth2Account(savedUser, userOauth2Info);
        return Optional.of(UserSecurityAndProfileDTO.fromEntity(savedUser));
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
        String base = (info.getEmail() != null && !info.getEmail().isBlank())
                ? info.getEmail().split("@")[0].replaceAll("[^a-zA-Z0-9_.]", "")
                : info.getProvider().name().toLowerCase();

        if (base.isBlank() || base.length() < 3) {
            base = "user";
        }

        // Tạo suffix ngắn 6 ký tự đảm bảo O(1) Unique
        String candidate = base;
        if (userRepository.existsByUsername(candidate)) {
            candidate = base + "_" + UUID.randomUUID().toString().substring(0, 6);
        }
        return candidate;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();
        UserProfileInfoView view = userRepository.findProfileInfoById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        return UserProfileResponse.fromView(view);
    }


    //</editor-fold>
    //Tạo người dùng qua Form Login
    @Override
    @Transactional
    public UserCreationResultDTO createLocalUser(UserCreationDTO dto) {
        // 1. Kiểm tra tính duy nhất (tránh lỗi duplicate key sau)
        if (userRepository.existsByUsername(dto.username())) {
            throw new ConflictException("Tên đăng nhập đã được sử dụng: " + dto.username());
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("Email đã được sử dụng: " + dto.email());
        }

        // 2. Tạo Entity User
        User newUser = new User();
        newUser.setUsername(dto.username());
        newUser.setEmail(dto.email().trim());

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
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(UserSecurityAndProfileDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true) // Nên có readOnly = true cho các method get
    public UserInfoDTO getCurrentAuthenticatedUserInfo() {
        // 1. Gọi Core để lấy chuỗi định danh (Username/Email)
        Long userId = SecurityUtils.getRequiredCurrentUserId();

        // 2. Lấy Interface View từ DB
        UserInfoView view = userRepository.findUserInfoById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin người dùng: " + userId));

        // 3. MAPPING: Chuyển đổi từ Entity User sang Internal DTO
        return UserInfoDTO.fromView(view);
    }

    @Override
    public Optional<UserInfoDTO> findUserInfoById(Long userId) {
        return userRepository.findUserInfoById(userId)
                .map(UserInfoDTO::fromView);
    }

    @Override
    public void grantRoleToUser(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        if (user.getRoles().add(role)) {
            userRepository.save(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserReference(Long userId) {
        return userRepository.getReferenceById(userId);
    }

    @Override
    public UserProfileResponse updateCurrentUserProfile(UpdateUserProfileRequest request) {
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

        if (request.fullName() != null) user.setFullName(request.fullName().trim());
        if (request.phoneNumber() != null) user.setPhoneNumber(request.phoneNumber().trim());
        if (request.avatarUrl() != null) user.setAvatarUrl(request.avatarUrl().trim());
        if (request.gender() != null) user.setGender(request.gender());
        if (request.dateOfBirth() != null) user.setDateOfBirth(request.dateOfBirth());

        User savedUser = userRepository.save(user);
        return UserProfileResponse.fromEntity(savedUser);
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
