package com.thabith.cart_services.repo;



import com.thabith.cart_services.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepo
        extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCart_IdAndProductId(
            Long cartId,
            Long productId
    );


    List<CartItem> findAllByCart_Id(
            Long cartId
    );


    void deleteAllByCart_Id(
            Long cartId
    );


    void deleteAllByProductId(
            Long productId
    );
}
