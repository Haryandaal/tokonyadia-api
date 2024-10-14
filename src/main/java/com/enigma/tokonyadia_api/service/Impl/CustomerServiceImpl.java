package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.constant.UserRole;
import com.enigma.tokonyadia_api.dto.request.CustomerCreateRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.CustomerResponse;
import com.enigma.tokonyadia_api.entity.Customer;
import com.enigma.tokonyadia_api.entity.UserAccount;
import com.enigma.tokonyadia_api.repository.CustomerRepository;
import com.enigma.tokonyadia_api.service.CustomerService;
import com.enigma.tokonyadia_api.service.UserService;
import com.enigma.tokonyadia_api.util.SortUtil;
import jakarta.persistence.criteria.Predicate;
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
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse create(CustomerCreateRequest request) {

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
        Pageable pageable = PageRequest.of(request.getPage() <= 0 ? 0 : request.getPage() - 1, request.getSize(), sortBy);

        Specification<Customer> specification = ((root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(request.getQuery())) return criteriaBuilder.conjunction();
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(request.getQuery())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + request.getQuery() + "%")
                ));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });
        Page<Customer> customers = customerRepository.findAll(specification, pageable);
        return customers.map(this::toCustomerResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerResponse getById(String id) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return toCustomerResponse(existingCustomer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse update(String id, CustomerCreateRequest customer) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userAccount.getRole().equals(UserRole.ROLE_CUSTOMER) && !userAccount.getId().equals(existingCustomer.getUserAccount().getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to update this customer");

        existingCustomer.setName(customer.getName());
        existingCustomer.setAddress(customer.getAddress());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setPhone(customer.getPhone());
        customerRepository.save(existingCustomer);

        return toCustomerResponse(existingCustomer);
    }

    @Override
    public void removeById(String id) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        customerRepository.delete(existingCustomer);
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
