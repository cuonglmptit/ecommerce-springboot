package com.cuonglm.ecommerce.backend.core.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AddressUtils – Tiện ích xử lý và chuẩn hóa chuỗi địa chỉ giao hàng.
 *
 * @author cuonglmptit
 * @since Monday, 17 August 2026
 */
public final class AddressUtils {
    /**
     * Ghép các thành phần địa chỉ thành 1 chuỗi hoàn chỉnh chuẩn hóa.
     * Tự động loại bỏ null, khoảng trắng thừa và không bao giờ bị lỗi dấu phẩy lặp ", , ".
     * Ví dụ: ("123 Yên Phụ", "Phúc Xá", "Ba Đình", "Hà Nội")
     *     -> "123 Yên Phụ, Phúc Xá, Ba Đình, Hà Nội"
     */
    public static String formatFullAddress(String addressLine, String wardName, String districtName, String provinceName) {
        return Arrays.stream(new String[]{addressLine, wardName, districtName, provinceName})
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));
    }
}