package com.coffee.controller;

import com.coffee.dto.CartProductDto;
import com.coffee.entity.*;
import com.coffee.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/*
테스트 시나리오
사용자가 상품을 장바구니에 담았을 때 체크
    1. 로그인한 사람의 회원 ID와 상품 엔터티의 상품 번호 확인
    2. Cart 엔터티의 회원 ID가 로그인한 사람인지 확인
    3. Cart 엔터티의 카트 ID와 CartProduct 엔터티의 카트 ID가 2번과 동일해야함.
    3. CartProduct 엔터티의 상품 ID와 Product 엔터티의 상품 ID가 일치해야함.

  장바구니가 있을 때 상품을 담기
   1. Cart 엔터티에 변동사항 없음
   2. CartProduct 엔터티에 신규상품 정보만 추가됨.
*/

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor // final 키워드를 가지고있는 필드에 생성자를 이용하여 자동으로 주입.
public class CartController {

    private final MemberService memberService;
    private final ProductService productService;
    private final CartService cartService;
    private final CartProductService cartProductService;

    @PostMapping("insert") // React에서 장바구니 버튼을 클릭하였음.
    public ResponseEntity<String> addToCart(@RequestBody CartProductDto dto){

        // Member 또는 Product가 유효한 정보인지 확인
        Optional<Member> memberOptional = memberService.findMemberById(dto.getMemberId());
        Optional<Product> productOptional = productService.findProductById(dto.getProductId());

        if(memberOptional.isEmpty() || productOptional.isEmpty()){ // 정보가 무효하면
            return ResponseEntity.badRequest().body("회원 또는 상품 정보가 올바르지 않습니다.");
        }

        // Member와 Product의 객체 정보 가져오기
        Member member = memberOptional.get(); // 진짜 회원 정보
        Product product = productOptional.get(); // 진짜 상품 정보
        
        // 재고가 충분한지 확인
        if(product.getStock() < dto.getQuantity()){
            return ResponseEntity.badRequest().body("재고 수량이 부족합니다.");
        }
        
        // Cart 조회 또는 신규 작성
        Cart cart = cartService.findByMember(member);

        if(cart == null){
            Cart newCart = new Cart(); // 새로운 카트
            newCart.setMember(member); // 고객이 카트를 집어듦
            cart = cartService.saveCart(newCart); // 데이터 베이스에 저장
        }

        // 동일한 cart_id 내에 동일한 product_id가 존재하는지 확인하고 있으면 수량만 변경, 없으면 새로추가
        cartProductService.updateCart(cart, product,dto.getQuantity());

        // 재고 수량은 차감하지 않음(아직 판매한게 아니기 때문)

        return ResponseEntity.ok("상품이 장바구니에 추가되었습니다.");
    }
}
