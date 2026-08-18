package com.cuonglm.ecommerce.backend.user.dto.external;

import com.cuonglm.ecommerce.backend.location.entity.snapshot.AddressSnapshot;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserAddressInfoView;
import com.cuonglm.ecommerce.backend.user.entity.UserAddress;
import com.cuonglm.ecommerce.backend.user.enums.UserAddressType;

/**
 * UserAddressResponse – DTO trả về thông tin địa chỉ trong sổ địa chỉ của User.
 *
 * @author cuonglmptit
 * @since Tuesday, 18 August 2026
 */
public record UserAddressResponse(
        Long id,
        String fullName,
        String phoneNumber,
        boolean isDefault,
        UserAddressType type,
        String note,
        AddressSnapshot address
) {
    public static UserAddressResponse fromEntity(UserAddress address) {
        if (address == null) return null;
        return new UserAddressResponse(
                address.getId(),
                address.getFullName(),
                address.getPhoneNumber(),
                address.isDefault(),
                address.getType(),
                address.getNote(),
                address.getAddress()
        );
    }

    public static UserAddressResponse fromView(UserAddressInfoView view){
        if (view == null) return null;
        return new UserAddressResponse(
                view.getId(),
                view.getFullName(),
                view.getPhoneNumber(),
                view.getIsDefault(),
                view.getType(),
                view.getNote(),
                view.getAddress()
        );
    }
}