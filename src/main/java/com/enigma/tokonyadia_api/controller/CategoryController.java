package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.request.CategoryRequest;
import com.enigma.tokonyadia_api.dto.response.CategoryResponse;
import com.enigma.tokonyadia_api.dto.response.WebResponse;
import com.enigma.tokonyadia_api.service.CategoryService;
import com.enigma.tokonyadia_api.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/categories")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<WebResponse<CategoryResponse>> createCategory(@RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Created Category", response);
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<CategoryResponse> responses = categoryService.getAll();
        return ResponseUtil.buildResponse(HttpStatus.OK, "Got All Categories", responses);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable(name = "id") String id) {
        CategoryResponse response = categoryService.getOne(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Got Category", response);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping(path = "{id}")
    public ResponseEntity<?> updateCategoryById(@PathVariable(name = "id") String id, CategoryRequest request) {
        CategoryResponse response = categoryService.updateById(id, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Updated Category", response);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable(name = "id") String id) {
        categoryService.removeById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Deleted Category", null);
    }
}
