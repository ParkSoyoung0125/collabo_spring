package com.coffee.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class OrderListDto {
    private Long orderId; // 주문번호
    private LocalDate orderDate; // 주문 일자
    private int totalQuantity; // 주문 총 수량(상품별 낱개 X, 상품별 건수
    private String ordersSummary; // 대표 상품명 "아메리카노 외 n건"
}
