package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.constant.TransactionStatus;
import com.enigma.tokonyadia_api.dto.request.SearchTransactionRequest;
import com.enigma.tokonyadia_api.dto.request.TransactionDetailRequest;
import com.enigma.tokonyadia_api.dto.request.TransactionRequest;
import com.enigma.tokonyadia_api.dto.request.UpdateTransactionStatusRequest;
import com.enigma.tokonyadia_api.dto.response.TransactionDetailResponse;
import com.enigma.tokonyadia_api.dto.response.TransactionResponse;
import com.enigma.tokonyadia_api.entity.*;
import com.enigma.tokonyadia_api.repository.TransactionRepository;
import com.enigma.tokonyadia_api.service.CustomerService;
import com.enigma.tokonyadia_api.service.ProductService;
import com.enigma.tokonyadia_api.service.TransactionService;
import com.enigma.tokonyadia_api.specification.StoreSpecification;
import com.enigma.tokonyadia_api.util.DateUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;
    private final ProductService productService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransactionResponse createDraft(TransactionRequest request) {
        validationUtil.validate(request);
        Customer customer = customerService.getById(request.getCustomerId());
        Transaction draftTransaction = Transaction.builder()
                .customer(customer)
                .transactionStatus(TransactionStatus.DRAFT)
                .transactionDetails(new ArrayList<>())
                .build();
        transactionRepository.saveAndFlush(draftTransaction);
        return toTransactionResponse(draftTransaction);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDetailResponse> getTransactionDetails(String transactionId) {
        Transaction transaction = getById(transactionId);
        return transaction.getTransactionDetails().stream()
                .map(this::toTransactionDetailResponse)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransactionResponse addTransactionDetail(String transactionId, TransactionDetailRequest request) {
        validationUtil.validate(request);
        Transaction transaction = getById(transactionId);
        if (transaction.getTransactionStatus() != TransactionStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only add items to draft orders");

        Product product = productService.getById(request.getProductId());

        Optional<TransactionDetail> existingOrderDetail = transaction.getTransactionDetails().stream()
                .filter(transactionDetail -> transactionDetail.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingOrderDetail.isPresent()) {
            TransactionDetail transactionDetail = existingOrderDetail.get();
            transactionDetail.setQuantity(transactionDetail.getQuantity() + request.getQuantity());
            transactionDetail.setPrice(product.getPrice());
        } else {
            TransactionDetail newTransactionDetail = TransactionDetail.builder()
                    .product(product)
                    .transaction(transaction)
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .build();
            transaction.getTransactionDetails().add(newTransactionDetail);
        }
        transactionRepository.save(transaction);
        return toTransactionResponse(transaction);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransactionResponse updateTransactionDetail(String transactionId, String detailId, TransactionDetailRequest request) {
        validationUtil.validate(request);
        Transaction transaction = getById(transactionId);
        if (transaction.getTransactionStatus() != TransactionStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only update items to draft orders");

        TransactionDetail transactionDetail = transaction.getTransactionDetails().stream()
                .filter(detail -> detail.getId().equals(detailId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "order details not found"));

        Product product = productService.getById(request.getProductId());
        transactionDetail.setProduct(product);
        transactionDetail.setQuantity(request.getQuantity());
        transactionDetail.setPrice(product.getPrice());

        transactionRepository.save(transaction);
        return toTransactionResponse(transaction);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransactionResponse removeTransactionDetails(String transactionId, String detailId) {
        Transaction transaction = getById(transactionId);
        if (transaction.getTransactionStatus() != TransactionStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only delete items to draft orders");

        transaction.getTransactionDetails().removeIf(detail -> detail.getId().equals(detailId));
        transactionRepository.save(transaction);
        return toTransactionResponse(transaction);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransactionResponse updateTransactionStatus(String transactionId, UpdateTransactionStatusRequest request) {
        validationUtil.validate(request);
        Transaction transaction = getById(transactionId);
        transaction.setTransactionStatus(request.getStatus());
        transactionRepository.save(transaction);
        return toTransactionResponse(transaction);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TransactionResponse> getAllTransactions(SearchTransactionRequest request) {
//        Sort sortBy = SortUtil.parseSort(request.getSort());
////        Specification<Store> specification = StoreSpecification.getSpecification(request.getQuery());
//        PageRequest pageRequest = PageRequest.of(request.getPage() <= 0 ? 0 : request.getPage() - 1, request.getSize(), sortBy);
//        return transactionRepository.findAll(specification, pageRequest).map(this::toTransactionResponse);
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public TransactionResponse getOne(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction not found"));
        return toTransactionResponse(transaction);
    }

    @Transactional(readOnly = true)
    @Override
    public Transaction getById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction not found"));
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        List<TransactionDetailResponse> transactionDetailResponses = transaction.getTransactionDetails().stream()
                .map(this::toTransactionDetailResponse)
                .toList();

        return TransactionResponse.builder()
                .id(transaction.getId())
                .customerId(transaction.getCustomer().getId())
                .customerName(transaction.getCustomer().getName())
                .transactionDate(DateUtil.localDateTimeToString(transaction.getTransactionDate()))
                .transactionStatus(transaction.getTransactionStatus())
                .transactionDetails(transactionDetailResponses)
                .build();
    }

    private TransactionDetailResponse toTransactionDetailResponse(TransactionDetail transactionDetail) {
        return TransactionDetailResponse.builder()
                .id(transactionDetail.getId())
                .productId(transactionDetail.getProduct().getId())
                .productName(transactionDetail.getProduct().getName())
                .quantity(transactionDetail.getQuantity())
                .price(transactionDetail.getPrice())
                .build();
    }
}
