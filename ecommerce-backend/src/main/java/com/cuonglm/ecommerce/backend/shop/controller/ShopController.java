package com.cuonglm.ecommerce.backend.shop.controller;

import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.shop.dto.external.*;
import com.cuonglm.ecommerce.backend.shop.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ShopController – Controller xử lý các API liên quan đến Cửa hàng (Shop).
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

    /**
     * 1. Đăng ký mở Cửa hàng mới (Dành cho Seller)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ShopResponse>> createShop(
            @Valid @RequestBody CreateShopRequest request
    ) {
        ShopResponse data = shopService.createShop(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo cửa hàng thành công", data));
    }

    /**
     * 2. Lấy thông tin Cửa hàng của Seller đang đăng nhập (/me)
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ShopResponse>> getMyShop() {
        ShopResponse data = shopService.getCurrentShop();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 3. Cập nhật thông tin Cửa hàng của Seller đang đăng nhập (/me)
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ShopResponse>> updateMyShop(
            @Valid @RequestBody UpdateShopRequest request
    ) {
        ShopResponse data = shopService.updateCurrentShop(request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin cửa hàng thành công", data));
    }

    /**
     * 4. Xem thông tin Cửa hàng công khai (Dành cho Khách hàng trên sàn)
     */
    @GetMapping("/{shopId}")
    public ResponseEntity<ApiResponse<ShopResponse>> getShopById(
            @PathVariable Long shopId
    ) {
        ShopResponse data = shopService.getShopById(shopId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}