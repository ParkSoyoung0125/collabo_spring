package com.coffee.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class OrderDetailDto {
    private Long orderId;
    private String memberName; // 구매자 명
    private LocalDate orderDate;// 구매 일자
    private int totalQuantity;// 구매 수량
    private int totalAmount;// 구매 금액
    private List<OrderOneDto> items;// 모든 구매 상품
}
