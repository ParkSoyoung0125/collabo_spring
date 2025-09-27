package com.coffee.service;

import com.coffee.entity.Cart;
import com.coffee.entity.CartProduct;
import com.coffee.entity.Product;
import com.coffee.repository.CartProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartProductService {
    private final CartProductRepository cartProductRepository;

    public void saveCartProduct(CartProduct cp){
        this.cartProductRepository.save(cp);
    }

    public void updateCart(Cart cart, Product product, int qtt) {
        // 기존에 데이터가 존재하는지 확인
        Optional<CartProduct> cartProduct = cartProductRepository.findByCartAndProduct(cart, product);

        // 존재하면 quantity 덮어씌우기
        if (cartProduct.isPresent()){
            CartProduct cp = cartProduct.get();
            cp.setQuantity(cp.getQuantity() + qtt);
            cartProductRepository.save(cp);
        } else{
            CartProduct cp = new CartProduct();
            cp.setCart(cart);
            cp.setProduct(product);
            cp.setQuantity(qtt);
            cartProductRepository.save(cp);
        }

    }
}
