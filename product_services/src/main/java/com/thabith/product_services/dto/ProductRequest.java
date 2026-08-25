package com.thabith.product_services.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private String productName;
    private String category;
    private String brand;
    private double price;
    private int qty;
    private String modelNumber;

}
