package com.coffee.repository;

import com.coffee.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

// 회원 정보들을 이용하여 데이터베이스와 교신하는 인터페이스.
// 이전의 Dao 역할
// JpaRepository<엔터티 이름, 해당 엔터티의 기본키변수의 타입>
public interface MemberRepository extends JpaRepository<Member, Long> {
    // findByEmail을 JPA에서는 '쿼리 메소드'라고 부름.
    // 이메일 정보를 이용하여 해당 회원이 존재하는지 체크.
    Member findByEmail(String email);
}
