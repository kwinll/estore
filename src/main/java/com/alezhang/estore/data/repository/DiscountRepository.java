package com.alezhang.estore.data.repository;

import com.alezhang.estore.data.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Discount findByProductId(String productId);

    void deleteByProductId(String productId);

    List<Discount> findByStrategy(String strategy);
}
