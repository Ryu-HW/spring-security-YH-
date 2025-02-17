package com.example.prac_ss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
//순환참조오류 발생
public class PasswordEncoderConfig {

    @Bean
    //BCrypt를 권장한다고 함 spring security가.
    public PasswordEncoder passwordEncoder(){
        System.out.println("PasswordEncoder bean is created");
        return new BCryptPasswordEncoder();
    }

    //1. SecurityConfig는 CustomOAuth2UserService를 참조하고 있다.
    //2. CustomOAuth2UserService는 PasswordEncoder를 참조하고 있다.
    //3. PasswordEncoder의 Bean은 SecurityConfig 내부에서 등록된다.

    // CustomOAuth2UserService가 PasswordEncoder를 참조할 때,
    // PasswordEncoder이 등록된 SecurityConfig 클래스를 참조함.
    // SecurityConfig는 CustomOAuth2UserService를 참조(1),
    // CustomOAuth2UserService는 SecurityConfig를 참조(3)하면서 순환 참조가 발생한다.

    // PasswordEncoder을 빈에 등록하는 클래스를 따로 만들면 해결.

}
