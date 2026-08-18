package com.cuonglm.ecommerce.backend.user.service;

import com.cuonglm.ecommerce.backend.core.exception.ResourceNotFoundException;
import com.cuonglm.ecommerce.backend.core.utils.SecurityUtils;
import com.cuonglm.ecommerce.backend.location.entity.snapshot.AddressSnapshot;
import com.cuonglm.ecommerce.backend.location.service.LocationService;
import com.cuonglm.ecommerce.backend.user.dto.external.CreateUserAddressRequest;
import com.cuonglm.ecommerce.backend.user.dto.external.UpdateUserAddressRequest;
import com.cuonglm.ecommerce.backend.user.dto.external.UserAddressResponse;
import com.cuonglm.ecommerce.backend.user.entity.User;
import com.cuonglm.ecommerce.backend.user.entity.UserAddress;
import com.cuonglm.ecommerce.backend.user.repository.UserAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserAddressServiceImpl – Triển khai logic Sổ địa chỉ người dùng kết hợp LocationService.
 *
 * @author cuonglmptit
 * @since Tuesday, 18 August 2026
 */
@Service
@Transactional
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserService userService;
    private final LocationService locationService;

    public UserAddressServiceImpl(UserAddressRepository userAddressRepository,
                                  UserService userService,
                                  LocationService locationService) {
        this.userAddressRepository = userAddressRepository;
        this.userService = userService;
        this.locationService = locationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getCurrentUserAddresses() {
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();
        return userAddressRepository.findAllByUserIdOrderByIsDefaultDesc(currentUserId)
                .stream()
                .map(UserAddressResponse::fromView)
                .toList();
    }

    @Override
    public UserAddressResponse createAddress(CreateUserAddressRequest request) {
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();
        User userRef = userService.getUserReference(currentUserId);

        // 1. Nhờ LocationService thẩm định và build AddressSnapshot
        AddressSnapshot addressSnapshot = locationService.validateAndBuildSnapshot(request.address());

        // 2. Logic cờ Default: Nếu là địa chỉ đầu tiên hoặc user tick chọn default
        boolean hasAnyAddress = userAddressRepository.existsByUserId(currentUserId);
        boolean isDefault = !hasAnyAddress || request.isDefault();

        if (isDefault && hasAnyAddress) {
            userAddressRepository.resetDefaultAddressByUserId(currentUserId);
        }

        // 3. Lưu UserAddress
        UserAddress userAddress = new UserAddress();
        userAddress.setUser(userRef);
        userAddress.setFullName(request.fullName().trim());
        userAddress.setPhoneNumber(request.phoneNumber().trim());
        userAddress.setDefault(isDefault);
        userAddress.setType(request.type());
        userAddress.setNote(request.note());
        userAddress.setAddress(addressSnapshot);

        UserAddress saved = userAddressRepository.save(userAddress);
        return UserAddressResponse.fromEntity(saved);
    }

    @Override
    public UserAddressResponse updateAddress(Long addressId, UpdateUserAddressRequest request) {
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();
        UserAddress userAddress = userAddressRepository.findByIdAndUserId(addressId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tồn tại hoặc bạn không có quyền sửa."));

        AddressSnapshot updatedSnapshot = locationService.validateAndBuildSnapshot(request.address());

        if (request.isDefault() && !userAddress.isDefault()) {
            userAddressRepository.resetDefaultAddressByUserId(currentUserId);
            userAddress.setDefault(true);
        }

        userAddress.setFullName(request.fullName().trim());
        userAddress.setPhoneNumber(request.phoneNumber().trim());
        userAddress.setType(request.type());
        userAddress.setNote(request.note());
        userAddress.setAddress(updatedSnapshot);

        UserAddress saved = userAddressRepository.save(userAddress);
        return UserAddressResponse.fromEntity(saved);
    }

    @Override
    public void deleteAddress(Long addressId) {
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();
        UserAddress userAddress = userAddressRepository.findByIdAndUserId(addressId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tồn tại hoặc bạn không có quyền xóa."));

        boolean wasDefault = userAddress.isDefault();
        userAddressRepository.delete(userAddress);

        // Nếu vừa xóa địa chỉ mặc định, tự động chuyển cờ default cho địa chỉ mới nhất còn lại
        if (wasDefault) {
            userAddressRepository.findFirstByUserIdOrderByAuditCreatedAtDesc(currentUserId)
                    .ifPresent(nextAddress -> {
                        nextAddress.setDefault(true);
                        userAddressRepository.save(nextAddress);
                    });
        }
    }

    @Override
    public void setDefaultAddress(Long addressId) {
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();
        UserAddress userAddress = userAddressRepository.findByIdAndUserId(addressId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tồn tại."));

        if (!userAddress.isDefault()) {
            userAddressRepository.resetDefaultAddressByUserId(currentUserId);
            userAddress.setDefault(true);
            userAddressRepository.save(userAddress);
        }
    }
}