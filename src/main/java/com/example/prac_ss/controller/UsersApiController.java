package com.example.prac_ss.controller;

import com.example.prac_ss.component.JwtUtil;
import com.example.prac_ss.dto.UserDto;
import com.example.prac_ss.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UsersApiController {

    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @Autowired
    UsersService usersService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signupForm(@RequestBody UserDto userDto){

        try {
            usersService.signUp(userDto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Signup successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "회원가입 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> user) {
        try {
            // AuthenticationManager.authenticate()가 호출될 때 CustomUserDetailService 실행
            //AuthenticationManager는 Spring Sequrity 인증관리 객체
            //UsernamePasswordAuthenticationToken는 username과 password를 받아서 인증확인
            //authenticate 메서드가 CustomUserDetailService를 username으로 실행시킴
            //그렇게 생성한 UserDetails와 어려것들을 이용해 Authentication(인증객체)생성
            //SecurityContext(api의 토큰인증 정보)에 해당토큰의 인증정보도 추가함
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.get("username"), user.get("password"))
            );

            //authentication의 이름을 이용해서 토큰생성
            String token = jwtUtil.generateToken(authentication.getName());
            System.out.println("실험");

            //토큰형태를 맵으로 생성후 반환      {
            //                                   "token" : (토큰문자열)
            //                                }
            return Map.of("token", token);

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
