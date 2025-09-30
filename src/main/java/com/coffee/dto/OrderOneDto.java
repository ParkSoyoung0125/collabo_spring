package com.coffee.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class OrderOneDto{
    private Long productId;// 상품 아이디
    private String productName;// 상품명
    private int price;// 상품단가
    private int quantity;// 구매수량
    private int totalAmount;// 구매 총 금액

}
