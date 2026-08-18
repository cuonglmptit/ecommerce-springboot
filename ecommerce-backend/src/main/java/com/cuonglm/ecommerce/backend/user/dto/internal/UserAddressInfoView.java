package com.cuonglm.ecommerce.backend.user.dto.internal;

import com.cuonglm.ecommerce.backend.location.entity.snapshot.AddressSnapshot;
import com.cuonglm.ecommerce.backend.user.enums.UserAddressType;

/**
 * UserAddressInfoView – Interface Projection đọc dữ liệu Sổ địa chỉ của User.
 *
 * @author cuonglmptit
 * @since Tuesday, 18 August 2026
 */
public interface UserAddressInfoView {
    Long getId();
    String getFullName();
    String getPhoneNumber();
    boolean getIsDefault();
    UserAddressType getType();
    String getNote();
    AddressSnapshot getAddress();
}