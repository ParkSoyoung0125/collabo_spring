package com.coffee.controller;

import com.coffee.entity.Product;
import com.coffee.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    @Value("${productImageLocation}")
    private String productImageLocation ; // 기본 값 : null

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

    // 상품 등록하기
    @PostMapping("/insert")
    public ResponseEntity<?> insert(@RequestBody Product product) {
        // @RequestBody : http를 사용하여 넘어온 데이터(body)를 자바 객체 형식으로 변환해줌.
        
        //데이터베이스와 이미지 경로에 저장될 이미지의 이름
        // currentTimeMillis(): 현재 시간을 초단위로 찍어주는 함수
        // 시간은 계속 흐르기 때문에 중복될 가능성이 없어서 사용함.
        String imageFileName = "product_" + System.currentTimeMillis() + ".jpg";

        // String 클래스 공부 : endsWith(), split() 알아야 함.

        // 폴더 구분자가 제대로 설정되어 있으면 그대로 사용.
        // 제대로 설정 안돼있으면 폴더 구분자 붙여서 사용.
        // File.separator : 폴더 구분자를 의미, 리눅스는 /, 윈도우는 \(역슬래시)
        String pathName = productImageLocation.endsWith("\\") || productImageLocation.endsWith("/")
                ? productImageLocation
                : productImageLocation + File.separator;

        File imageFile = new File(pathName + imageFileName);

        String imageData = product.getImage(); // Base64 인코딩 문자열(상당히 김.)

        try{
            log.info("상품 insert 정보 : " + product);
            // 파일 정보를 byte 단위로 변환하여 이미지를 복사함.
            FileOutputStream fos = new FileOutputStream(imageFile);

            // 메소드 체이닝 : 점을 연속적으로 찍어서 매소드를 계속 호출하는 것
            byte[] decodedImage = Base64.getDecoder().decode(imageData.split(",")[1]);
            fos.write(decodedImage); // 바이트 파일을 해당 이미지 경로에 복사하기

            product.setImage(imageFileName);
            product.setInputdate(LocalDate.now());

            this.productService.save(product);

            return ResponseEntity.ok(Map.of("message","Product insert successfully","image",imageFileName));
        } catch (Exception err){
            log.error("에러 정보 : " + err);
            return  ResponseEntity.status(500).body(Map.of("message",err.getMessage(),"error","Error file uploading"));
        }

    }
    // 프론트 엔드의 상품 수정 페이지에서 요청이 들어옴.
    @GetMapping("/update/{id}") // 상품의 id 정보를 이용하여 해당 상품 Bean 객체를 반환해줌.
    public ResponseEntity<Product> getUpdate(@PathVariable Long id){
        System.out.println("수정할 상품 번호 : " + id);
        log.error("error");

        Product product =  this.productService.getProductById(id);

        if(product == null){ // 상품이 없으면 404 응답과 함께 null을 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else { // 해당 상품의 정보와 함께, 성공(200) 메시지를 반환.
            return ResponseEntity.ok(product);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> putUpdate(@PathVariable Long id, @RequestBody Product updatedProduct){
        System.out.println("수정할 상품 id : " + id);

        System.out.println("관리자가 수정한 상품 정보");
        System.out.println(updatedProduct);

        Optional<Product> findProduct = productService.findById(id);
        if(findProduct.isEmpty()){ // 상품이 존재하지않으면 404 응답반환.
            return ResponseEntity.notFound().build();
        } else { // 상품이 있음.
            // Optional에서 실제 상품 정보 끄집어내기
            Product saveProduct = findProduct.get();

            try{
                // 이전 이미지 객체에 새로운 이미지 객체 정보를 업데이트함.
                saveProduct.setName(updatedProduct.getName());
                saveProduct.setPrice(updatedProduct.getPrice());
                saveProduct.setCategory(updatedProduct.getCategory());
                saveProduct.setStock(updatedProduct.getStock());
                saveProduct.setDescription(updatedProduct.getDescription());
//                saveProduct.setInputdate(LocalDate.now());

                // 이미지가 의미 있는 문자열로 되어 있고, Base64 인코딩 형식이면 이미지 이름 변경
                if(updatedProduct.getImage() != null && updatedProduct.getImage().startsWith("data:image")){
                    String imageFileName = saveProductImage(updatedProduct.getImage());
                    saveProduct.setImage(imageFileName);
                }

                this.productService.save(saveProduct); // 서비스를 통하여 데이터 베이스에 저장

                return ResponseEntity.ok(Map.of("message","상품 수정 성공"));

            } catch (Exception err){ // 오류 발생 시 500 응답코드 반환
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", err.getMessage(),"error","Error product update Failed"));
            }
        }
    }

    // Base64 인코딩 문자열을 변환하여 이미지로 만들고, 저장해주는 메소드
    private String saveProductImage(String base64Image) {

        String imageFileName = "product_" + System.currentTimeMillis()+".jpg";

        String pathName = productImageLocation.endsWith("\\") || productImageLocation.endsWith("/")
                ? productImageLocation
                : productImageLocation + File.separator;

        File imageFile = new File(pathName + imageFileName);

        // base64Image : JavaScript FileReader API에 만들어진 이미지.
        byte[] decodedImage = Base64.getDecoder().decode(base64Image.split(",")[1]);

        try{ // FileOutputStream은 바이트 파일을 처리해주는 자바의 Stream 클래스
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(decodedImage);
            return imageFileName;

        } catch (Exception e) {
            e.printStackTrace();
            return base64Image;
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Product> productDetail(@PathVariable Long id){
        Product product = productService.getProductById(id);
        if (product == null){
//                 return ResponseEntity.notFound().build();
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.ok().body(product);
            }
    }

    @GetMapping("") // 홈페이지에 보여줄 큰 이미지들에 대한 정보를 읽어옴.
    public List<Product> getBigsizeProducts(@RequestParam(required = false) String filter){
        return productService.getProductsByFilter(filter);
    }
}
