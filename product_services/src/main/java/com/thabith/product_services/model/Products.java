package com.thabith.product_services.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "product_name",
            nullable = false
    )
    private String productName;


    @ElementCollection
    @CollectionTable(
            name = "product_images",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category_id;


    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand_id;


    private int qty;

    private double price;


    @Column(
            nullable = false,
            unique = true
    )
    private String modelNumber;
}