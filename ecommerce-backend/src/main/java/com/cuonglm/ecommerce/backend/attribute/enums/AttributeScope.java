package com.cuonglm.ecommerce.backend.attribute.enums;

/**
 * AttributeScope – Lớp định nghĩa loại Attribute.
 *
 * <p>
 * - GLOBAL: Dùng chung toàn sàn (Admin tạo).
 * <br>
 * - SHOP: Thuộc tính riêng của từng Shop.
 * Nếu scope là cho GLOBAL thì sẽ đc hiển thị/đề xuất khi tạo product, search,...
 * <br>
 * Nếu scope là SHOP thì sẽ là template cho shop hoặc chỉ search filter đc trong shop
 * </p>
 *
 * @author cuonglmptit
 * @since Saturday, 26 July 2025
 */
public enum AttributeScope {
    GLOBAL, SHOP
}
