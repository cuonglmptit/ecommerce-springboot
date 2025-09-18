package com.cuonglm.ecommerce.backend.category.enums;

/**
 * FilterType – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 29 July 2025
 */
// Enums
public enum FilterType {
    CHECKBOX,      // Multiple selection với checkbox
    RADIO,         // Single selection với radio button
    DROPDOWN,      // Dropdown select
    RANGE,         // Range slider (cho price, size number...)
    COLOR_PICKER,  // Color picker cho màu sắc
    SIZE_CHART     // Size chart đặc biệt
}
