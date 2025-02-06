package com.example.prac_ss.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

//    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
//        this.jwtUtil = jwtUtil;
//        this.userDetailsService = userDetailsService;
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        //헤더에 요청이 있는지 확인하는 코드 쿠키에서 바로 확인도 가능
        final String authHeader = request.getHeader("Authorization");

        //권한이 잘 있고 Bearer로 시작된다면 true
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            //jwt토큰의 액기스만 추출, "Bearer (jwt토큰)" 식이라 7번째 char부터 추출한다는 의미
            String jwtToken = authHeader.substring(7);
            //jwt의 토큰의 액기스를 이용해 username추출
            String username = jwtUtil.extractUsername(jwtToken);

            //username이 있고, 해당 토큰이 현재 사용자의 인증정보, SecurityContext가 없을 때
            //SecurityContext란, Spring Security 필터에 해당 토큰에대한 정보
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                //username을 통해 권한객체 생성(권한, 만료여부 등등) [!중요]CustomUserDetailService실행
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                //토큰이 유효한지 검사, jwtUtill을 통해 만료 혹은 위조 검사
                if (jwtUtil.validateToken(jwtToken)) {
                    //검사 완료시 해당토큰의 인증정보를 UsernamePasswordAuthenticationToken 를 통해 생성
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    //해당토큰의 인증정보를 Spring Sequrity에 등록
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
