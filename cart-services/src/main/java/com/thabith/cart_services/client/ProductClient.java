package com.thabith.cart_services.client;

import com.thabith.cart_services.dto.ProductResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping(
            "/api/v1/products/single-prod/{id}"
    )
    ProductResponse findProductById(
            @PathVariable Long id
    );
}
