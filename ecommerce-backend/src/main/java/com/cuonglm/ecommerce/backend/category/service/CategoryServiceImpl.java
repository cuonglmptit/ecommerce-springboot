package com.cuonglm.ecommerce.backend.category.service;

import com.cuonglm.ecommerce.backend.category.dto.internal.CategoryInfoDTO;
import com.cuonglm.ecommerce.backend.category.entity.Category;
import com.cuonglm.ecommerce.backend.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<CategoryInfoDTO> findCategoryInfoById(Long categoryId) {
        return categoryRepository.findCategoryInfoById(categoryId)
                .map(category -> new CategoryInfoDTO(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getDepth(),
                        category.getSortOrder()
                ));
    }

    @Override
    public Category getCategoryReference(Long categoryId) {
        return categoryRepository.getReferenceById(categoryId);
    }
}
