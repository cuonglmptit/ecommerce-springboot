package com.cuonglm.ecommerce.backend.category.service;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.category.dto.internal.*;
import com.cuonglm.ecommerce.backend.category.entity.Category;
import com.cuonglm.ecommerce.backend.category.repository.CategoryAttributeRepository;
import com.cuonglm.ecommerce.backend.category.repository.CategoryRepository;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CategoryServiceImpl – Triển khai logic cho {@link CategoryService}.
 *
 * @author cuonglmptit
 * @since Friday, 28 November 2025
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryAttributeRepository categoryAttributeRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryAttributeRepository = categoryAttributeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryInfoDTO> findCategoryInfoById(Long categoryId) {
        return categoryRepository.findCategoryInfoById(categoryId)
                .map(CategoryInfoDTO::fromView);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryReference(Long categoryId) {
        return categoryRepository.getReferenceById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfoDTO> getRootCategories() {
        return categoryRepository.findRootCategories(BasicStatus.ACTIVE)
                .stream()
                .map(CategoryInfoDTO::fromView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfoDTO> getChildrenCategories(Long parentId) {
        return categoryRepository.findChildrenCategories(parentId, BasicStatus.ACTIVE)
                .stream()
                .map(CategoryInfoDTO::fromView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfoDTO> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return categoryRepository.searchCategories(keyword.trim(), BasicStatus.ACTIVE)
                .stream()
                .map(CategoryInfoDTO::fromView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryAttributeInfoDTO> getCategoryAttributes(Long categoryId, AttributeType type) {
        return categoryAttributeRepository.findCategoryAttributes(
                categoryId,
                AttributeStatus.ACTIVE,
                type
        )
                .stream()
                .map(CategoryAttributeInfoDTO::fromView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryTreeNodeDTO> getCategoryTree() {
        // 1. Chỉ phát sinh 1 câu SQL duy nhất
        List<CategoryInfoView> flatCategories = categoryRepository.findAllActiveCategories(BasicStatus.ACTIVE);

        // 2. Nhóm danh mục con theo parent.id an toàn
        Map<Long, List<CategoryInfoView>> childrenMap = flatCategories.stream()
                .filter(c -> c.getParent() != null && c.getParent().getId() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        // 3. Lấy danh sách Root nodes
        List<CategoryInfoView> roots = flatCategories.stream()
                .filter(c -> c.getDepth() == 0 || c.getParent() == null)
                .toList();

        // 4. Dựng cây
        return buildTreeNodes(roots, childrenMap);
    }

    private List<CategoryTreeNodeDTO> buildTreeNodes(
            List<CategoryInfoView> currentLevel,
            Map<Long, List<CategoryInfoView>> childrenMap
    ) {
        if (currentLevel == null || currentLevel.isEmpty()) {
            return List.of();
        }

        return currentLevel.stream()
                .map(view -> {
                    List<CategoryInfoView> childViews = childrenMap.getOrDefault(view.getId(), List.of());
                    List<CategoryTreeNodeDTO> childNodes = buildTreeNodes(childViews, childrenMap);
                    boolean hasChildren = !childNodes.isEmpty();

                    // Chuẩn DRY DTO Mapping
                    return CategoryTreeNodeDTO.of(view, hasChildren, childNodes);
                })
                .toList();
    }
}
