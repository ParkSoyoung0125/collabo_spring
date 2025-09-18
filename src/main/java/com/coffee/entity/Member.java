package com.coffee.entity;

import com.coffee.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

// 회원 1명에 대한 정보를 저장하고 있는 자바 클래스
@Getter
@Setter
@ToString
@Entity // 해당 클래스를 엔터티로 관리함.
@Table(name = "Members") // 기본 클래스명으로 테이블명을 하지않고 직접 테이블명 명명.
public class Member {
    @Id // 이 컬럼은 primary key 입니다.
    @GeneratedValue(strategy = GenerationType.AUTO) // 기본키 생성 전략
    @Column(name = "member_id") // 컬럼 이름 변경
    private long id;
    
    private String name;

    // 동일한 값 허용안함, Null 허용안함
    @Column(unique = true, nullable = false)
    private String email;
    
    private String password;
    
    private String address;

    @Enumerated(EnumType.STRING) // 컬럼에 문자열 형식으로 데이터가 들어감.
    private Role role; // 일반유저 또는 관리자
    
    private LocalDate regdate; // 가입일

    public  Member() {}

    public Member(long id, String name, String email, String password, String address, Role role, LocalDate regdate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.role = role;
        this.regdate = regdate;
    }
}


