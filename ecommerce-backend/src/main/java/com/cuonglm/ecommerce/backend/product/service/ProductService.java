package com.cuonglm.ecommerce.backend.product.service;

import com.cuonglm.ecommerce.backend.product.dto.external.CreateProductRequest;
import com.cuonglm.ecommerce.backend.product.dto.external.ProductResponse;

/**
 * ProductService – Interface mô tả các phương thức thao tác với Product.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
}
