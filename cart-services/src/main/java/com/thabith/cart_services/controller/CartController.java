package com.thabith.cart_services.controller;

import com.thabith.cart_services.dto.AddToCartRequest;
import com.thabith.cart_services.dto.CartResponse;
import com.thabith.cart_services.dto.UpdateCartItemRequest;

import com.thabith.cart_services.service.CartService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @PostMapping
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request) {
        CartResponse response = cartService.addToCart(request);


        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cartService.getCart(Long.parseLong(Objects.requireNonNull(jwt.getSubject()))));
    }


    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateQuantity(@PathVariable Long itemId, @RequestBody UpdateCartItemRequest request) {

        return ResponseEntity.ok(cartService.updateQuantity(itemId, request));
    }


    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long itemId) {

        return ResponseEntity.ok(cartService.removeItem(itemId));
    }


    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal Jwt jwt) {
        cartService.clearCart(Long.parseLong(Objects.requireNonNull(jwt.getSubject())));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> removeProductFromAllCarts(@PathVariable Long productId) {
        cartService.removeProductFromAllCarts(productId);
        return ResponseEntity.noContent().build();
    }
}