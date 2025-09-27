package com.coffee.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 장바구니에 담을 상품에 대한 정보를 가지고 있는 엔터티 클래스.
@Getter @Setter @ToString
@Entity
@Table(name = "cart_products")
public class CartProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="cart_product_id") // 테이블에 생성되는 컬럼 이름
    private Long id; // 카트 상품의 아이디(PK)

    // Cart 1개에는 여러 개의 'cart product'를 담을 수 있음.
    // JoinColumn에 명시한 "cart_id"는 외래 키.
    //  이 컬럼은 PK의 이름을 그대로 복사해서 사용하면 됨.
    // mappedBy 구문이 없는 곳이 '연관관계'의 주인이 되면, 외래키를 관리해주는 ㅓ제링ㅁ;.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    //  e i[상품 1개는 여러 개의 카트상품에 담길 수 있음.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity; // 구매 수량
}
