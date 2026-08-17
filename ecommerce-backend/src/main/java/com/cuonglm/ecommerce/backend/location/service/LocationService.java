package com.cuonglm.ecommerce.backend.location.service;

import com.cuonglm.ecommerce.backend.location.dto.external.AddressRequest;
import com.cuonglm.ecommerce.backend.location.dto.external.DistanceCalculationResponse;
import com.cuonglm.ecommerce.backend.location.dto.internal.DistrictInfoDTO;
import com.cuonglm.ecommerce.backend.location.dto.internal.ProvinceInfoDTO;
import com.cuonglm.ecommerce.backend.location.dto.internal.WardInfoDTO;
import com.cuonglm.ecommerce.backend.location.entity.AddressSnapshot;

import java.math.BigDecimal;
import java.util.List;

/**
 * LocationService – Interface định nghĩa các method của LocationService.
 *
 * <p>
 * Bao gồm các phương thức để thao tác với dữ liệu về vị trí, địa chỉ.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 25 July 2025
 */
public interface LocationService {
    /**
     * Thẩm định phân cấp Tỉnh/Huyện/Xã và đóng gói thành AddressSnapshot (JSONB) cho User, Shop, Order.
     */
    AddressSnapshot validateAndBuildSnapshot(AddressRequest request);

    /**
     * Tính toán khoảng cách địa lý (Km) giữa 2 tọa độ GPS (Công thức Haversine).
     */
    DistanceCalculationResponse calculateDistance(BigDecimal originLat, BigDecimal originLng,
                                                  BigDecimal destLat, BigDecimal destLng);

    /**
     * Lấy danh sách tất cả Tỉnh/Thành phố phục vụ Dropdown tầng 1.
     */
    List<ProvinceInfoDTO> getAllProvinces();

    /**
     * Lấy danh sách Quận/Huyện theo Tỉnh phục vụ Dropdown tầng 2.
     */
    List<DistrictInfoDTO> getDistrictsByProvinceId(Integer provinceId);

    /**
     * Lấy danh sách Phường/Xã theo Huyện phục vụ Dropdown tầng 3.
     */
    List<WardInfoDTO> getWardsByDistrictId(Integer districtId);
}