package com.cuonglm.ecommerce.backend.product.service;

import com.cuonglm.ecommerce.backend.product.dto.external.ProductCreateRequestDTO;
import com.cuonglm.ecommerce.backend.product.dto.external.ProductCreateResponseDTO;

/**
 * ProductService – Interface mô tả các phương thức thao tác với Product.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
public interface ProductService {
    ProductCreateResponseDTO createProduct(ProductCreateRequestDTO request);
}
