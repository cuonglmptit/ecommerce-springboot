package com.cuonglm.ecommerce.backend.shop.controller;

import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.shop.dto.external.ShopCreateRequestDTO;
import com.cuonglm.ecommerce.backend.shop.dto.external.ShopCreateResponseDTO;
import com.cuonglm.ecommerce.backend.shop.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ShopController – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
@RestController
@RequestMapping("/api/v1/shops")
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShopCreateResponseDTO>> createShop(@Valid @RequestBody ShopCreateRequestDTO request) {
        // 1. Gọi Service để lấy Data DTO
        ShopCreateResponseDTO data = shopService.createShop(request);

        // 2. Wrap Data vào ApiResponse
        ApiResponse<ShopCreateResponseDTO> responseBody = ApiResponse.success(
                "Tạo cửa hàng thành công", // User Message
                data
        );
        // 3. Trả về kèm HTTP Status 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

}
