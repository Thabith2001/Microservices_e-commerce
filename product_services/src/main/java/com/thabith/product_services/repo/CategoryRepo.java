package com.thabith.product_services.repo;

import com.thabith.product_services.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Long> {
   Category findByName(String name);
}
