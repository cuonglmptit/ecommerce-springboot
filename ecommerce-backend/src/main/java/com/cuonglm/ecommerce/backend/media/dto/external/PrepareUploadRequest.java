package com.cuonglm.ecommerce.backend.media.dto.external;

import com.cuonglm.ecommerce.backend.media.enums.MediaScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * PrepareUploadRequest – Check xem trước khi upload có trùng chưa
 *
 * @author cuonglmptit
 * @since Sunday, 23 August 2026
 */

public record PrepareUploadRequest(
        @NotBlank(message = "Checksum SHA-256 không được để trống")
        @Size(min = 64, max = 64, message = "Checksum SHA-256 phải đúng 64 ký tự")
        String checksum,

        @NotBlank(message = "Tên file không được để trống")
        String fileName,

        @NotNull(message = "Kích thước file không được để trống")
        Long fileSize,

        @NotBlank(message = "MIME Type không được để trống")
        String contentType,

        MediaScope scope // Mặc định là PUBLIC nếu không truyền
) {
        public MediaScope getResolvedScope() {
                return scope != null ? scope : MediaScope.PUBLIC;
        }
}
