package com.enigma.tokonyadia_api.entity;

import com.enigma.tokonyadia_api.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false, referencedColumnName = "id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "order_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @PrePersist
    private void prePersist() {
        this.orderDate = LocalDateTime.now();
    }

}
