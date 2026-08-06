package com.cuonglm.ecommerce.backend.category.service;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.category.dto.internal.CategoryAttributeInfoView;
import com.cuonglm.ecommerce.backend.category.dto.internal.CategoryInfoDTO;
import com.cuonglm.ecommerce.backend.category.dto.internal.CategoryInfoView;
import com.cuonglm.ecommerce.backend.category.entity.Category;
import com.cuonglm.ecommerce.backend.category.repository.CategoryAttributeRepository;
import com.cuonglm.ecommerce.backend.category.repository.CategoryRepository;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public List<CategoryInfoView> getRootCategories() {
        return categoryRepository.findRootCategories(BasicStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfoView> getChildrenCategories(Long parentId) {
        return categoryRepository.findChildrenCategories(parentId, BasicStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfoView> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return categoryRepository.searchCategories(
                keyword.trim(),
                BasicStatus.ACTIVE
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryAttributeInfoView> getCategoryAttributes(Long categoryId, AttributeType type) {
        return categoryAttributeRepository.findCategoryAttributes(
                categoryId,
                AttributeStatus.ACTIVE,
                type
        );
    }
}
