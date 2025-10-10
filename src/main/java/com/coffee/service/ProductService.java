package com.coffee.service;

import com.coffee.entity.Product;
import com.coffee.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public void save(Product product) {
        // save()는 CrudRepository에 포함되어있음
        this.productRepository.save(product);
    }

    public Product getProductById(Long id) {
        // findById()는 CrudRepository에 포함되어있음
        // 그리고 Optional을 반환함.
        // Optional : 해당상품이 있을 수도 있지만, 경우에 따라 없을 수도 있음.
        Optional<Product> product = this.productRepository.findById(id);

        // 의미 있는 데이터면 그냥 넘기고, 그렇지 않으면 null을 반환
        return product.orElse(null);
    }

    public Optional<Product> findById(Long id){
        return productRepository.findById(id);
    }

    public Optional<Product> findProductById(Long productId) {
        return this.productRepository.findById(productId);
    }

    public List<Product> getProductsByFilter(String filter) {
        if(filter != null && !filter.isEmpty()){
            return productRepository.findByImageContaining(filter);
        }
        return productRepository.findAll();
    }

    public Page<Product> listProduct(Pageable pageable) {
        return this.productRepository.findAll(pageable);
    }
}
