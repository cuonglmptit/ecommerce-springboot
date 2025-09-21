package com.cuonglm.ecommerce.backend.shop.service;

import com.cuonglm.ecommerce.backend.shop.dto.external.ShopCreateRequestDTO;
import com.cuonglm.ecommerce.backend.shop.dto.external.ShopCreateResponseDTO;

/**
 * ShopService – Định nghĩa các phương thức liên quan đến Shop.
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
public interface ShopService {
    ShopCreateResponseDTO createShop(ShopCreateRequestDTO request);
}
