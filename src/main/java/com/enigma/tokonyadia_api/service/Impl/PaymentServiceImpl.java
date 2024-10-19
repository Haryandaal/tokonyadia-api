package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.client.MidtransClient;
import com.enigma.tokonyadia_api.constant.PaymentStatus;
import com.enigma.tokonyadia_api.constant.TransactionStatus;
import com.enigma.tokonyadia_api.dto.request.*;
import com.enigma.tokonyadia_api.dto.response.MidtransSnapResponse;
import com.enigma.tokonyadia_api.dto.response.PaymentResponse;
import com.enigma.tokonyadia_api.entity.Payment;
import com.enigma.tokonyadia_api.entity.Transaction;
import com.enigma.tokonyadia_api.entity.TransactionDetail;
import com.enigma.tokonyadia_api.repository.PaymentRepository;
import com.enigma.tokonyadia_api.service.PaymentService;
import com.enigma.tokonyadia_api.service.TransactionService;
import com.enigma.tokonyadia_api.util.HashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionService transactionService;
    private final MidtransClient midtransClient;

    @Value("${midtrans.server.key}")
    private String MIDTRANS_SERVER_KEY;

    @Transactional(rollbackOn = Exception.class)
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        Transaction transaction = transactionService.getById(request.getTransactionId());

        if (!transaction.getTransactionStatus().equals(TransactionStatus.DRAFT))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only checkout draft orders");

        long amount = 0;
        for (TransactionDetail transactionDetail : transaction.getTransactionDetails()) {
            Integer quantity = transactionDetail.getQuantity();
            Long price = transactionDetail.getPrice();
            amount += quantity * price;
        }

        MidtransPaymentRequest midtransPaymentRequest = MidtransPaymentRequest.builder()
                .transactionDetails(MidtransTransactionRequest.builder()
                        .transactionId(transaction.getId())
                        .grossAmount(amount)
                        .build())
                .enabledPayment(List.of("bca_va", "gopay", "shopeepay", "other_qris"))
                .build();

        String headerValue = "Basic " + Base64.getEncoder().encodeToString(MIDTRANS_SERVER_KEY.getBytes(StandardCharsets.UTF_8));
        MidtransSnapResponse snapTransaction = midtransClient.createSnapTransaction(midtransPaymentRequest, headerValue);

        Payment payment = Payment.builder()
                .transaction(transaction)
                .amount(amount)
                .paymentStatus(PaymentStatus.PENDING)
                .redirectUrl(snapTransaction.getRedirectUrl())
                .tokenSnap(snapTransaction.getToken())
                .build();
        paymentRepository.saveAndFlush(payment);

        transactionService.updateTransactionStatus(transaction.getId(), UpdateTransactionStatusRequest.builder()
                        .status(TransactionStatus.PENDING)
                .build());

        return PaymentResponse.builder()
                .transactionId(payment.getTransaction().getId())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .redirectUrl(payment.getRedirectUrl())
                .tokenSnap(payment.getTokenSnap())
                .build();
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void getNotification(MidtransNotificationRequest request) {
        log.info("start getNotification: {}", System.currentTimeMillis());
        if (!validateSignatureKey(request))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid signature key");

        Payment payment = paymentRepository.findByTransaction_Id(request.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        PaymentStatus newPaymentStatus = PaymentStatus.findByDesc(request.getTransactionStatus());
        payment.setPaymentStatus(newPaymentStatus);
        payment.setUpdatedAt(LocalDateTime.now());

        Transaction transaction = transactionService.getById(request.getOrderId());

        if (newPaymentStatus != null && newPaymentStatus.equals(PaymentStatus.SETTLEMENT)) {
            transaction.setTransactionStatus(TransactionStatus.CONFIRMED);
        }

        UpdateTransactionStatusRequest updateTransactionStatusRequest = UpdateTransactionStatusRequest.builder()
                .status(transaction.getTransactionStatus())
                .build();
        transactionService.updateTransactionStatus(transaction.getId(), updateTransactionStatusRequest);
        paymentRepository.saveAndFlush(payment);
        log.info("end getNotification: {}", System.currentTimeMillis());
    }

    private boolean validateSignatureKey(MidtransNotificationRequest request) {
        String rawString = request.getOrderId() + request.getStatusCode() + request.getGrossAmount() + MIDTRANS_SERVER_KEY;
        String signatureKey = HashUtil.encryptThisString(rawString);
        return request.getSignatureKey().equalsIgnoreCase(signatureKey);
    }
}
