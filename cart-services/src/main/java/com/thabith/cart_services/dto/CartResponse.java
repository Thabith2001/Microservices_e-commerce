package com.thabith.cart_services.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long cartId;

    private Long userId;

    @Builder.Default
    private List<CartItemResponse> items =
            new ArrayList<>();

    private int totalItems;

    private double totalPrice;
}
