package com.cuonglm.ecommerce.backend.attribute.controller;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.service.AttributeService;
import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AttributeController – Mo_ta_file
 *
 * <p>
 * Mo_ta_chi_tiet
 * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 02 August 2026
 */
@RestController
@RequestMapping("/api/v1/attributes")
public class AttributeController {

    private final AttributeService attributeService;

    public AttributeController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    /**
     * API Tìm kiếm Tên Thuộc tính (Autocomplete)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AttributeInfoDTO>>> searchAttributes(
            @RequestParam Long shopId,
            @RequestParam(defaultValue = "") String query
    ) {
        List<AttributeInfoDTO> results = attributeService.searchAttributes(shopId, query);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * API Tìm kiếm Giá trị Phân loại Theo Đúng Ngữ Cảnh Tên Thuộc tính
     */
    @GetMapping("/options/search")
    public ResponseEntity<ApiResponse<List<AttributeOptionInfoDTO>>> searchOptions(
            @RequestParam Long shopId,
            @RequestParam String attributeName,
            @RequestParam(defaultValue = "") String query
    ) {
        List<AttributeOptionInfoDTO> results = attributeService.searchOptionsByAttributeName(shopId, attributeName, query);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}