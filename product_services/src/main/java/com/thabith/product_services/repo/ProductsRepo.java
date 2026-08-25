package com.thabith.product_services.repo;

import com.thabith.product_services.model.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductsRepo extends JpaRepository<Products, Long> {


    @Query(
            value = """
        SELECT p
        FROM Products p
        JOIN FETCH p.brand_id
        JOIN FETCH p.category_id
    """,
            countQuery = """
        SELECT COUNT(p)
        FROM Products p
    """
    )
    Page<Products> allProducts(Pageable pageable);

    Products findByModelNumber(String modelNumber);

    @Query("""
    SELECT p
    FROM Products p
    JOIN FETCH p.brand_id
    JOIN FETCH p.category_id
    WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :text, '%'))
       OR LOWER(p.modelNumber) LIKE LOWER(CONCAT('%', :text, '%'))
       OR LOWER(p.category_id.name) LIKE LOWER(CONCAT('%', :text, '%'))
       OR LOWER(p.brand_id.name) LIKE LOWER(CONCAT('%', :text, '%'))
""")
    List<Products> findByText(@Param("text") String text);
}
