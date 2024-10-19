package com.enigma.tokonyadia_api.repository;

import com.enigma.tokonyadia_api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByTransaction_Id(String transId);
}