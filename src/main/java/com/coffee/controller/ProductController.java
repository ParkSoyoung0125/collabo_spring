package com.coffee.controller;

import com.coffee.entity.Product;
import com.coffee.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 클라이언트가 특정 상품 id에 대하여 "삭제" 요청을 함.
    // @PathVariable
    @DeleteMapping("/delete/{id}") // {id}는 경로변수라고 하며, 가변 매개변수임.
    public ResponseEntity<String> delete(@PathVariable Long id) { // {id}로 넘겨온 상품 id가 변수 id에 할당됨
        try{
            boolean isDeleted = this.productService.deleteProduct(id);

            if (isDeleted){
                return ResponseEntity.ok(id + "번 상품이 삭제되었습니다.");
            } else {
                return ResponseEntity.badRequest().body(id + "번 상품이 존재하지 않습니다.");
            }
        } catch (Exception err) {
            return ResponseEntity.internalServerError().body("오류 발생 : " + err.getMessage());
        }
    }
}
