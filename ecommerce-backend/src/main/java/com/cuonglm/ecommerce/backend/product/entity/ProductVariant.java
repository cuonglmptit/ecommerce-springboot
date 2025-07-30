package com.cuonglm.ecommerce.backend.product.entity;

import com.cuonglm.ecommerce.backend.media.Media;
import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductVariant – Đại diện cho một biến thể cụ thể của sản phẩm (Product).
 *
 * <p>
 * Một sản phẩm (Product) có thể có nhiều biến thể (Variant),
 * được phân biệt bởi các thuộc tính như Màu sắc, Kích thước...
 * </p>
 *
 * <p>
 * Ví dụ: Product = "Áo thun Nam" có thể có:
 * <ul>
 *     <li>Variant 1: Màu Đỏ, Size M</li>
 *     <li>Variant 2: Màu Đen, Size L</li>
 * </ul>
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 22 July 2025
 */
@Entity
@Table(name = "product_variants")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(unique = true)
    private String sku;

    @Column(nullable = false, precision = 19, scale = 2)
    @Min(value = 0, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @Column(precision = 19, scale = 2)
    @Min(value = 0, message = "Giá khuyến mãi phải lớn hơn 0")
    private BigDecimal salePrice;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @OneToMany(mappedBy = "variant")
    private List<ProductVariantAttributeValue> attributeValues = new ArrayList<>();

    @OneToMany(mappedBy = "variant")
    private List<ProductMedia> media;

    private ProductStatus status;
}
