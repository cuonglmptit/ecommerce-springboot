package com.cuonglm.ecommerce.backend.user.service;

import com.cuonglm.ecommerce.backend.user.dto.UserConflictInfoDTO;
import com.cuonglm.ecommerce.backend.user.dto.UserCreationDTO;
import com.cuonglm.ecommerce.backend.user.dto.UserCreationResultDTO;
import com.cuonglm.ecommerce.backend.user.dto.UserSecurityAndProfileDTO;
import com.cuonglm.ecommerce.backend.user.entity.User;
import com.cuonglm.ecommerce.backend.user.entity.UserOAuth2Account;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;
import com.cuonglm.ecommerce.backend.user.repository.UserRepository;
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

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
