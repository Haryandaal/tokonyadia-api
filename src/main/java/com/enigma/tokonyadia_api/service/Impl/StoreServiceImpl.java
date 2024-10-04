package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.request.StoreRequest;
import com.enigma.tokonyadia_api.dto.response.StoreResponse;
import com.enigma.tokonyadia_api.entity.Store;
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

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Override
    public StoreResponse create(StoreRequest request) {

        Store store = new Store();
        store.setNoSiup(request.getNoSiup());
        store.setName(request.getName());
        store.setAddress(request.getAddress());
        store.setPhone(request.getPhone());

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
                        criteriaBuilder.like(root.get("noSiup"), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(root.get("name"), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(root.get("address"), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(root.get("phone"), "%" + request.getQuery() + "%")
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

        storeRepository.save(existingMenu);

        return toStoreResponse(existingMenu);
    }

    @Override
    public void removeById(String id) {
        Store existingMenu = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        storeRepository.delete(existingMenu);
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
