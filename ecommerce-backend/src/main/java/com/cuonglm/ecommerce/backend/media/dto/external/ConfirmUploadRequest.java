package com.cuonglm.ecommerce.backend.media.dto.external;

import com.cuonglm.ecommerce.backend.media.enums.MediaScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ConfirmUploadRequest – Mo_ta_file
 *
 * @author cuonglmptit
 * @since Sunday, 23 August 2026
 */
public record ConfirmUploadRequest(
        @NotBlank(message = "Checksum SHA-256 không được để trống")
        @Size(min = 64, max = 64)
        String checksum,

        @NotBlank(message = "URL không được để trống")
        String url,

        @NotBlank(message = "External ID không được để trống")
        String externalId,

        @NotBlank(message = "Format không được để trống")
        String format,

        @NotNull(message = "Kích thước không được để trống")
        Long size,

        String originalFilename,
        String alt,
        MediaScope scope
) {
        public MediaScope getResolvedScope() {
                return scope != null ? scope : MediaScope.PUBLIC;
        }
}