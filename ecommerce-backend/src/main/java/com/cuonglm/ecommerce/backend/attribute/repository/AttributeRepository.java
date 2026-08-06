package com.cuonglm.ecommerce.backend.attribute.repository;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoView;
import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AttributeRepository – Repository cho {@link Attribute}.
 *
 * @author cuonglmptit
 * @since Saturday, 29 November 2025
 */
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, UUID> {
    /**
     * Tìm thông tin của một {@link Attribute} thông qua projection bởi {@link AttributeInfoView}
     *
     * @return Một proxy {@link AttributeInfoView} chứa thông tin hoặc {@link Optional#empty()}
     */
    Optional<AttributeInfoView> findAttributeInfoById(UUID uuid);

    /**
     * Kiểm tra Attribute có tồn tại theo Shop ID và Code không
     *
     * @param shopId Id của Shop
     * @param code Code của Attribute
     * @return true - tồn tại, false - nếu ko
     */
    boolean existsByShopIdAndCode(Long shopId, String code);

    Optional<Attribute> findByCode(String code);

    Optional<Attribute> findByShopIdAndCode(Long shopId, String code);

    /**
     * Tìm kiếm và lọc danh sách thuộc tính (Attribute) theo nhiều tiêu chí động.
     * <p>
     * Tất cả các tham số lọc đều là tùy chọn. Nếu truyền {@code null} (hoặc chuỗi rỗng đối với {@code query}),
     * tiêu chí đó sẽ bị bỏ qua. Kết quả được sắp xếp theo tên thuộc tính tăng dần.
     * </p>
     *
     * @param shopId   ID của cửa hàng cần lọc (truyền {@code null} để lấy thuộc tính không phụ thuộc shop)
     * @param scopes   Danh sách phạm vi thuộc tính cần lọc (ví dụ: GLOBAL, SHOP, ...)
     * @param types    Danh sách loại thuộc tính cần lọc (ví dụ: TEXT, NUMBER, SELECT, ...)
     * @param statuses Danh sách trạng thái thuộc tính cần lọc (ví dụ: ACTIVE, INACTIVE, ...)
     * @param query    Từ khóa tìm kiếm theo tên thuộc tính (tìm kiếm tương đối, không phân biệt hoa thường)
     * @return Danh sách các đối tượng {@link AttributeInfoView} thỏa mãn điều kiện, sắp xếp theo tên (A-Z)
     */
    @Query("""
        SELECT a FROM Attribute a
        WHERE (:shopId IS NULL OR a.shop.id = :shopId)
          AND (:scopes IS NULL OR a.scope IN :scopes)
          AND (:types IS NULL OR a.type IN :types)
          AND (:statuses IS NULL OR a.status IN :statuses)
          AND (:query IS NULL OR :query = '' OR LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')))
        ORDER BY a.name ASC
    """)
    List<AttributeInfoView> searchAttributes(
            @Param("shopId") Long shopId,
            @Param("scopes") List<AttributeScope> scopes,
            @Param("types") List<AttributeType> types,
            @Param("statuses") List<AttributeStatus> statuses,
            @Param("query") String query
    );

    /**
     * Lấy tất cả thuộc tính mà một Cửa hàng (Shop) có thể sử dụng.
     * <p>
     * Bao gồm các thuộc tính riêng của Shop đó ({@code a.shop.id = :shopId}) và các thuộc tính
     * dùng chung toàn hệ thống/Global ({@code a.shop IS NULL}). Hỗ trợ lọc thêm theo loại,
     * trạng thái và từ khóa tìm kiếm.
     * Kết quả được sắp xếp ưu tiên theo phạm vi (Scope), sau đó đến tên thuộc tính (A-Z).
     * </p>
     *
     * @param shopId   ID của cửa hàng cần lấy danh sách thuộc tính khả dụng (Bắt buộc)
     * @param types    Danh sách loại thuộc tính cần lọc (truyền {@code null} nếu lấy tất cả)
     * @param statuses Danh sách trạng thái thuộc tính cần lọc (truyền {@code null} nếu lấy tất cả)
     * @param query    Từ khóa tìm kiếm theo tên thuộc tính (tìm kiếm tương đối, không phân biệt hoa thường)
     * @return Danh sách các đối tượng {@link AttributeInfoView} khả dụng cho shop, sắp xếp theo scope và tên
     */
    @Query("""
        SELECT a FROM Attribute a
        WHERE (a.shop.id = :shopId OR a.shop IS NULL)
          AND (:types IS NULL OR a.type IN :types)
          AND (:statuses IS NULL OR a.status IN :statuses)
          AND (:query IS NULL OR :query = '' OR LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')))
        ORDER BY a.scope ASC, a.name ASC
    """)
    List<AttributeInfoView> searchUsableShopAttributes(
            @Param("shopId") Long shopId,
            @Param("types") List<AttributeType> types,
            @Param("statuses") List<AttributeStatus> statuses,
            @Param("query") String query
    );
}
