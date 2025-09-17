package com.cuonglm.ecommerce.backend.user.repository;

import com.cuonglm.ecommerce.backend.user.dto.internal.UserInfoView;
import com.cuonglm.ecommerce.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 30 July 2025
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    // Kỹ thuật projection sử dụng Closed Interface-based Projection Tự động chỉ SELECT các cột có trong UserInfoView
    Optional<UserInfoView> findUserInfoById(Long id);

    /**
     * Tìm kiếm User theo username hoặc email
     * @param username Username muốn tìm
     * @param email Email muốn tìm
     * @return Optional chứa User nếu tìm thấy, ngược lại là Optional.empty()
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Tìm danh sách User theo Username hoặc Email
     * @param username Username muốn tìm
     * @param email Email muốn tìm
     * @return List chứa User nếu tìm thấy, ngược lại là List rỗng
     */
    List<User> findAllByUsernameOrEmail(String username, String email);

    /**
     * Check xem có Username có tồn tại trong DB hay không
     *
     * @param username username cần check
     * @return true nếu tồn tại, ngược lại là false
     */
    boolean existsByUsername(String username);

    /**
     * Check xem Email có tồn tại trong DB hay không
     *
     * @param email Email cần check
     * @return true nếu tồn tại, ngược lại là false
     */
    boolean existsByEmail(String email);

    /**
     * Check xem PhoneNumber có tòn tại trong DB hay không
     * @param phoneNumber PhoneNumber cần check
     * @return true nếu tồn tại, ngược lại là false
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
