package com.coffee.service;

import com.coffee.entity.Product;
import com.coffee.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getProductList() {
        return this.productRepository.findAllByOrderByIdDesc();
    }


    public boolean deleteProduct(Long id) {
        // exitsById(), deleteById()는 CrudRepository에 포함되어있음
        if(productRepository.existsById(id)){
            this.productRepository.deleteById(id); // 삭제하기
            return true; // true = "삭제 성공"
        } else{ // 존재하지 않으면
            return  false;
        }
    }
}
