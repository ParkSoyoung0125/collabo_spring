package com.coffee.repository;

import com.coffee.constant.OrderStatus;
import com.coffee.entity.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    // 쿼리 메소드를 사용하여 특정 회원의 송장 번호가 큰 것(최신주문)부터 조회
    // 좀더 복잡한 쿼리를 사용하려면 @Query 또는 query dsl을 사용.
    List<Order> findByMemberIdOrderByIdDesc(Long memberId);

    // 주문번호(id) 기준으로 모든 주문 내역을 역순(내림차순)으로 조회
    List<Order> findAllByOrderByIdDesc(); // 관리자가 사용하는 메소드

    // 특정 주문에 대해 주문상태를 COMPLETED로 변경(업뎃)
    // 쿼리 메소드 대신 @Query 사용 예시 : SQL 대신 JPQL
    // 주의)
    // 1. 테이블 이름이 아닌 Entity 이름을 적어야함.
    // 2. 대소문자를 구분하기 때문에 정확히 적어야함.
    // ":"의 의미 : 치환될 값(=외부에서 넘어올 값)
    @Modifying // 이 쿼리는 select 구문이 아니고 데이터 변경을 위한 쿼리임.
    @Transactional // import jakarta.transaction.Transactional
    @Query("update Order o set o.status = :status where o.id = :orderId")
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") OrderStatus status);

    boolean existsById(Long orderId);
}
