package com.enigma.tokonyadia_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "store_admins")
public class StoreAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "phone_number", unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    @OneToMany(mappedBy = "storeAdmin")
    private List<Store> stores;
}
