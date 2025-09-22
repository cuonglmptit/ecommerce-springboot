package com.cuonglm.ecommerce.backend.user.service;

import com.cuonglm.ecommerce.backend.user.dto.internal.*;
import com.cuonglm.ecommerce.backend.user.entity.User;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.service.oauth2.UserOAuth2Info;

import java.util.List;
import java.util.Optional;

/**
 * UserService – Xử lý business logic liên quan đến User.
 *
 * <p>
 * Lớp interface để định nghĩa các hành vi chính của UserService.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 30 July 2025
 */
public interface UserService {
    /**
     * Tạo user local (đã hash password) với trạng thái ACTIVE.
     * <p>
     * Phương thức này sẽ thiết lập cờ isLocalPasswordSet = true.
     * </p>
     *
     * @param userCreationDTO Dữ liệu tạo người dùng mới.
     * @return User Entity đã được tạo.
     */
    UserCreationResultDTO createLocalUser(UserCreationDTO userCreationDTO);

    /**
     * Tìm kiếm UserSecurityAndProfileDTO theo username hoặc email.
     *
     * @param usernameOrEmail Tham số username hoặc email để tìm kiếm.
     * @return Optional chứa UserSecurityDTO nếu tìm thấy, ngược lại là Optional.empty().
     */
    Optional<UserSecurityAndProfileDTO> findSecurityDetailsByUsernameOrEmail(String usernameOrEmail);

    /**
     * Tìm kiếm hoặc tạo mới User dựa trên thông tin OAuth2. Nếu không có, tạo User mới và link UserOAuth2 Account.
     *
     * @param userOauth2Info            Thông tin OAuth2 của người dùng.
     * @param initialRandomPasswordHash Mật khẩu ngẫu nhiên đã được hash để gán cho User mới đăng ký qua OAuth2.
     * @return Optional chứa UserSecurityAndProfileDTO nếu tìm thấy hoặc tạo mới thành công, ngược lại là Optional.empty().
     */
    Optional<UserSecurityAndProfileDTO> findOrCreateUserByOAuth2(
            UserOAuth2Info userOauth2Info, String initialRandomPasswordHash);

    /**
     * Tìm người dùng bằng Email.
     *
     * @param email Email cần tìm.
     * @return Optional chứa User nếu tìm thấy, ngược lại là Optional.empty().
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Tìm người dùng bằng số điện thoại.
     *
     * @param phoneNumber Số điện thoại cần tìm.
     * @return Optional chứa User nếu tìm thấy, ngược lại là Optional.empty().
     */
    Optional<User> findUserByPhoneNumber(String phoneNumber);


    /**
     * Tìm danh sách User theo Username hoặc Email.
     * <p>
     * Dùng cho trường hợp muốn kiểm tra trùng lặp trước khi tạo tài khoản và trả về TẤT CẢ các User trùng
     * (có thể 2 user khác nhau) bằng 1 query duy nhất.
     * Nghĩa là nếu như Username hoặc Email truyền vào có 2 User trùng sẽ trả về danh sách có 2 người đó.
     * </p>
     *
     * @param username Tên người dùng cần kiểm tra.
     * @param email    Địa chỉ email cần kiểm tra.
     * @return List chứa UserConflictInfoDTO nếu tìm thấy, ngược lại là List rỗng
     */
    List<UserConflictInfoDTO> findConflictUsers(String username, String email);

    /**
     * Lấy ra người dùng đã đăng nhập.
     *
     * @return Thông tin người dùng đã đăng nhập.
     */
    UserInfoDTO getCurrentAuthenticatedUserInfo();

    /**
     * Lấy ra thông tin người dùng theo thông tin Id
     * @param userId Id của người dùng
     * @return Thông tin người dùng
     */
    UserInfoDTO findUserInfoById(Long userId);

    /**
     * Cấp một vai trò mới cho người dùng.
     *
     * @param userId ID của người dùng
     * @param role   Vai trò muốn cấp (ví dụ: UserRole.SELLER)
     */
    void grantRoleToUser(Long userId, UserRole role);

    List<User> getAllUsers();

    User updateUser(Long id, User user);

    Optional<User> findUserById(Long id);

    void deleteUser(Long id);

    // <editor-fold desc="Các phương thức kiểm tra">
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    // </editor-fold>
}