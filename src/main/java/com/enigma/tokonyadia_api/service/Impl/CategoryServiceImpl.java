package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.dto.request.CategoryRequest;
import com.enigma.tokonyadia_api.dto.response.CategoryResponse;
import com.enigma.tokonyadia_api.dto.response.ProductInStoreResponse;
import com.enigma.tokonyadia_api.entity.Category;
import com.enigma.tokonyadia_api.repository.CategoryRepository;
import com.enigma.tokonyadia_api.service.CategoryService;
import com.enigma.tokonyadia_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CategoryResponse create(CategoryRequest request) {
        validationUtil.validate(request);

        Category category = Category.builder()
                .name(request.getName())
                .products(new ArrayList<>())
                .build();

        categoryRepository.saveAndFlush(category);
        return toCategoryResponse(category);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CategoryResponse updateById(String id, CategoryRequest request) {
        validationUtil.validate(request);
        Category category = getById(id);
        category.setName(request.getName());
        categoryRepository.saveAndFlush(category);
        return toCategoryResponse(category);
    }

    @Transactional(readOnly = true)
    @Override
    public Category getById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toCategoryResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryResponse getOne(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        return toCategoryResponse(category);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeById(String id) {
        Category category = getById(id);
        categoryRepository.delete(category);
    }

    private CategoryResponse toCategoryResponse(Category category) {
        List<ProductInStoreResponse> productResponses = category.getProducts().stream()
                .map(product -> new ProductInStoreResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock()
                )).toList();

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .products(productResponses)
                .build();
    }
}
