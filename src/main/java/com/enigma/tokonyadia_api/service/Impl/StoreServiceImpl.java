package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.request.StoreRequest;
import com.enigma.tokonyadia_api.dto.response.PagingResponse;
import com.enigma.tokonyadia_api.dto.response.ProductInStoreResponse;
import com.enigma.tokonyadia_api.dto.response.StoreResponse;
import com.enigma.tokonyadia_api.entity.Product;
import com.enigma.tokonyadia_api.entity.Store;
import com.enigma.tokonyadia_api.repository.ProductRepository;
import com.enigma.tokonyadia_api.repository.StoreRepository;
import com.enigma.tokonyadia_api.service.StoreService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    private final ProductRepository productRepository;

    @Override
    public StoreResponse create(StoreRequest request) {

        Store store = new Store();
        store.setNoSiup(request.getNoSiup());
        store.setName(request.getName());
        store.setPhone(request.getPhone());
        store.setAddress(request.getAddress());

        if (request.getProducts() != null) {
            List<Product> products = request.getProducts().stream().map(productRequest -> {
                Product product = new Product();
                product.setName(productRequest.getName());
                product.setPrice(productRequest.getPrice());
                product.setStock(productRequest.getStock());
                product.setDescription(productRequest.getDescription());
                product.setStore(store); // set relation to the store
                return product;
            }).collect(Collectors.toList());
            store.setProducts(products);
        }

        storeRepository.save(store);

        return toStoreResponse(store);
    }

    @Override
    public Page<StoreResponse> search(SearchRequest request) {
        Sort sortBy = SortUtil.parseSort(request.getSort());
        PageRequest pageRequest = PageRequest.of(request.getPage() <= 0 ? 0 : request.getPage() - 1, request.getSize(), sortBy);

        Specification<Store> specification = ((root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(request.getQuery())) return criteriaBuilder.conjunction();
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(request.getQuery())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("noSiup")), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + request.getQuery() + "%")
                ));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });
        Page<Store> stores = storeRepository.findAll(specification, pageRequest);
        return stores.map(this::toStoreResponse);
    }

    @Override
    public StoreResponse getById(String id) {
        Store existingMenu = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        return toStoreResponse(existingMenu);
    }

    @Override
    public StoreResponse updateById(String id, StoreRequest request) {
        Store existingMenu = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        existingMenu.setNoSiup(request.getNoSiup());
        existingMenu.setName(request.getName());
        existingMenu.setAddress(request.getAddress());
        existingMenu.setPhone(request.getPhone());

        // Handle updating products as needed
        if (request.getProducts() != null) {
            List<Product> products = request.getProducts().stream().map(productRequest -> {
                Product product = productRepository.findById(productRequest.getStoreId())
                        .orElse(new Product());
                product.setName(productRequest.getName());
                product.setDescription(productRequest.getDescription());
                product.setPrice(productRequest.getPrice());
                product.setStock(productRequest.getStock());
                product.setStore(existingMenu);
                return product;
            }).collect(Collectors.toList());
            existingMenu.setProducts(products);
        }

        storeRepository.save(existingMenu);

        return toStoreResponse(existingMenu);
    }

    @Override
    public void removeById(String id) {
        Store existingMenu = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        List<Product> products = existingMenu.getProducts();
        if (products != null) {
            productRepository.deleteAll(products);
        }

        storeRepository.delete(existingMenu);
    }

    private StoreResponse toStoreResponse(Store store) {
//        return StoreResponse.builder()
//                .id(store.getId())
//                .noSiup(store.getNoSiup())
//                .name(store.getName())
//                .address(store.getAddress())
//                .phone(store.getPhone())
//                .build();

        StoreResponse storeResponse = new StoreResponse();
        storeResponse.setId(store.getId());
        storeResponse.setNoSiup(store.getNoSiup());
        storeResponse.setName(store.getName());
        storeResponse.setAddress(store.getAddress());
        storeResponse.setPhone(store.getPhone());

        if (store.getProducts() != null) {
            List<ProductInStoreResponse> responses = store.getProducts().stream().map(product -> {
                ProductInStoreResponse productResponse = new ProductInStoreResponse();
                productResponse.setId(product.getId());
                productResponse.setName(product.getName());
                productResponse.setPrice(product.getPrice());
                productResponse.setStock(product.getStock());
                productResponse.setDescription(product.getDescription());
                return productResponse;
            }).collect(Collectors.toList());
            storeResponse.setProducts(responses);
        }
        return storeResponse;
    }
}
