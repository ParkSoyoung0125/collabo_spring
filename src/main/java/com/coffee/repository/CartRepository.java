package com.coffee.repository;

import com.coffee.entity.Cart;
import com.coffee.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    // 특정 회원이 cart를 가지고 있는지 확인. 없으면 새로 생성.
    Optional<Cart> findByMember(Member member);
}
