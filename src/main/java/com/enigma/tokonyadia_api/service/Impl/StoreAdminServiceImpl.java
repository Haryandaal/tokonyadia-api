package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.constant.UserRole;
import com.enigma.tokonyadia_api.dto.request.RegisterStoreAdminRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.StoreAdminResponse;
import com.enigma.tokonyadia_api.entity.StoreAdmin;
import com.enigma.tokonyadia_api.entity.UserAccount;
import com.enigma.tokonyadia_api.repository.StoreAdminRepository;
import com.enigma.tokonyadia_api.service.StoreAdminService;
import com.enigma.tokonyadia_api.service.UserService;
import com.enigma.tokonyadia_api.specification.StoreAdminSpecification;
import com.enigma.tokonyadia_api.util.SortUtil;
import com.enigma.tokonyadia_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StoreAdminServiceImpl implements StoreAdminService {

    private final StoreAdminRepository storeAdminRepository;
    private final ValidationUtil validationUtil;
    private final UserService userService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreAdminResponse create(RegisterStoreAdminRequest request) {
        validationUtil.validate(request);

        UserAccount userAccount = UserAccount.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .password(request.getPassword())
                .role(UserRole.ROLE_STORE_ADMIN)
                .build();
        userService.create(userAccount);
        StoreAdmin storeAdmin = StoreAdmin.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .userAccount(userAccount)
                .build();
        storeAdminRepository.saveAndFlush(storeAdmin);

        return toStoreAdmin(storeAdmin);
    }

    @Override
    public Page<StoreAdminResponse> search(SearchRequest request) {
        Sort sortBy = SortUtil.parseSort(request.getSort());
        Specification<StoreAdmin> specification = StoreAdminSpecification.getSpecification(request.getQuery());
        Pageable pageable = PageRequest.of(request.getPage() <= 0 ? 0 : request.getPage() - 1, request.getSize(), sortBy);
        return storeAdminRepository.findAll(specification, pageable).map(this::toStoreAdmin);
    }

    @Transactional(readOnly = true)
    @Override
    public StoreAdmin getById(String id) {
        return storeAdminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store admin not found"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreAdminResponse update(String id, RegisterStoreAdminRequest request) {
        validationUtil.validate(request);

        StoreAdmin storeAdmin = getById(id);

        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userAccount.getRole().equals(UserRole.ROLE_STORE_ADMIN) && !userAccount.getId().equals(storeAdmin.getUserAccount().getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to update this store admin");

        storeAdmin.setName(request.getName());
        storeAdmin.setAddress(request.getAddress());
        storeAdmin.setEmail(request.getEmail());
        storeAdmin.setPhone(request.getPhone());
        storeAdminRepository.save(storeAdmin);

        return toStoreAdmin(storeAdmin);
    }

    @Transactional(readOnly = true)
    @Override
    public StoreAdminResponse getOne(String id) {
        StoreAdmin storeAdmin = storeAdminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store admin not found"));
        return toStoreAdmin(storeAdmin);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeById(String id) {
        StoreAdmin storeAdmin = getById(id);
        storeAdminRepository.delete(storeAdmin);
    }

    private StoreAdminResponse toStoreAdmin(StoreAdmin storeAdmin) {
        return StoreAdminResponse.builder()
                .id(storeAdmin.getId())
                .name(storeAdmin.getName())
                .phone(storeAdmin.getPhone())
                .email(storeAdmin.getEmail())
                .address(storeAdmin.getAddress())
                .userId(storeAdmin.getUserAccount().getId())
                .build();
    }
}
