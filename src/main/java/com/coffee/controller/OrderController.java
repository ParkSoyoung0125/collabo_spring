package com.coffee.controller;

import com.coffee.constant.OrderStatus;
import com.coffee.constant.Role;
import com.coffee.dto.OrderDto;
import com.coffee.dto.OrderItemDto;
import com.coffee.dto.OrderResponseDto;
import com.coffee.entity.Member;
import com.coffee.entity.Order;
import com.coffee.entity.OrderProduct;
import com.coffee.entity.Product;
import com.coffee.service.CartProductService;
import com.coffee.service.MemberService;
import com.coffee.service.OrderService;
import com.coffee.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ProductService productService;
    private final CartProductService cartProductService;

    // React의 '카트 목록'이나 '주문하기' 버튼을 눌러서 주문을 시도함.
    @PostMapping("") // CartList.js 파일의 makeOrder()함수와 연관이 있음
    public ResponseEntity<?> order(@RequestBody OrderDto dto){
        System.out.println(dto);

        // 회원(Member) 객체 생성
        Optional<Member> optionalMember = memberService.findMemberById(dto.getMemberId());
        if(!optionalMember.isPresent()){
            throw new RuntimeException("회원이 존재하지 않습니다.");
        }
        Member member = optionalMember.get();

        // 혹시 마일리지 적립 시스템이면, 마일리지 적립은 여기서 해야함.
        // 주문(Order) 객체 생성
        Order order = new Order();
        order.setMember(member); // 이 사람이 주문자임
        order.setOrderDate(LocalDate.now()); // 주문 시점
        order.setStatus(dto.getStatus());

        // 주문 상품(OrderProduct)들은 확장 for 구문을 사용함.
        List<OrderProduct> orderProductList = new ArrayList<>();

        for (OrderItemDto item : dto.getOrderItems()){
            // item은 주문하고자 하는 상품 1개를 의미함.
            Optional<Product> optionalProduct = productService.findProductById(item.getProductId());

            if(!optionalProduct.isPresent()){
                throw new RuntimeException("해당 상품이 존재하지않습니다.");
            }
            Product product = optionalProduct.get();
            
            if(product.getStock() < item.getQuantity()){
                throw new RuntimeException("재고 수량이 부족합니다.");
            }
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(item.getQuantity());

            // 리스트 컬렉션에 각 '주문 상품'을 담아 줌.
            orderProductList.add(orderProduct);

            // 상품의 재고수량 빼기
            product.setStock(product.getStock() - item.getQuantity());

            // 카트에 담겨 있던 품목을 삭제해 줘야 합니다.
            Long cartProductId = item.getCartProductId() ;
            //System.out.println("cartProductId : " + cartProductId);

            if(cartProductId != null){ // 장바구니 내역에서 `주문하기` 버튼을 클릭한 경우에 해당함
                cartProductService.deleteById(cartProductId);

            }else{
                System.out.println("상품 상세 보기에서 클릭하셨군요.");
            }
        }

        order.setOrderProducts(orderProductList);

        // 주문 객체를 저장함.
        orderService.saveOrder(order);

        String message = "주문이 완료되었습니다.";

        return ResponseEntity.ok(message);
    }

    // 특정한 회원의 주문 정보를 최신 날짜 순으로 조회함
    // http://localhost:9000/order/list?memberId=회원아이디
    @GetMapping("/list") // React의 OrderList.js 파일 내의 useEffect 참조
    public ResponseEntity<List<OrderResponseDto>> getOrderList(@RequestParam Long memberId, @RequestParam Role role){
        System.out.println("로그인 한 사람의 id : " + memberId);
        System.out.println("로그인 한 사람의 역할 : " + role);

        List<Order> orders = null;

        if (role == Role.ADMIN){ // 관리자면 모든 주문 내역 조회하기 
//            System.out.println("관리자");
            orders = orderService.findAllOrders();
        } else { // 일반 유저인 경우 본인 주문만 조회하기
//            System.out.println("일반 유저");
            orders = orderService.findByMemberId(memberId); // memberId로 Id계정주의 주문 정보 가져오기
        }


        System.out.println("주문 건수 : " + orders.size());

        List<OrderResponseDto> responseDtos = new ArrayList<>(); // 전송할 데이터(빈 곽)

        for (Order bean : orders){
            OrderResponseDto dto = new OrderResponseDto();

            // 주문의 기초 정보 세팅
            dto.setOrderId(bean.getId());
            dto.setOrderDate(bean.getOrderDate());
            dto.setStatus(bean.getStatus().name());

            // 주문 기초 정보 내에 여러 개의 상품 정보 세팅
            List<OrderResponseDto.OrderItem> orderItems = new ArrayList<>();

            for (OrderProduct op : bean.getOrderProducts()){
                OrderResponseDto.OrderItem item
                        = new OrderResponseDto.OrderItem(op.getProduct().getName(), op.getQuantity());

                orderItems.add(item);
            }
            // OrderProduct : 하나의 주문을 뜻하는 엔터티
            // orders : memberId로 받아온 주문내역 리스트
            //  bean : 주문 내역 리스트(orders)를 배열한 1 건의 주문
            // bean.getOrderProducts() : 1건의 주문 내역 안에 있는 여러개의 상품 정보(ex:한번에 상품 3개 결제했을 시)
            // OrderResponseDto.OrderItem : OrderResponseDto 안의 중첩클래스 OrderItem(productName, quantity)

            dto.setOrderItems(orderItems);
            responseDtos.add(dto);
        }

        return  ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/update/{orderId}")
    public String ddd(@PathVariable Long orderId){
        System.out.println("수정할 항목 : " + orderId);
        return null ;
    }

    // /update/status/${bean.orderId}?status=${newStatus}
    @PutMapping("/update/status/{orderId}")
    public ResponseEntity<String>  statusChange(@PathVariable Long orderId, @RequestParam OrderStatus status){
        System.out.println("수정할 항목의 아이디 : " + orderId);
        System.out.println("변경하고자 하는 주문 상태 : " + status);

        int affected = -1; // DB에 반영이 된 행 갯수
        affected = orderService.updateOrderStatus(orderId,status);
        System.out.println("DB에 반영이 된 행 갯수 : " + affected);

        String message = "송장 번호 " + orderId + "의 주문 상태가 변경되었습니다.";
        return ResponseEntity.ok(message);
    }

    // '관리자' 또는 '주문 당사자'가 주문에 대한 삭제 요청
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId){
        if (!orderService.exitsById(orderId)){
            return ResponseEntity.notFound().build();
        }



        // 아래부터는 재고 수량 증가를 위한 코드
        Optional<Order> orderOptional = orderService.findByOrderId(orderId);
        if(orderOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Order order = orderOptional.get();

        // 주문 상품을 반복하면서 재고 수량을 더해줌
        for (OrderProduct op : order.getOrderProducts()){
            Product product = op.getProduct();
            int quantity = op.getQuantity();

            // 기존 재고에 주문 취소된 수량을 다시 더해줌.
            product.setStock(product.getStock() + quantity);
            log.error("실행");

            productService.save(product); // 데이터베이스에 수정.
        }

        orderService.deleteById(orderId);

        String message  = "주문이 취소되었습니다.";
        return ResponseEntity.ok(message);
    }
}

