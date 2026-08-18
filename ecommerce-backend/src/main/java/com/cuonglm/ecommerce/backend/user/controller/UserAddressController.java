package com.cuonglm.ecommerce.backend.user.controller;

import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.user.dto.external.CreateUserAddressRequest;
import com.cuonglm.ecommerce.backend.user.dto.external.UpdateUserAddressRequest;
import com.cuonglm.ecommerce.backend.user.dto.external.UserAddressResponse;
import com.cuonglm.ecommerce.backend.user.service.UserAddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserAddressController – Sub controller của user thao tác với địa chỉ
 *
 * @author cuonglmptit
 * @since Tuesday, 18 August 2026
 */
@RestController
@RequestMapping("/api/v1/users/me/addresses")
public class UserAddressController {

    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserAddressResponse>>> getMyAddresses() {
        List<UserAddressResponse> addresses = userAddressService.getCurrentUserAddresses();
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserAddressResponse>> createAddress(
            @Valid @RequestBody CreateUserAddressRequest request
    ) {
        UserAddressResponse created = userAddressService.createAddress(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm địa chỉ thành công", created));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<UserAddressResponse>> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody UpdateUserAddressRequest request
    ) {
        UserAddressResponse updated = userAddressService.updateAddress(addressId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật địa chỉ thành công", updated));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long addressId
    ) {
        userAddressService.deleteAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success("Xóa địa chỉ thành công", null));
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(
            @PathVariable Long addressId
    ) {
        userAddressService.setDefaultAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success("Đặt địa chỉ mặc định thành công", null));
    }
}