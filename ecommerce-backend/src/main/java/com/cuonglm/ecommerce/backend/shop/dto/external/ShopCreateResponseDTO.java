package com.cuonglm.ecommerce.backend.shop.dto.external;

import com.cuonglm.ecommerce.backend.shop.entity.ShopAddress;
import com.cuonglm.ecommerce.backend.shop.enums.ShopStatus;

/**
 * ShopCreationResultDTO – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
public class ShopCreateResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long ownerId; // ID của User sở hữu
    private ShopAddress address;
    private ShopStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public ShopAddress getAddress() {
        return address;
    }

    public void setAddress(ShopAddress address) {
        this.address = address;
    }

    public ShopStatus getStatus() {
        return status;
    }

    public void setStatus(ShopStatus status) {
        this.status = status;
    }
}
