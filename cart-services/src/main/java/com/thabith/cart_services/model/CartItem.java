package com.thabith.cart_services.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "cart_id",
                                "product_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "product_id",
            nullable = false
    )
    private Long productId;


    @Column(nullable = false)
    private int quantity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cart_id",
            nullable = false
    )
    private Cart cart;
}
