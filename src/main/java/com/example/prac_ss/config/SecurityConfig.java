package com.example.prac_ss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  //해당 클래스가 Config(설정) 클래스라는 걸 정의하는 어노테이션.
@EnableWebSecurity  // Spring Security설정을 한다는 어노테이션.
public class SecurityConfig {

    @Bean  // 해당 메서드가 빈에 등록된다는 어노테이션.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // http(HttpSecurity) 객체는 보안 관련 설정을 담당하는 객체
        http
                .authorizeHttpRequests(auth -> auth  // HTTP 요청에 대한 접근 권한을 설정합니다.
                        .requestMatchers("/", "/login","/signup").permitAll()  // "/"와 "/login" 경로는 누구나 접근할 수 있도록 허용
                        .requestMatchers("/admin").hasRole("ADMIN")  // "/admin" 경로는 "ADMIN" 역할을 가진 사용자만 접근 가능하게 설정합니다.
                        .requestMatchers("/my/**").hasAnyRole("ADMIN", "USER")  // "/my/**" **은 그 뒤 모든 주소.
                        .anyRequest().authenticated()  // 나머지 모든 요청은 인증된 사용자만 접근할 수 있도록 설정합니다.
                )

                .formLogin(auth -> auth
                        .loginPage("/login") //로그인 페이지를 /login으로 설정한다는 매서드(GetMapping)
                        //loginProcessingUrl은 UserDetailsService를 상속받은 클래스를 실행시키는 중요한 설정.
                        .loginProcessingUrl("/login") //로그인 버튼을(PostMapping) /login으로 연결한다는 의미
                        .permitAll() // 위 경로를 누구나 접근하게 허용
                )

                .logout(auth -> auth
                        .logoutUrl("/logout") //get메서드로 로그아웃 할 수 있게 함
                        .logoutSuccessUrl("/")
                );


        http
                .sessionManagement(auth -> auth
                        .maximumSessions(1) //최대 몇 개의 세션을 만들 수 있는지.
                        .maxSessionsPreventsLogin(true) //true는 새로운 로그인 차단, false는 기존 로그인 세션 삭제
                );

        http
                //로그인 할 때마다 해당 유저의 세션 정보를 변경함(보안)
                .sessionManagement(auth -> auth
                        .sessionFixation().changeSessionId());

        //csrf는 Cross-Site Request Forgery의 약자로 도메인 요청 위조라는 뜻이다.
        //활성화하면 세션을 해킹해서 다른 도메인에 세션정보를 넣고 API서버로 요청했을 때 spring security의존성이 막아준다.
        //근데 개발환경에서는 disable해놓고 사용해야함 아래 코드를 삭제하면 enable상태로 됨.
        //get을 제외한 요청시 위조검사함. get을 제외한 요청에 _csrf.token 데이터를 줘야함.(login.html)확인
//        http.csrf(AbstractHttpConfigurer::disable);
//        http.csrf(csrf -> csrf.disable());



        return http.build();  // SecurityFilterChain으로 HttpSecurity설정 후 반환.
    }

    @Bean
    //BCrypt를 권장한다고 함 spring security가.
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

}