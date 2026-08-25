package com.thabith.userservice.repo;

import com.thabith.userservice.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {

    @Query("select u from Users u where u.email =:email")
    Optional<Users> findByEmail(@Param("email") String email);


    @Query("select u from Users u where lower(u.email) LIKE lower(:text) " +
            "OR lower(u.contact) LIKE lower(:text)")
    Optional<Users> searchUsers(@Param("text") String text);


}
