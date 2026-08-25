package com.thabith.cart_services.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long cartItemId;

    private Long productId;

    private String productName;

    private String brand;

    private String modelNumber;

    private String image;

    private int quantity;

    private double unitPrice;

    private double subtotal;

    private boolean inStock;

    private int availableQty;
}
