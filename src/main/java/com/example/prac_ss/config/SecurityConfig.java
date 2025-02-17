package com.example.prac_ss.config;

import com.example.prac_ss.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  //해당 클래스가 Config(설정) 클래스라는 걸 정의하는 어노테이션.
@EnableWebSecurity  // Spring Security설정을 한다는 어노테이션.
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;


    @Bean  // 해당 메서드가 빈에 등록된다는 어노테이션.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        // http(HttpSecurity) 객체는 보안 관련 설정을 담당하는 객체
        http
                .authorizeHttpRequests(auth -> auth  // HTTP 요청에 대한 접근 권한을 설정합니다.
                        .requestMatchers("/","/oauth2/**", "/login/**","/signup").permitAll()  // "/"와 "/login" 경로는 누구나 접근할 수 있도록 허용
                        .requestMatchers("/admin").hasRole("ADMIN")  // "/admin" 경로는 "ADMIN" 역할을 가진 사용자만 접근 가능하게 설정합니다.
//                        .requestMatchers("/my/**").hasAnyRole("ADMIN", "USER")  // "/my/**" **은 그 뒤 모든 주소.
                        .anyRequest().authenticated()  // 나머지 모든 요청은 인증된 사용자만 접근할 수 있도록 설정합니다.
                )

//                .formLogin(auth -> auth
//                        .loginPage("/login")
//                        .loginProcessingUrl("/loginForm")
//                        .permitAll()
//                );
                .formLogin(login-> login.disable())

                .httpBasic(basic -> basic.disable())


                //OAuth2Login설정, API서버에서 인증을 마치고(인증안되면 실행안됨), SecurityContext 에 유저 정보가 담기기 전
                //해당 유저의 정보를 반환한 마지막 지점(endpoint)의 설정을 내가 만든 클래스로 대체하는 코드
                .oauth2Login(oauth2->oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)));

        http.csrf(AbstractHttpConfigurer::disable);
//        http.csrf(csrf -> csrf.disable());



        return http.build();  // SecurityFilterChain으로 HttpSecurity설정 후 반환.
    }

    @Bean
    //BCrypt를 권장한다고 함 spring security가.
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

}