package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.request.StoreRequest;
import com.enigma.tokonyadia_api.dto.response.ProductInStoreResponse;
import com.enigma.tokonyadia_api.dto.response.StoreResponse;
import com.enigma.tokonyadia_api.entity.Product;
import com.enigma.tokonyadia_api.entity.Store;
import com.enigma.tokonyadia_api.repository.ProductRepository;
import com.enigma.tokonyadia_api.repository.StoreRepository;
import com.enigma.tokonyadia_api.service.StoreService;
import com.enigma.tokonyadia_api.specification.StoreSpecification;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreResponse create(StoreRequest request) {
        validationUtil.validate(request);
        Store store = new Store();
        store.setNoSiup(request.getNoSiup());
        store.setName(request.getName());
        store.setPhone(request.getPhone());
        store.setAddress(request.getAddress());

        storeRepository.saveAndFlush(store);

        return toStoreResponse(store);
    }

    @Transactional(readOnly = true)
    public List<ProductInStoreResponse> getByStoreId(String storeId) {
        List<Product> existingStore = productRepository.findByStoreId(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        return existingStore.stream().map(product ->
                new ProductInStoreResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock()
                )).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<StoreResponse> search(SearchRequest request) {
        Sort sortBy = SortUtil.parseSort(request.getSort());
        Specification<Store> specification = StoreSpecification.getSpecification(request.getQuery());
        PageRequest pageRequest = PageRequest.of(request.getPage() <= 0 ? 0 : request.getPage() - 1, request.getSize(), sortBy);
        return storeRepository.findAll(specification, pageRequest).map(this::toStoreResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Store getById(String id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public StoreResponse getOne(String id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));
        return toStoreResponse(store);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreResponse updateById(String id, StoreRequest request) {
        validationUtil.validate(request);
        Store store = getById(id);

        store.setNoSiup(request.getNoSiup());
        store.setName(request.getName());
        store.setAddress(request.getAddress());
        store.setPhone(request.getPhone());

        // Handle updating products as needed
        if (request.getProducts() != null) {
            List<Product> products = request.getProducts().stream().map(productRequest -> {
                Product product = productRepository.findById(productRequest.getStoreId())
                        .orElse(new Product());
                product.setName(productRequest.getName());
                product.setDescription(productRequest.getDescription());
                product.setPrice(productRequest.getPrice());
                product.setStock(productRequest.getStock());
                product.setStore(store);
                return product;
            }).collect(Collectors.toList());
            store.setProducts(products);
        }

        storeRepository.save(store);

        return toStoreResponse(store);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeById(String id) {
        Store store = getById(id);
        List<Product> products = store.getProducts();
        if (products != null) {
            productRepository.deleteAll(products);
        }

        storeRepository.delete(store);
    }

    private StoreResponse toStoreResponse(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .noSiup(store.getNoSiup())
                .name(store.getName())
                .address(store.getAddress())
                .phone(store.getPhone())
                .build();

    }
}
