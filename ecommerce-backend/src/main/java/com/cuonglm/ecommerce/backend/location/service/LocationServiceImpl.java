package com.cuonglm.ecommerce.backend.location.service;

import com.cuonglm.ecommerce.backend.core.exception.InvalidLocationDataException;
import com.cuonglm.ecommerce.backend.location.dto.external.AddressRequest;
import com.cuonglm.ecommerce.backend.location.dto.external.DistanceCalculationResponse;
import com.cuonglm.ecommerce.backend.location.dto.internal.DistrictInfoDTO;
import com.cuonglm.ecommerce.backend.location.dto.internal.ProvinceInfoDTO;
import com.cuonglm.ecommerce.backend.location.dto.internal.WardInfoDTO;
import com.cuonglm.ecommerce.backend.location.entity.AddressSnapshot;
import com.cuonglm.ecommerce.backend.location.entity.District;
import com.cuonglm.ecommerce.backend.location.entity.Province;
import com.cuonglm.ecommerce.backend.location.entity.Ward;
import com.cuonglm.ecommerce.backend.location.repository.DistrictRepository;
import com.cuonglm.ecommerce.backend.location.repository.ProvinceRepository;
import com.cuonglm.ecommerce.backend.location.repository.WardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * LocationServiceImpl – Triển khai logic nghiệp vụ Location. *
 *
 * @author cuonglmptit
 * @since Tuesday, 18 November 2025
 */
@Service
@Transactional
public class LocationServiceImpl implements LocationService {
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    public LocationServiceImpl(ProvinceRepository provinceRepository,
                               DistrictRepository districtRepository,
                               WardRepository wardRepository) {
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AddressSnapshot validateAndBuildSnapshot(AddressRequest request) {
        if (request.latitude() != null && request.longitude() != null) {
            if (request.placeId() == null || request.placeId().isBlank()) {
                throw new InvalidLocationDataException("Nếu cung cấp tọa độ địa lý, Place ID là bắt buộc.");
            }
        }

        // Validate 3 cấp hành chính trong ĐÚNG 1 query JOIN FETCH duy nhất
        Ward ward = wardRepository.findByIdAndHierarchy(
                request.wardId(),
                request.districtId(),
                request.provinceId()
        ).orElseThrow(() -> new InvalidLocationDataException(
                String.format("Dữ liệu địa chỉ không hợp lệ: Phường/Xã ID '%d' không thuộc Quận/Huyện ID '%d' hoặc Tỉnh/Thành ID '%d'.",
                        request.wardId(), request.districtId(), request.provinceId())));

        District district = ward.getDistrict();
        Province province = district.getProvince();

        // Đóng gói AddressSnapshot bất biến
        return AddressSnapshot.of(
                province.getId(), province.getName(),
                district.getId(), district.getName(),
                ward.getId(), ward.getName(),
                request.addressLine().trim(),
                request.latitude(), request.longitude(),
                request.placeId(), request.formattedAddress()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DistanceCalculationResponse calculateDistance(BigDecimal originLat, BigDecimal originLng,
                                                         BigDecimal destLat, BigDecimal destLng) {
        if (originLat == null || originLng == null || destLat == null || destLng == null) {
            throw new InvalidLocationDataException("Tọa độ xuất phát và tọa độ đích không được để trống.");
        }

        // Thuật toán Haversine tính khoảng cách giữa 2 điểm GPS trên mặt cầu Trái Đất (0ms trễ, không tốn tiền API)
        double lat1 = Math.toRadians(originLat.doubleValue());
        double lon1 = Math.toRadians(originLng.doubleValue());
        double lat2 = Math.toRadians(destLat.doubleValue());
        double lon2 = Math.toRadians(destLng.doubleValue());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceKm = EARTH_RADIUS_KM * c;
        return DistanceCalculationResponse.of(distanceKm);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProvinceInfoDTO> getAllProvinces() {
        return provinceRepository.findAllByOrderByNameAsc()
                .stream()
                .map(ProvinceInfoDTO::fromView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictInfoDTO> getDistrictsByProvinceId(Integer provinceId) {
        if (provinceId == null) return List.of();
        return districtRepository.findByProvinceIdOrderByNameAsc(provinceId)
                .stream()
                .map(DistrictInfoDTO::fromView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WardInfoDTO> getWardsByDistrictId(Integer districtId) {
        if (districtId == null) return List.of();
        return wardRepository.findByDistrictIdOrderByNameAsc(districtId)
                .stream()
                .map(WardInfoDTO::fromView)
                .toList();
    }
}
