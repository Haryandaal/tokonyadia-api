package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.dto.request.CustomerRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.CustomerResponse;
import com.enigma.tokonyadia_api.entity.Customer;
import com.enigma.tokonyadia_api.repository.CustomerRepository;
import com.enigma.tokonyadia_api.service.CustomerService;
import com.enigma.tokonyadia_api.util.SortUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;


    @Override
    public CustomerResponse create(CustomerRequest customer) {

        Customer newCustomer = new Customer();
        newCustomer.setName(customer.getName());
        newCustomer.setAddress(customer.getAddress() );
        newCustomer.setEmail(customer.getEmail());
        newCustomer.setPhone(customer.getPhone());

        customerRepository.saveAndFlush(newCustomer);

        return toCustomerResponse(newCustomer);
    }

    @Override
    public Page<CustomerResponse> search(SearchRequest request) {
//        List<Customer> customers = customerRepository.findAll();
//        return customers.stream().map(this::toCustomerResponse).toList();
        Sort sortBy = SortUtil.parseSort(request.getSort());
        Pageable pageable = PageRequest.of(request.getPage() <= 0 ? 0 : request.getPage() - 1, request.getSize(), sortBy);

        Specification<Customer> specification = ((root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(request.getQuery())) return criteriaBuilder.conjunction();
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(request.getQuery())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("name"), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(root.get("address"), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(root.get("phone"), "%" + request.getQuery() + "%"),
                        criteriaBuilder.like(root.get("email"), "%" + request.getQuery() + "%")
                ));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });
        Page<Customer> customers = customerRepository.findAll(specification, pageable);
        return customers.map(this::toCustomerResponse);


//
//        Page<Customer> customers = customerRepository.findAll(pageable);
//
//        return customers.map(this::toCustomerResponse);


    }

    @Override
    public CustomerResponse getById(String id) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return toCustomerResponse(existingCustomer);
    }

    @Override
    public CustomerResponse updateById(String id, CustomerRequest customer) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

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
                .build();
    }
}
