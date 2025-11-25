package com.cuonglm.ecommerce.backend.core.utils;

import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * FileUtils – Utils dùng cho xử lý files.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
public class FileUtils {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    /**
     * Tạo Public ID chuẩn hóa từ tên file gốc.
     * Ví dụ: "Áo Thun Đẹp.jpg" -> "ao-thun-dep_550e8400"
     */
    public static String generateUniquePublicId(String originalFilename) {
        String baseName = getBaseName(originalFilename);
        String slug = toSlug(baseName);

        // Cắt ngắn nếu tên quá dài
        if (slug.length() > 50) {
            slug = slug.substring(0, 50);
        }

        // Thêm UUID ngắn (hoặc full UUID) để đảm bảo duy nhất
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);

        return slug + "_" + uniqueSuffix;
    }

    // Chuyển Tiếng Việt có dấu -> không dấu, space -> gạch ngang
    private static String toSlug(String input) {
        if (input == null) return "";
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase();
    }

    // Lấy tên file bỏ đuôi mở rộng (extension)
    private static String getBaseName(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
    }
}
