package com.thabith.cart_services.service;

import com.thabith.cart_services.client.ProductClient;

import com.thabith.cart_services.dto.AddToCartRequest;
import com.thabith.cart_services.dto.CartItemResponse;
import com.thabith.cart_services.dto.CartResponse;
import com.thabith.cart_services.dto.ProductResponse;
import com.thabith.cart_services.dto.UpdateCartItemRequest;

import com.thabith.cart_services.model.Cart;
import com.thabith.cart_services.model.CartItem;

import com.thabith.cart_services.repo.CartItemRepo;
import com.thabith.cart_services.repo.CartRepo;

import feign.FeignException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepo cartRepo;

    private final CartItemRepo cartItemRepo;

    private final ProductClient productClient;


    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {

        if (request.getQuantity() <= 0) {

            throw new RuntimeException("Quantity must be greater than 0");
        }

        ProductResponse product = getProduct(request.getProductId());


        if (!product.isInStock()) {

            throw new RuntimeException("Product is currently out of stock");
        }


        Cart cart = cartRepo.findByUserId(request.getUserId()).orElseGet(() ->

                cartRepo.save(Cart.builder().userId(request.getUserId()).build()));

        CartItem existingItem = cartItemRepo.findByCart_IdAndProductId(cart.getId(), request.getProductId()).orElse(null);


        if (existingItem != null) {

            int newQuantity = existingItem.getQuantity() + request.getQuantity();


            if (newQuantity > product.getQty()) {

                throw new RuntimeException("Only " + product.getQty() + " items are available");
            }


            existingItem.setQuantity(newQuantity);


            cartItemRepo.save(existingItem);

        } else {


            if (request.getQuantity() > product.getQty()) {

                throw new RuntimeException("Requested quantity exceeds available stock");
            }


            CartItem newItem = CartItem.builder()

                    .productId(request.getProductId())

                    .quantity(request.getQuantity())

                    .cart(cart)

                    .build();


            cartItemRepo.save(newItem);
        }


        return getCart(request.getUserId());
    }


    public CartResponse getCart(Long userId) {

        Cart cart = cartRepo.findByUserId(userId).orElse(null);


        if (cart == null) {

            return CartResponse.builder()

                    .userId(userId)

                    .items(new ArrayList<>())

                    .totalItems(0)

                    .totalPrice(0)

                    .build();
        }


        List<CartItem> cartItems = cartItemRepo.findAllByCart_Id(cart.getId());


        List<CartItemResponse> responses = new ArrayList<>();


        double totalPrice = 0;

        int totalItems = 0;


        for (CartItem cartItem : cartItems) {

            ProductResponse product = getProduct(cartItem.getProductId());


            double subtotal = product.getPrice() * cartItem.getQuantity();


            String image = null;


            if (product.getImageUris() != null && !product.getImageUris().isEmpty()) {

                image = product.getImageUris().get(0);
            }


            CartItemResponse itemResponse = CartItemResponse.builder()

                    .cartItemId(cartItem.getId())

                    .productId(product.getId())

                    .productName(product.getProductName())

                    .brand(product.getBrand())

                    .modelNumber(product.getModelNumber())

                    .image(image)

                    .quantity(cartItem.getQuantity())

                    .unitPrice(product.getPrice())

                    .subtotal(subtotal)

                    .inStock(product.isInStock())

                    .availableQty(product.getQty())

                    .build();


            responses.add(itemResponse);


            totalItems += cartItem.getQuantity();


            totalPrice += subtotal;
        }


        return CartResponse.builder()

                .cartId(cart.getId())

                .userId(cart.getUserId())

                .items(responses)

                .totalItems(totalItems)

                .totalPrice(totalPrice)

                .build();
    }


    @Transactional
    public CartResponse updateQuantity(Long itemId, UpdateCartItemRequest request) {

        CartItem item = cartItemRepo.findById(itemId).orElseThrow(() -> new RuntimeException("Cart item not found"));


        Long userId = item.getCart().getUserId();


        if (request.getQuantity() <= 0) {

            cartItemRepo.delete(item);

            return getCart(userId);
        }


        ProductResponse product = getProduct(item.getProductId());


        if (!product.isInStock()) {

            throw new RuntimeException("Product is out of stock");
        }


        if (request.getQuantity() > product.getQty()) {

            throw new RuntimeException("Only " + product.getQty() + " items are available");
        }


        item.setQuantity(request.getQuantity());


        cartItemRepo.save(item);


        return getCart(userId);
    }


    @Transactional
    public CartResponse removeItem(Long itemId) {

        CartItem item = cartItemRepo.findById(itemId).orElseThrow(() -> new RuntimeException("Cart item not found"));


        Long userId = item.getCart().getUserId();


        cartItemRepo.delete(item);


        return getCart(userId);
    }


    @Transactional
    public void clearCart(Long userId) {

        Cart cart = cartRepo.findByUserId(userId).orElse(null);


        if (cart == null) {
            return;
        }


        cartItemRepo.deleteAllByCart_Id(cart.getId());


        cartRepo.delete(cart);
    }


    @Transactional
    public void removeProductFromAllCarts(Long productId) {

        cartItemRepo.deleteAllByProductId(productId);
    }


    private ProductResponse getProduct(Long productId) {

        try {

            ProductResponse product = productClient.findProductById(productId);


            if (product == null) {

                throw new RuntimeException("Product not found: " + productId);
            }


            return product;

        } catch (FeignException.NotFound e) {

            throw new RuntimeException("Product not found: " + productId);
        }
    }
}
