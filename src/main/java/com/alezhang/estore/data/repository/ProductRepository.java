package com.alezhang.estore.data.repository;

import com.alezhang.estore.data.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    void deleteByProductId(String productId);

    Product findByProductId(String productId);
}
