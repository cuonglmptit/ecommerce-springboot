package com.cuonglm.ecommerce.backend.product.controller;

import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.product.dto.external.CreateProductRequest;
import com.cuonglm.ecommerce.backend.product.dto.external.ProductResponse;
import com.cuonglm.ecommerce.backend.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProductController – Controller cho Product.
 *
 * @author cuonglmptit
 * @since Wednesday, 23 July 2025
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse productResponse = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(productResponse));
    }
}
