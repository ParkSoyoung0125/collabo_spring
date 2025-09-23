package com.coffee.controller;

import com.coffee.entity.Product;
import com.coffee.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list") // 상품 목록을 List 컬렉션으로 반환.
    public List<Product> list(){
        List<Product> products = null;
        products = productService.getProductList();
        return products;
    }
}
