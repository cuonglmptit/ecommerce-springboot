package com.cuonglm.ecommerce.backend.attribute.controller;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.attribute.service.AttributeService;
import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AttributeController – Controller của Attribute.
 *
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
     * API Autocomplete danh sách Thuộc tính KHẢ DỤNG dành riêng cho Seller Form (Shop + Global)
     * Ví dụ: GET /api/v1/attributes/search?shopId=2&query=Màu&type=VARIATION
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AttributeInfoDTO>>> searchAttributes(
            @RequestParam Long shopId,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(required = false) AttributeType type
    ) {
        List<AttributeInfoDTO> results = attributeService.searchUsableShopAttributes(shopId, query, type);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * API Tìm kiếm & Lọc thuộc tính nâng cao dành cho Admin (Hỗ trợ Multi-select)
     * Ví dụ: GET /api/v1/attributes/admin/search?scopes=GLOBAL,SHOP&types=SPECIFICATION&statuses=ACTIVE
     */
    @GetMapping("/admin/search")
    public ResponseEntity<ApiResponse<List<AttributeInfoDTO>>> searchAttributesAdmin(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) List<AttributeScope> scopes,
            @RequestParam(required = false) List<AttributeType> types,
            @RequestParam(required = false) List<AttributeStatus> statuses,
            @RequestParam(defaultValue = "") String query
    ) {
        List<AttributeInfoDTO> results = attributeService.searchAttributes(shopId, scopes, types, statuses, query);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * API Tìm kiếm Giá trị Phân loại (Options) theo Đúng Ngữ Cảnh Tên Thuộc tính
     * Ví dụ: GET /api/v1/attributes/options/search?shopId=2&attributeName=Màu sắc&query=Đỏ&type=VARIATION
     */
    @GetMapping("/options/search")
    public ResponseEntity<ApiResponse<List<AttributeOptionInfoDTO>>> searchOptions(
            @RequestParam Long shopId,
            @RequestParam String attributeName,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(required = false) AttributeType type
    ) {
        List<AttributeOptionInfoDTO> results = attributeService.searchOptionsByAttributeName(shopId, attributeName, query, type);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}