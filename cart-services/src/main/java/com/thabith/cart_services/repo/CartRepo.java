package com.thabith.cart_services.repo;


import com.thabith.cart_services.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo
        extends JpaRepository<Cart, Long> {

@Query("select c from Cart c where c.userId=:userId")
    Optional<Cart> findByUserId(@Param("userId") Long userId);
}
