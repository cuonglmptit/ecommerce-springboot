package com.cuonglm.ecommerce.backend.core.utils;

import java.text.Normalizer;

/**
 * NamingUtils – Chuẩn hóa các đầu vào gì đó.
 *
 * @author cuonglmptit
 * @since Wednesday, 03 December 2025
 */
public class NamingUtils {

    /**
     * Chuyển chuỗi thành dạng CONSTANT_NAME (UPPER_CASE + "_")
     *
     * @param input String đầu vào VD: "mÀu sắc"
     * @return Kết quả được chuẩn hóa: "MAU_SAC"
     */
    public static String toConstantName(String input) {
        if (input == null || input.isEmpty()) return "";

        // 1. Chuẩn hóa Unicode và bỏ dấu (\p{M} = Unicode Category M = Combining Marks)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // 2. Bỏ khoảng trắng đầu cuối
        String trimmed = normalized.trim();

        // 3. Thay nhiều khoảng trắng -> "_"
        String underscored = trimmed.replaceAll("\\s+", "_");

        // 4. Loại bỏ ký tự không hợp lệ
        String cleaned = underscored.replaceAll("[^A-Za-z0-9_]", "");

        // 5. Uppercase
        return cleaned.toUpperCase();
    }
}