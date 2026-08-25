package com.thabith.product_services.repo;

import com.thabith.product_services.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BrandRepo extends JpaRepository<Brand,Long> {
    Brand findByName(String name);
}
