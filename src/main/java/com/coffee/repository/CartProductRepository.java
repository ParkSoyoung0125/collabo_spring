package com.coffee.repository;

import com.coffee.entity.Cart;
import com.coffee.entity.CartProduct;
import com.coffee.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct,Long> {
    Optional<CartProduct> findByCartAndProduct(Cart cart, Product product);
}
