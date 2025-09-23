package com.coffee.controller;

import com.coffee.entity.Member;
import com.coffee.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController // 해당 클래스는 회원과 관ㄹㄴ기웹 요청(from react)을 접수하여 처리해주는 컨트롤러 클래스
@RequiredArgsConstructor // final 키워드가 들어있는 식별자에 생성자를 통하여 값을 외부에서 주입.
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody Member bean, BindingResult bindingResult){ // 회원 가입을 위한 컨트롤러 메소드
        // ResponseEntity : Http 응답 코드(숫자 형식)나, 적절한 메시지 등을 표현하기 위한 클래스
        // JSON : JavaScript Object Notation
        // RequestBody : JSON 형태의 문자열을 자바의 객체 타입으로 변환해줌.
        // @Valid : 입력 데이터에 대한 유효성 검사를 수행하는 어노테이션.
        // BindingResult : 유효성 검사시 문제가 있으면 이 객체에 해당 예외 정보들이 포함됨.
        System.out.println(bean);

        System.out.println("유효성 오류 갯수");

        // 여기서 Field는 각각의 변수를 의미함.
        System.out.println(bindingResult.getFieldErrorCount());

        if(bindingResult.hasErrors()){ // 유효성 검사에 문제가 있음.
            // Map<컬럼이름, 오류메시지> 형식으로 만들어서 클라이언트에게 전송.
            Map<String, String> errors = new HashMap<>();

            for(FieldError err : bindingResult.getFieldErrors()){
                // errors.put(컬럼이름, 오류메시지)
                errors.put(err.getField(),err.getDefaultMessage());
            }

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        Member member = memberService.findByEmail(bean.getEmail());

        if(member != null){ // 이미 존재하는 email
            return new ResponseEntity<>(Map.of("email","이미 존재하는 이메일 주소입니다."), HttpStatus.BAD_REQUEST);
        } else{ // 회원 가입 처리
            memberService.insert(bean);
            return new ResponseEntity<>("회원 가입에 성공하였습니다.", HttpStatus.OK);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String , Object>> login(@RequestBody Member bean){
        // bean은 클라이언트가 기입한 로그인 정보를 담고있는 객체
        System.out.println("클라이언트에서 로그인 요청이 들어옴.");
        System.out.println(bean);

        // Member는 데이터베이스에서 내가 조회한 회원정보를 담고있는 객체
        Member member = this.memberService.findByEmail(bean.getEmail());

        boolean isFound = false; // 회원이 발견되면  true로 바뀜

        if(member != null){ // 아이디가 존재함
            if(bean.getPassword().equals(member.getPassword())) {
                isFound = true;
            }
        }

        // Response : 클라이언트에게 넘겨주고자 하는 응답 정보의 모음
        Map<String , Object> response = new HashMap<>();

        if (isFound == true){
            response.put("message","success");
            response.put("member",member);
            return ResponseEntity.ok(response);
        } else{
            response.put("message", "ID 또는 비밀번호가 잘못되었습니다.");
            // 응답코드 401은 인증실패(Unauthorized)를 의미함.
            return ResponseEntity.status(401).body(response);
        }

    }
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(){
//        // 추후 추가 코딩 정보가 있으면 추가할 수 있음.
//        return ResponseEntity.ok("logout success");
//    }
    
}
