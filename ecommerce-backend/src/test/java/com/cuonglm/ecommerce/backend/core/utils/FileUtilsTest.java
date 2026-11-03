package com.cuonglm.ecommerce.backend.core.utils;

/**
 * FileUtilsTest – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 03 December 2025
 */
class FileUtilsTest {
    public static void main(String[] args) {
        System.out.println("--- DEMO CHUẨN HÓA PUBLIC ID ---");
        System.out.println();

        // 1. Trường hợp Tên file tiếng Việt có dấu
        String filename1 = "Áo Thun Cổ Tròn Đẹp.jpg";
        String id1 = FileUtils.generateUniquePublicId(filename1);
        System.out.println("✅ Đầu vào 1: \"" + filename1 + "\"");
        System.out.println("   -> Public ID: " + id1);
        System.out.println();

        // 2. Trường hợp Tên file có ký tự đặc biệt và khoảng trắng dư thừa
        String filename2 = "Sản phẩm ! Giảm giá 50%!.pdf";
        String id2 = FileUtils.generateUniquePublicId(filename2);
        System.out.println("✅ Đầu vào 2: \"" + filename2 + "\"");
        System.out.println("   -> Public ID: " + id2);
        System.out.println();

        // 3. Trường hợp Tên file dài (để kiểm tra cắt chuỗi)
        String filename3 = "Day-la-mot-chuoi-ten-file-rat-dai-va-co-nhieu-tu-de-kiem-tra-logic-cat-chuoi-trong-ham-to-slug.png";
        String id3 = FileUtils.generateUniquePublicId(filename3);
        System.out.println("✅ Đầu vào 3: \"" + filename3 + "\"");
        System.out.println("   -> Public ID (Đã cắt): " + id3);
        System.out.println("   -> Độ dài Slug: " + id3.substring(0, id3.lastIndexOf('_')).length());
        System.out.println();

        // 4. Trường hợp Tên file không có đuôi mở rộng
        String filename4 = "ten-file-khong-extension";
        String id4 = FileUtils.generateUniquePublicId(filename4);
        System.out.println("✅ Đầu vào 4: \"" + filename4 + "\"");
        System.out.println("   -> Public ID: " + id4);
        System.out.println();
    }
}