package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.CategoryRequest;
import com.enigma.tokonyadia_api.dto.response.CategoryResponse;
import com.enigma.tokonyadia_api.entity.Category;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);
    CategoryResponse updateById(String id, CategoryRequest request);
    Category getById(String id);

    List<CategoryResponse> getAll();

    CategoryResponse getOne(String id);

    void removeById(String id);
}
