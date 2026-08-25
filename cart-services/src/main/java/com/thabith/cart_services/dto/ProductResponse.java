package com.thabith.cart_services.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String productName;

    private String category;

    private String brand;

    private double price;

    private int qty;

    private boolean inStock;

    private String modelNumber;

    private List<String> imageUris;
}
