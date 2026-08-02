package com.cuonglm.ecommerce.backend.product.controller;

import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.product.dto.external.ProductCreateRequestDTO;
import com.cuonglm.ecommerce.backend.product.dto.external.ProductCreateResponseDTO;
import com.cuonglm.ecommerce.backend.product.service.ProductService;
import com.cuonglm.ecommerce.backend.shop.dto.external.ShopCreateResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<ApiResponse<ProductCreateResponseDTO>> createProduct(@Valid @RequestBody ProductCreateRequestDTO request) {
        ProductCreateResponseDTO productResponse = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(productResponse));
    }
}
