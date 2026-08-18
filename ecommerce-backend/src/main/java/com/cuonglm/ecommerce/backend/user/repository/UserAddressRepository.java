package com.cuonglm.ecommerce.backend.user.repository;

import com.cuonglm.ecommerce.backend.user.dto.internal.UserAddressInfoView;
import com.cuonglm.ecommerce.backend.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserAddressRepository – Repository quản lý Sổ địa chỉ {@link UserAddress}.
 *
 * @author cuonglmptit
 * @since Tuesday, 18 August 2026
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    // 1. Lấy danh sách địa chỉ qua Projection (Ưu tiên địa chỉ Default lên đầu)
    List<UserAddressInfoView> findAllByUserIdOrderByIsDefaultDesc(Long userId);

    // 2. Tìm địa chỉ theo ID và bắt buộc thuộc sở hữu của User (Bảo mật chống IDOR)
    Optional<UserAddress> findByIdAndUserId(Long id, Long userId);

    // 3. Tìm địa chỉ mới nhất còn lại của User (Dùng khi vừa xóa địa chỉ mặc định)
    Optional<UserAddress> findFirstByUserIdOrderByAuditCreatedAtDesc(Long userId);

    // 4. Kiểm tra user đã có địa chỉ nào chưa
    boolean existsByUserId(Long userId);

    // 5. Bỏ cờ mặc định của tất cả các địa chỉ cũ của User trong đúng 1 câu UPDATE
    @Modifying
    @Query("UPDATE UserAddress ua SET ua.isDefault = false WHERE ua.user.id = :userId")
    void resetDefaultAddressByUserId(@Param("userId") Long userId);
}