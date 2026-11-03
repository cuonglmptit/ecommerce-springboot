package com.cuonglm.ecommerce.backend;


import java.nio.charset.Charset;

/**
 * MainTest – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Monday, 27 October 2025
 */
public class MainTest {
    public static void main(String[] args) {
        System.out.println("Chạy thử main test");
        // 1. Kiểm tra mã hóa file/hệ thống
        System.out.println("Mã hóa File Mặc định: " + System.getProperty("file.encoding"));

        // 2. Kiểm tra mã hóa mặc định của JVM
        System.out.println("Charset Mặc định JVM: " + Charset.defaultCharset().displayName());

        // 3. Kiểm tra mã hóa cho Standard Output/Error (Nếu đã cấu hình)
        System.out.println("Mã hóa Stdout (nếu được đặt): " + System.getProperty("stdout.encoding", "Không đặt"));
        System.out.println("Mã hóa Stderr (nếu được đặt): " + System.getProperty("stderr.encoding", "Không đặt"));
    }
}

