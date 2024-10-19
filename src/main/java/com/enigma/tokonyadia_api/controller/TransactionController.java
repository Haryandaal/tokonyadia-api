package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.request.TransactionDetailRequest;
import com.enigma.tokonyadia_api.dto.request.TransactionRequest;
import com.enigma.tokonyadia_api.dto.request.UpdateTransactionStatusRequest;
import com.enigma.tokonyadia_api.dto.response.TransactionDetailResponse;
import com.enigma.tokonyadia_api.dto.response.TransactionResponse;
import com.enigma.tokonyadia_api.service.TransactionService;
import com.enigma.tokonyadia_api.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping(path = "draft")
    public ResponseEntity<?> createDraftOrder(@RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createDraft(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Successfully created draft transaction", response);
    }

    @GetMapping(path = "{transId}/details")
    public ResponseEntity<?> getTransactionDetails(@PathVariable("transId") String transId) {
        List<TransactionDetailResponse> transactionDetails = transactionService.getTransactionDetails(transId);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully retrieved order details", transactionDetails);
    }

    @PostMapping(path = "{transId}/details")
    public ResponseEntity<?> addTransactionDetails(@PathVariable("transId") String transId, @RequestBody TransactionDetailRequest request) {
        TransactionResponse response = transactionService.addTransactionDetail(transId, request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Successfully added details transaction", response);
    }

    @PutMapping(path = "{transId}/details/{detailId}")
    public ResponseEntity<?> updateTransactionDetails(@PathVariable("transId") String transId, @PathVariable("detailId") String detailId, @RequestBody TransactionDetailRequest request) {
        TransactionResponse response = transactionService.updateTransactionDetail(transId, detailId, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully updated details transaction", response);
    }

    @DeleteMapping("/{transId}/details/{detailId}")
    public ResponseEntity<?> removeOrderDetail(@PathVariable("transId") String transId, @PathVariable("detailId") String detailId) {
        TransactionResponse response = transactionService.removeTransactionDetails(transId, detailId);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully deleted details transaction", response);
    }

    @PatchMapping("/{transId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable("transId") String transId, @RequestBody UpdateTransactionStatusRequest request) {
        TransactionResponse response = transactionService.updateTransactionStatus(transId, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully updated transaction status", request);
    }

//    @PostMapping("/{transId}/checkout")
//    public ResponseEntity<?> checkoutOrder(@PathVariable("transId") String orderId) {
//        TransactionResponse response = transactionService.checkoutTransaction(orderId);
//        return ResponseUtil.buildResponse(HttpStatus.OK, "Success checkout transaction", response);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        return ResponseUtil.buildResponse(HttpStatus.OK, "Success get transaction by id", transactionService.getById(id));
    }
}
