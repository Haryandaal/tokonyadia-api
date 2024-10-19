package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.dto.request.ProductRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.ProductResponse;
import com.enigma.tokonyadia_api.dto.response.StoreResponse;
import com.enigma.tokonyadia_api.entity.Category;
import com.enigma.tokonyadia_api.entity.Product;
import com.enigma.tokonyadia_api.entity.Store;
import com.enigma.tokonyadia_api.repository.ProductRepository;
import com.enigma.tokonyadia_api.service.CategoryService;
import com.enigma.tokonyadia_api.service.ProductService;
import com.enigma.tokonyadia_api.service.StoreService;
import com.enigma.tokonyadia_api.specification.ProductSpecification;
import com.enigma.tokonyadia_api.util.SortUtil;
import com.enigma.tokonyadia_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final CategoryService categoryService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse create(ProductRequest request) {
        validationUtil.validate(request);
        Store store = storeService.getById(request.getStoreId());
        Category category = categoryService.getById(request.getCategoryId());

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        product.setStore(store);

        productRepository.saveAndFlush(product);

        return toProductResponse(product);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> search(SearchRequest request) {
        Sort sortBy = SortUtil.parseSort(request.getSort());
        Specification<Product> specification = ProductSpecification.getSpecification(request.getQuery());
        PageRequest pageRequest = PageRequest.of(request.getPage() <= 0 ? 0 : request.getPage() - 1, request.getSize(), sortBy);
        return productRepository.findAll(specification, pageRequest).map(this::toProductResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Product getById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponse> getByCategoryId(String categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::toProductResponse).toList();
    }


    @Transactional(readOnly = true)
    @Override
    public ProductResponse getOne(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return toProductResponse(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse updateById(String id, ProductRequest request) {
        validationUtil.validate(request);
        Product product = getById(id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        Store store = storeService.getById(request.getStoreId());
        product.setStore(store);

        productRepository.saveAndFlush(product);
        return toProductResponse(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeById(String id) {
        Product product = getById(id);
        productRepository.delete(product);
    }

    private ProductResponse toProductResponse(Product product) {


        StoreResponse storeResponse = StoreResponse.builder()
                .id(product.getStore().getId())
                .name(product.getStore().getName())
                .noSiup(product.getStore().getNoSiup())
                .address(product.getStore().getAddress())
                .phone(product.getStore().getPhone())
                .build();
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryName(product.getCategory().getName())
                .store(storeResponse)
                .build();
    }
}
