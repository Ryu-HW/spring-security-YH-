package com.example.prac_ss.component;

import com.example.prac_ss.dto.Role;
import com.example.prac_ss.dto.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

//    @Value("${jwt.expiration-time}")// 보안성을 위해 환경변수로 관리 권장
//    private long EXPIRATION_TIME;    // 1일 (ms)

    @Value("${jwt.access-token-expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token-expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    // Claims(?) 추출
    // 클래임이란
    // jwt는 헤더 페이로드(본문) 서명으로 나뉘는데 페이로드에 클레임이 저장돼있음
    // 클레임은 jwt에 조회가능한 정보, 페이로드는 그 정보를 담은 바디(본문)
    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        //파서는 직역하자면 구문 분석기. jwt를 분석한다.
        //같은 key를 이용해 분석후 분석한 값의 페이로드(본문)을 가져옴. Claims 객체로.
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //유저네임, 권한, 시간, 키 등으로 토큰을 만드는 메서드
    public String generateToken(String username, List<String> roles, long expirationTime) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        String rolesStr = String.join(",", roles);

        return Jwts.builder()
                .setSubject(username) //유저 이름을 페이로드의 sub으로 삽입
                .claim("roles", rolesStr) //rolesStr을 페이로드의 roles로 삽임
                .setIssuedAt(new Date()) //발행시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) //만료시간 설정
                .signWith(key) //key로 서명
                .compact(); //콤팩트 !
    }

    // Access Token 생성
    public String generateAccessToken(String username, List<String> roles) {
        return generateToken(username, roles, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    // Refresh Token 생성 (role 없이)
    public String generateRefreshToken(String username) {
        return generateToken(username, Collections.emptyList(), REFRESH_TOKEN_EXPIRATION_TIME);
    }

    // JWT 토큰에서 사용자이름 추출
    public String extractUsername(String token) {
        return getClaimsFromToken(token).getSubject(); //해당 토큰의 페이로드에서 Sub정보 가져오기 'username'으로 만들어놓음
    }

    // JWT 토큰에서 권한 정보 추출
    public List<String> extractRoles(String token) {

        //roles를 가져오는데, 해당 페이로드안에 클레임인 roles가 스트링타입으로 받아오게 해주는 메서드
        String roles =  getClaimsFromToken(token).get("roles", String.class);

        return Arrays.stream(roles.split(","))
                .map(UserRole::fromRoleName)
                .collect(Collectors.toList());

//        List<String> roleList = new ArrayList<>();
        //","를 기점으로 나누어서 스트링배열로 만듬.
//        String[] roleArray = roles.split(",");
//
         //배열만큼 돌면서
//        for (String role : roleArray) {
            //enum 클래스객체는 임포트하지 않아도 같은 페키지 내에 있으면 사용 가능 (UserRole.fromRoleName(role)과 같이.
            //아래 메서드는 user 나 admin 문자열을 반환함
            //반환한 걸 List<String> 배열객체로 저장
//            roleList.add(UserRole.fromRoleName(role));
//        }
//        배열객체 반환
//        return roleList;

    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        //해당토큰의 만료시간을 받고 현 시간보다 전이면 false반환
        return !getClaimsFromToken(token).getExpiration().before(new Date());
    }


}
