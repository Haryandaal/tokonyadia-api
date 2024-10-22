package com.enigma.tokonyadia_api.entity;

import com.enigma.tokonyadia_api.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "amount")
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "token_snap")
    private String tokenSnap;

    @Column(name = "redirect_url")
    private String redirectUrl;

    @PrePersist
    protected void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
