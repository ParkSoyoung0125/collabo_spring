package com.coffee.service;

import com.coffee.entity.Member;
import com.coffee.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
UserDetailsService 인터페이스
    로그인 시 입력한 email 정보를 기반으로 DB에서 사용자 정보를 조회하고,
    인증에 필요한 UserDetails 객체를 반환함.

UserDetails 인터페이스
    로그인 시 사용할 id(식별자/꼭 id형태가 아니더라도), password, 계정 만료 여부, 계정 잠금여부, 비밀번호 만료여부 등
*/

// 해당 클래스는 로그인 시 입력한 사용자 정보를 토대로 데이터베이스에서 읽은 다음, "인증" 객체로 반환하는 역할.
// 개발자가 직접 호출할 필요는 없고, Spring Security가 임시적으로 호출함.
@Service
@RequiredArgsConstructor // final 키워드와 연관됨.
public class CustomUserDetailsService implements UserDetailsService {
    final private MemberRepository memberRepository ;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // loadUserByUsername 메소드의 매개변수(username/현재는 email)는 로그인 시 사용했던 식별자(id=email)임.
        Member member = memberRepository.findByEmail(email);

        if (member == null){
            String msg = "이메일이 " + email + "인 회원은 존재하지 않습니다.";
            throw new UsernameNotFoundException(msg);
        }

        // Spring Security는 UserDetails의 구현체인 User를 사용하여 사용자 인증(Authentication)과 권한(Authorization)을 수행함.
        return User.builder()
                .username(member.getEmail()) // 로그인 시 사용했던 ID(여기서는 email)
                .password(member.getPassword()) // 데이터베이스에 저장된 암호화된 비밀 번호.
                .roles(member.getRole().name()) // 사용자의 권한정보(Role.USER, Role.Admin 등)
                .build();
    }
}
