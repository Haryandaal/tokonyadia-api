package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.client.MidtransClient;
import com.enigma.tokonyadia_api.constant.PaymentStatus;
import com.enigma.tokonyadia_api.constant.OrderStatus;
import com.enigma.tokonyadia_api.dto.request.*;
import com.enigma.tokonyadia_api.dto.response.MidtransSnapResponse;
import com.enigma.tokonyadia_api.dto.response.PaymentResponse;
import com.enigma.tokonyadia_api.entity.*;
import com.enigma.tokonyadia_api.repository.PaymentRepository;
import com.enigma.tokonyadia_api.repository.ProductRepository;
import com.enigma.tokonyadia_api.service.CartService;
import com.enigma.tokonyadia_api.service.PaymentService;
import com.enigma.tokonyadia_api.service.ProductService;
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
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final MidtransClient midtransClient;


    @Value("${midtrans.server.key}")
    private String MIDTRANS_SERVER_KEY;

    @Transactional(rollbackOn = Exception.class)
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        Cart cart = cartService.getById(request.getCartId());

        if (!cart.getOrderStatus().equals(OrderStatus.DRAFT))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only checkout draft orders");

        // Validasi stok produk di dalam keranjang
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + product.getName() + " is out of stock.");
            }
        }

        long amount = 0;
        for (CartItem cartItem : cart.getCartItems()) {
            Integer quantity = cartItem.getQuantity();
            Long price = cartItem.getPrice();
            amount += quantity * price;
        }

        MidtransPaymentRequest midtransPaymentRequest = MidtransPaymentRequest.builder()
                .transactionDetails(MidtransTransactionRequest.builder()
                        .orderId(cart.getId())
                        .grossAmount(amount)
                        .build())
                .enabledPayment(List.of("bca_va", "gopay", "shopeepay", "other_qris"))
                .build();

        String headerValue = "Basic " + Base64.getEncoder().encodeToString(MIDTRANS_SERVER_KEY.getBytes(StandardCharsets.UTF_8));
        MidtransSnapResponse snapTransaction = midtransClient.createSnapTransaction(midtransPaymentRequest, headerValue);

        Payment payment = Payment.builder()
                .cart(cart)
                .amount(amount)
                .paymentStatus(PaymentStatus.PENDING)
                .redirectUrl(snapTransaction.getRedirectUrl())
                .tokenSnap(snapTransaction.getToken())
                .build();
        paymentRepository.saveAndFlush(payment);

        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());  // Kurangi stok produk
            productRepository.saveAndFlush(product);  // Simpan perubahan stok ke database
        }

        cartService.updateOrderStatus(cart.getId(), UpdateOrderStatusRequest.builder()
                        .status(OrderStatus.PENDING)
                .build());

        return PaymentResponse.builder()
                .cartId(payment.getCart().getId())
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

        Payment payment = paymentRepository.findByCart_Id(request.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        PaymentStatus newPaymentStatus = PaymentStatus.findByDesc(request.getTransactionStatus());
        payment.setPaymentStatus(newPaymentStatus);
        payment.setUpdatedAt(LocalDateTime.now());

        Cart cart = cartService.getById(request.getOrderId());

        if (newPaymentStatus != null && newPaymentStatus.equals(PaymentStatus.SETTLEMENT)) {
            cart.setOrderStatus(OrderStatus.CONFIRMED);
        }

        UpdateOrderStatusRequest updateOrderStatusRequest = UpdateOrderStatusRequest.builder()
                .status(cart.getOrderStatus())
                .build();
        cartService.updateOrderStatus(cart.getId(), updateOrderStatusRequest);
        paymentRepository.saveAndFlush(payment);
        log.info("end getNotification: {}", System.currentTimeMillis());
    }

    private boolean validateSignatureKey(MidtransNotificationRequest request) {
        String rawString = request.getOrderId() + request.getStatusCode() + request.getGrossAmount() + MIDTRANS_SERVER_KEY;
        String signatureKey = HashUtil.encryptThisString(rawString);
        return request.getSignatureKey().equalsIgnoreCase(signatureKey);
    }
}
