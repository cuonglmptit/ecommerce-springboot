package com.cuonglm.ecommerce.backend.product.dto.external;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * CreateProductRequest – Payload nhận yêu cầu tạo mới sản phẩm từ Frontend.
 *
 * @author cuonglmptit
 * @since Saturday, 15 August 2026
 */
public record CreateProductRequest(
        @NotNull(message = "Shop ID không được để trống")
        Long shopId,

        @NotNull(message = "Category ID không được để trống")
        Long categoryId,

        @NotBlank(message = "Tên sản phẩm không được để trống")
        @Size(min = 5, max = 255, message = "Tên sản phẩm phải từ 5 đến 255 ký tự")
        String name,

        @NotBlank(message = "Mô tả sản phẩm không được để trống")
        String description,

        @Valid
        List<SpecInput> specifications,

        @Valid
        @NotEmpty(message = "Ảnh sản phẩm không được để trống")
        @Size(min = 1, max = 7, message = "Số lượng ảnh của sản phẩm phải từ 1 đến 7")
        List<MediaInput> media,

        @Valid
        @NotEmpty(message = "Sản phẩm phải có ít nhất một biến thể")
        List<VariantInput> variants
){
        // 1. DTO con nhận Thông số kỹ thuật (Thương hiệu, Xuất xứ, Chất liệu...)
        public record SpecInput(
                @NotNull(message = "Attribute ID không được để trống")
                UUID attributeId,

                @NotBlank(message = "Tên thuộc tính không được để trống")
                String attributeName,

                String attributeCode,

                UUID optionId,       // Dùng khi chọn từ dropdown

                String optionValue,  // Giá trị dropdown

                String rawValue      // Dùng khi nhập tay tự do (Free-text)
        ) {}

        // 2. DTO con nhận Ảnh/Media đính kèm
        public record MediaInput(
                @NotNull(message = "Media ID không được để trống")
                UUID mediaId,

                @NotBlank(message = "URL không được để trống")
                String url,

                String alt,

                boolean isThumbnail,

                int sortOrder
        ) {}

        // 3. DTO con nhận từng Biến thể (SKU, Giá, Kho, Phân loại)
        public record VariantInput(
                @NotBlank(message = "SKU không được để trống")
                @Size(max = 255, message = "SKU không được quá 255 ký tự")
                String sku,

                @NotNull(message = "Giá không được để trống")
                @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
                BigDecimal price,

                @Min(value = 0, message = "Giá khuyến mãi phải lớn hơn hoặc bằng 0")
                BigDecimal salePrice,

                @NotNull(message = "Số lượng tồn kho không được để trống")
                @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
                Integer stockQuantity,

                @Valid
                List<VariantAttrInput> attributes,

                @Valid
                @Size(max = 3, message = "Một biến thể chỉ được có tối đa 3 ảnh")
                List<MediaInput> media
        ) {}

        // 4. DTO con nhận Thuộc tính phân loại của biến thể (Màu sắc, Size)
        public record VariantAttrInput(
                @NotNull(message = "Attribute ID không được để trống")
                UUID attributeId,

                @NotBlank(message = "Tên thuộc tính không được để trống")
                String attributeName,

                UUID optionId,

                @NotBlank(message = "Giá trị thuộc tính không được để trống")
                String optionValue
        ) {}
}
