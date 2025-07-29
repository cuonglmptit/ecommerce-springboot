package com.cuonglm.ecommerce.backend.attribute.enums;

/**
 * AttributeScope – Lớp định nghĩa các thứ liên quan đến attribute là của shop, category, riêng product.
 *
 * <p>
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
