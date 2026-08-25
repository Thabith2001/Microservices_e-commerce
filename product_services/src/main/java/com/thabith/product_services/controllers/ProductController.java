package com.thabith.product_services.controllers;

import org.springframework.data.domain.Page;
import tools.jackson.databind.json.JsonMapper;
import com.thabith.product_services.dto.ProductRequest;
import com.thabith.product_services.dto.ProductResponse;
import com.thabith.product_services.services.ProductServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductServices services;
    private final JsonMapper jsonMapper;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> saveProducts(@RequestParam("product") String productJson, @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        ProductRequest request = jsonMapper.readValue(productJson, ProductRequest.class);

        ProductResponse response = services.addProducts(request, images);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {

        Page<ProductResponse> resp = services.allProducts(page, size);

        return ResponseEntity.ok(resp);
    }


    @GetMapping("/{text}")
    public ResponseEntity<List<?>> findByText(@PathVariable String text) {

        List<ProductResponse> resp = services.findByText(text);

        if (!resp.isEmpty()) {
            return ResponseEntity.ok(resp);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList("PRODUCTS NOT FOUND"));
    }


    @GetMapping("/single-prod/{id}")
    public ResponseEntity<?> findById(@PathVariable long id) {
        ProductResponse resp = services.findById(id);
        if (resp != null) {
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList("PRODUCTS NOT FOUND"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        boolean resp = services.delete(id);
        if (resp) {
            return ResponseEntity.status(HttpStatus.GONE).body("SUCCESSFULLY DELETED");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FAILED TO DELETE");
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/{id}")
    public ResponseEntity<?> update(@RequestParam("product") String productJson, @PathVariable long id, @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        ProductRequest request = jsonMapper.readValue(productJson, ProductRequest.class);

        ProductResponse response = services.update(request, id, images);

        if (response != null) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PRODUCT NOT FOUND");
    }
}