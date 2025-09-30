package com.coffee.service;

import com.coffee.entity.Cart;
import com.coffee.entity.Member;
import com.coffee.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public Cart findByMember(Member member) {
        // orElse ; 해당 Cart가 있으면 그대로 리턴하고, 없으면 null을 리턴
        return cartRepository.findByMember(member).orElse(null);
    }

    public Cart saveCart(Cart newCart) {
        return cartRepository.save(newCart);
    }

    public Optional<Cart> findByCart(Long id) {
        return  cartRepository.findById(id);
    }


}
