package com.cuonglm.ecommerce.backend.user.service;

import com.cuonglm.ecommerce.backend.user.dto.external.CreateUserAddressRequest;
import com.cuonglm.ecommerce.backend.user.dto.external.UpdateUserAddressRequest;
import com.cuonglm.ecommerce.backend.user.dto.external.UserAddressResponse;

import java.util.List;

/**
 * UserAddressService – Interface quản lý Sổ địa chỉ người dùng.
 *
 * @author cuonglmptit
 * @since Tuesday, 18 August 2026
 */
public interface UserAddressService {
    List<UserAddressResponse> getCurrentUserAddresses();

    UserAddressResponse createAddress(CreateUserAddressRequest request);

    UserAddressResponse updateAddress(Long addressId, UpdateUserAddressRequest request);

    void deleteAddress(Long addressId);

    void setDefaultAddress(Long addressId);
}