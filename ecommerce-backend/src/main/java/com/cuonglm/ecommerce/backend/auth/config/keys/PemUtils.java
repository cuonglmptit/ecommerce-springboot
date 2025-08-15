package com.cuonglm.ecommerce.backend.auth.config.keys;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * PemUtils – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 14 November 2025
 */
@Component
public class PemUtils {

    private final ResourceLoader resourceLoader;

    public PemUtils(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // Phương thức Utility để lấy nội dung Key
    private String getKeyContent(String keySource) throws IOException {
        // 1. Kiểm tra nếu chuỗi là đường dẫn resource/file (classpath: hoặc file:)
        if (keySource.startsWith("classpath:") || keySource.startsWith("file:")) {
            Resource resource = resourceLoader.getResource(keySource);
            // Đọc nội dung file
            try (var inputStream = resource.getInputStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        // 2. Nếu không phải là đường dẫn, giả định đó là nội dung Key trực tiếp từ ENV
        return keySource;
    }

    // Phương thức Utility để làm sạch chuỗi PEM
    private String cleanKeyContent(String content) {
        // Regex để bắt các chuỗi BEGIN/END cho PRIVATE KEY và PUBLIC KEY
        // Sử dụng (?s) để bật chế độ DOTALL, cho phép "." khớp với cả ký tự xuống dòng (cần thiết nếu chuỗi key bị truyền vào với nhiều dòng).

        // Regex chi tiết để bắt các biến thể:
        // PRIVATE KEY: (RSA|DSA|EC|ENCRYPTED)? PRIVATE KEY
        // PUBLIC KEY: PUBLIC KEY (X509)
        final String pemRegex = "(?s)-----BEGIN\\s+(?:RSA|DSA|EC|ENCRYPTED)?\\s*(?:PRIVATE|PUBLIC)\\s+KEY( BLOCK)?-----|-----END\\s+(?:RSA|DSA|EC|ENCRYPTED)?\\s*(?:PRIVATE|PUBLIC)\\s+KEY( BLOCK)?-----";

        // 1. Loại bỏ tất cả header/footer PEM
        String cleaned = content.replaceAll(pemRegex, "");

        // 2. Loại bỏ khoảng trắng, xuống dòng, và ký tự carriage return
        // Đây là bước quan trọng để Base64 decoder chỉ nhận ký tự hợp lệ.
        return cleaned.replaceAll("\\s", "").replaceAll("\\r", "").trim();
    }
    // 2. Parse Public Key
    public RSAPublicKey parsePublicKey(String keySource) {
        try {
            String content = getKeyContent(keySource);
            String cleanedContent = cleanKeyContent(content);

            byte[] decoded = Base64.getDecoder().decode(cleanedContent);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error loading RSA Public Key from source: " + keySource, e);
        }
    }

    // 3. Parse Private Key
    public RSAPrivateKey parsePrivateKey(String keySource) {
        try {
            String content = getKeyContent(keySource);
            String cleanedContent = cleanKeyContent(content);

            byte[] decoded = Base64.getDecoder().decode(cleanedContent);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error loading RSA Private Key from source: " + keySource, e);
        }
    }
}