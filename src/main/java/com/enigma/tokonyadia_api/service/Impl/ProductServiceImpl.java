package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.dto.request.ProductRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.ProductInStoreResponse;
import com.enigma.tokonyadia_api.dto.response.ProductResponse;
import com.enigma.tokonyadia_api.dto.response.ProductStoreResponse;
import com.enigma.tokonyadia_api.entity.Product;
import com.enigma.tokonyadia_api.entity.Store;
import com.enigma.tokonyadia_api.repository.ProductRepository;
import com.enigma.tokonyadia_api.repository.StoreRepository;
import com.enigma.tokonyadia_api.service.ProductService;
import com.enigma.tokonyadia_api.util.SortUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final StoreRepository storeRepository;

    @Override
    public ProductResponse create(ProductRequest request) {

        Store existingStore = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStore(existingStore);

        productRepository.save(product);

        return toProductResponse(product);
    }

    @Override
    public Page<ProductResponse> search(SearchRequest request) {
        Sort sortBy = SortUtil.parseSort(request.getSort());
        PageRequest pageRequest = PageRequest.of(request.getPage() <= 0 ? 0 : request.getPage() - 1, request.getSize(), sortBy);

        Specification<Product> specification = ((root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(request.getQuery())) return criteriaBuilder.conjunction();
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(request.getQuery())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("price")), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("stock")), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + request.getQuery() + "%")
                ));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });
        Page<Product> products = productRepository.findAll(specification, pageRequest);
        return products.map(this::toProductResponse);
    }


    @Override
    public ProductResponse getById(String id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        return toProductResponse(existingProduct);
    }

    @Override
    public ProductResponse updateById(String id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());

        if (request.getStoreId() != null) {
            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found with id " + request.getStoreId()));
            existingProduct.setStore(store);
        }

        productRepository.save(existingProduct);

        return toProductResponse(existingProduct);
    }

    @Override
    public void removeById(String id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        productRepository.delete(existingProduct);
    }

    private ProductResponse toProductResponse(Product product) {
//        return ProductResponse.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .description(product.getDescription())
//                .price(product.getPrice())
//                .stock(product.getStock())
//                .store(product.getStore())
//                .build();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        productResponse.setStock(product.getStock());

        if (product.getStore() != null) {
            ProductStoreResponse storeResponse = new ProductStoreResponse();
            storeResponse.setId(product.getStore().getId());
            storeResponse.setName(product.getStore().getName());
            storeResponse.setNoSiup(product.getStore().getNoSiup());
            storeResponse.setAddress(product.getStore().getAddress());
            storeResponse.setPhone(product.getStore().getPhone());
            productResponse.setStore(storeResponse);

        }
        return productResponse;
    }
}
