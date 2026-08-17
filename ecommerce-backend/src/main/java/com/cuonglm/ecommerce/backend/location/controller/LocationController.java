package com.cuonglm.ecommerce.backend.location.controller;

import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.location.dto.external.AddressRequest;
import com.cuonglm.ecommerce.backend.location.dto.external.DistanceCalculationRequest;
import com.cuonglm.ecommerce.backend.location.dto.external.DistanceCalculationResponse;
import com.cuonglm.ecommerce.backend.location.dto.internal.DistrictInfoDTO;
import com.cuonglm.ecommerce.backend.location.dto.internal.ProvinceInfoDTO;
import com.cuonglm.ecommerce.backend.location.dto.internal.WardInfoDTO;
import com.cuonglm.ecommerce.backend.location.entity.AddressSnapshot;
import com.cuonglm.ecommerce.backend.location.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LocationController – Cung cấp API địa chính 3 cấp, thẩm định địa chỉ và tính khoảng cách giao hàng.
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * 1. Lấy danh sách 63 Tỉnh/Thành phố (Dropdown cấp 1)
     */
    @GetMapping("/provinces")
    public ResponseEntity<ApiResponse<List<ProvinceInfoDTO>>> getAllProvinces() {
        return ResponseEntity.ok(ApiResponse.success(locationService.getAllProvinces()));
    }

    /**
     * 2. Lấy danh sách Quận/Huyện theo Tỉnh (Dropdown cấp 2)
     */
    @GetMapping("/provinces/{provinceId}/districts")
    public ResponseEntity<ApiResponse<List<DistrictInfoDTO>>> getDistrictsByProvince(
            @PathVariable Integer provinceId
    ) {
        return ResponseEntity.ok(ApiResponse.success(locationService.getDistrictsByProvinceId(provinceId)));
    }

    /**
     * 3. Lấy danh sách Phường/Xã theo Quận/Huyện (Dropdown cấp 3)
     */
    @GetMapping("/districts/{districtId}/wards")
    public ResponseEntity<ApiResponse<List<WardInfoDTO>>> getWardsByDistrict(
            @PathVariable Integer districtId
    ) {
        return ResponseEntity.ok(ApiResponse.success(locationService.getWardsByDistrictId(districtId)));
    }

    /**
     * 4. API Thẩm định & Preview địa chỉ (Kiểm tra xem Tỉnh-Huyện-Xã có khớp nhau không và trả về chuỗi Full)
     */
    @PostMapping("/validate-address")
    public ResponseEntity<ApiResponse<AddressSnapshot>> validateAddress(
            @Valid @RequestBody AddressRequest request
    ) {
        AddressSnapshot snapshot = locationService.validateAndBuildSnapshot(request);
        return ResponseEntity.ok(ApiResponse.success("Địa chỉ hợp lệ", snapshot));
    }

    /**
     * 5. API Tính khoảng cách (Km) giao hàng giữa 2 tọa độ GPS (Dành cho ship hỏa tốc)
     */
    @PostMapping("/calculate-distance")
    public ResponseEntity<ApiResponse<DistanceCalculationResponse>> calculateDistance(
            @Valid @RequestBody DistanceCalculationRequest request
    ) {
        DistanceCalculationResponse response = locationService.calculateDistance(
                request.originLat(), request.originLng(),
                request.destinationLat(), request.destinationLng()
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}