package com.cuonglm.ecommerce.backend.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * MoneyUtils – Tiện ích tính toán tiền tệ chính xác tuyệt đối, chống sai số dấu phẩy động.
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
public final class MoneyUtils {
    private MoneyUtils() {
    }

    /**
     * Tính giá sau khi giảm phần trăm (vd: 100.000 VNĐ giảm 20% -> 80.000 VNĐ).
     */
    public static BigDecimal applyDiscountPercent(BigDecimal originalPrice, BigDecimal discountPercent) {
        if (originalPrice == null || discountPercent == null) return originalPrice;
        if (discountPercent.compareTo(BigDecimal.ZERO) <= 0) return originalPrice;

        BigDecimal discountMultiplier = BigDecimal.valueOf(100).subtract(discountPercent);
        return originalPrice.multiply(discountMultiplier)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Làm tròn tiền VNĐ chuẩn (làm tròn số nguyên không có số thập phân).
     */
    public static BigDecimal roundVND(BigDecimal amount) {
        if (amount == null) return BigDecimal.ZERO;
        return amount.setScale(0, RoundingMode.HALF_UP);
    }
}