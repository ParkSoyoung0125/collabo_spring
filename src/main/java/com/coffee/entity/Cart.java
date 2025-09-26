package com.coffee.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 고객(Member)이 사용하는 카트 엔터티 클래스.

@Getter @Setter @ToString
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="cart_id") // 테이블에 생성되는 컬럼 이름
    private Long id; // 카트 id

    // 고객 1명이 1개의 카트를 사용.
    @OneToOne(fetch = FetchType.LAZY) // 일대일 연관관계 매핑, 지연 로딩
    @JoinColumn(name = "member_id")
    private Member member;
}
