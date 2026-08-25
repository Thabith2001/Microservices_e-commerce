package com.thabith.product_services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private long id;
    private String productName;
    private List<String> imageUris = new ArrayList<>();
    private String category;
    private String brand;
    private double price;
    private int qty;
    private boolean inStock;
    private String modelNumber;

}
