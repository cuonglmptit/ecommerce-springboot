package com.cuonglm.ecommerce.backend.core.pagination;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * PageResponse – DTO chuẩn hóa kết quả phân trang trả về cho Client.
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        if (page == null) return null;
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public static <T, R> PageResponse<R> of(Page<T> page, List<R> mappedContent) {
        if (page == null) return null;
        return new PageResponse<>(
                mappedContent != null ? mappedContent : List.of(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}