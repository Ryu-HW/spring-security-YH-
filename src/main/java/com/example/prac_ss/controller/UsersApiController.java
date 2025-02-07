package com.example.prac_ss.controller;

import com.example.prac_ss.component.JwtUtil;
import com.example.prac_ss.dto.CustomUserDetails;
import com.example.prac_ss.dto.UserDto;
import com.example.prac_ss.service.CustomUserDetailsService;
import com.example.prac_ss.service.UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@ResponseBody
public class UsersApiController {

    @Autowired
    UsersService usersService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Value("${jwt.refresh-token-expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @PostMapping("/signup")
    //ResponseEntity는 200이나 400번대 500번대 요청을 다룰 수 있고, Map<String, String>는 제이슨형식의 데이터형태이다.
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
    public ResponseEntity<Map<String, String>> login (@RequestBody Map<String, String> user, HttpServletResponse response) {
        try {
            // AuthenticationManager.authenticate()가 호출될 때 CustomUserDetailService 실행
            //AuthenticationManager는 Spring Sequrity 인증관리 객체
            //UsernamePasswordAuthenticationToken는 username과 password를 받아서 인증확인
            //authenticate 메서드가 CustomUserDetailService를 username으로 실행시킴 SecurityConfig를 보면 login경로설정을 주석화해놓음
            //그렇게 생성한 UserDetails와 어려것들을 이용해 Authentication(인증객체)생성
            //SecurityContext(api의 토큰인증 정보)에 해당토큰의 인증정보도 추가함
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.get("username"), user.get("password"))
            );

            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

//            String username =authentication.getName();

//            String accessToken = jwtUtil.generateAccessToken(username, roles);
//            String refreshToken = jwtUtil.generateRefreshToken(username);
            //accessToken은 로컬스토리지, refreshToken은 쿠키에 저장하고 평소엔 accessToken으로 인증하다
            //accessToken이 만료돼서 401에러메시지를 반환하면 클라이언트서버에서 refreshToken를 갖고 @PostMapping("/refresh")를 요청한다

            //여러 정보를 이용해서 토큰생성
            String token = jwtUtil.generateToken(authentication.getName(),roles,REFRESH_TOKEN_EXPIRATION_TIME);
            System.out.println("실험");

//            //토큰형태를 맵으로 생성후 반환      {
//            //                                   "jwtToken" : (토큰문자열)
//            //                                }
//            return Map.of("jwtToken", token);

            // JWT 토큰을 HttpOnly 쿠키로 설정
            Cookie jwtTokenCookie = new Cookie("jwtToken", token);
            jwtTokenCookie.setHttpOnly(true);  // 클라이언트 측 JavaScript에서 접근 불가
            jwtTokenCookie.setPath("/");       // 쿠키의 경로 설정 ("/"는 모든 경로에 대해 유효)
            jwtTokenCookie.setMaxAge(3600);   // 쿠키 만료 시간 (1시간)
            // 쿠키를 응답에 추가
            response.addCookie(jwtTokenCookie);

            // 토큰이 쿠키에 저장되었으므로, 클라이언트는 별도로 토큰을 응답 본문에서 받지 않아도 됨
            return ResponseEntity.ok(Map.of("jwtToken", token));

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //권한 확인 코드
        if (authentication != null) {
            System.out.println("Authenticated: " + authentication.isAuthenticated());
            System.out.println("Principal: " + authentication.getPrincipal());
            System.out.println("Credentials: " + authentication.getCredentials());
            System.out.println("Authorities: ");

            for (GrantedAuthority authority : authentication.getAuthorities()) {
                System.out.println(" - " + authority.getAuthority());
            }
        } else {
            System.out.println("No authentication found in SecurityContext.");
        }

        //권한에서 username 받아오기
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();  // CustomUserDetails에서 username을 가져옵니다.


        // 응답 데이터로 username을 포함한 Map 객체를 반환
        Map<String, String> response = new HashMap<>();
        response.put("username", username);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        // 쿠키 삭제
        Cookie cookie = new Cookie("jwtToken", null);  // 쿠키 이름은 jwtToken, 값을 null로 설정
        cookie.setMaxAge(0);  // 쿠키 만료 시간 0으로 설정
        cookie.setPath("/");  // 쿠키 경로 설정 (보통은 "/"로 설정)
        cookie.setHttpOnly(true);  // HttpOnly 설정 (자바스크립트에서 접근 불가)

        response.addCookie(cookie);  // 응답에 쿠키 추가


        String message = "로그아웃 되었습니다";

        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> tokens) {
        String refreshToken = tokens.get("refreshToken");

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Refresh Token"));
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String newAccessToken = jwtUtil.generateAccessToken(username, roles);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

}
