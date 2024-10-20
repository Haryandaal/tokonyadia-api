package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.constant.UserRole;
import com.enigma.tokonyadia_api.dto.request.RegisterCustomerRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.CustomerResponse;
import com.enigma.tokonyadia_api.entity.Customer;
import com.enigma.tokonyadia_api.entity.UserAccount;
import com.enigma.tokonyadia_api.repository.CustomerRepository;
import com.enigma.tokonyadia_api.service.CustomerService;
import com.enigma.tokonyadia_api.service.UserService;
import com.enigma.tokonyadia_api.specification.CustomerSpecification;
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
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final ValidationUtil validationUtil;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse create(RegisterCustomerRequest request) {
        validationUtil.validate(request);

        UserAccount userAccount = UserAccount.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(UserRole.ROLE_CUSTOMER)
                .build();
        userService.create(userAccount);
        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .userAccount(userAccount)
                .build();
        customerRepository.saveAndFlush(customer);

        return toCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CustomerResponse> search(SearchRequest request) {
        Sort sortBy = SortUtil.parseSort(request.getSort());
        Specification<Customer> specification = CustomerSpecification.getSpecification(request.getQuery());
        Pageable pageable = PageRequest.of(request.getPage() <= 0 ? 0 : request.getPage() - 1, request.getSize(), sortBy);
        return customerRepository.findAll(specification, pageable).map(this::toCustomerResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Customer getById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse update(String id, RegisterCustomerRequest request) {
        validationUtil.validate(request);

        Customer customer = getById(id);

        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userAccount.getRole().equals(UserRole.ROLE_CUSTOMER) && !userAccount.getId().equals(customer.getUserAccount().getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to update this customer");

        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customerRepository.save(customer);

        return toCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerResponse getOne(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return toCustomerResponse(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeById(String id) {
        Customer customer = getById(id);
        customerRepository.delete(customer);
    }

    private CustomerResponse toCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .address(customer.getAddress())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .userId(customer.getUserAccount().getId())
                .build();
    }
}
