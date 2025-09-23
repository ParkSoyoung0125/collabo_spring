package com.coffee.repository;

import com.coffee.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 상품의 id를 역순으로 정렬하여 상품목록을 보여주어야함.
    List<Product> findAllByOrderByIdDesc();
}
