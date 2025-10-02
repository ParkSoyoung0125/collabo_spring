package com.coffee.repository;

import com.coffee.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 상품의 id를 역순으로 정렬하여 상품목록을 보여주어야함.
    List<Product> findAllByOrderByIdDesc();

    // image 컬럼에 특정 문자열이 포함된 데이터를 조회함.
    // 데이터베이스의 like 키워드와 유사함.
    // select * from products where image like '%bigs%';
    List<Product> findByImageContaining(String keyword);
}
