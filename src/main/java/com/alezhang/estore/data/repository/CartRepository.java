package com.alezhang.estore.data.repository;

import com.alezhang.estore.data.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    long countAllByProductId(String productId);

    Cart findByUidAndProductId(long uid, String productId);

    List<Cart> findAllByUid(long uid);
}
