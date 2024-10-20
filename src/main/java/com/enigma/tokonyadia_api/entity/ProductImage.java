package com.enigma.tokonyadia_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.awt.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_image")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
}
