package com.example.prac_ss.service;

import com.example.prac_ss.dto.CustomOAuth2User;
import com.example.prac_ss.dto.NaverResponse;
import com.example.prac_ss.dto.OAuth2Response;
import com.example.prac_ss.dto.UserDto;
import com.example.prac_ss.mapper.UsersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
//SecurityContext 에 유저 정보가 담기기 전 실행되는 메서드
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsersMapper usersMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    //타서버API에서 로그인 인증 후 받아온 유저 정보(OAuth2UserRequest userRequest)를 갖고 커스텀하는 클래스
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //CustomOAuth2UserService의 부모클래스인 DefaultOAuth2UserService의 메서드 loadUser를 가져와서 OAuth2User에 담음
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //oAuth2User객체의 속성을 가져와서 보여줌 (형식은 아래에 주석으로 달음)
        System.out.println(oAuth2User.getAttributes());

        //받아온 유저 정보의 출처를 뽑아내는 코드
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        //resultcode=00, message=success, response={id=123123123, name=개호주}
        if (registrationId.equals("naver")){

            //oAuth2Response 추상 클래스를 통해 구현한 NaverResponse 클래스에 oAuth2User의 속성을 가져와서 객체 값 삽입
            //NaverResponse를 구현한 이유는, API마다 보내오는 속성데이터의 타입이 다르기 때문
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());


        //resultcode=00, message=success, id=123123123, name=개호주
        }else if(registrationId.equals("google")){

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }else {

            return null;
        }
        //로그인시 데이터를 이미 로그인했던 사용자면, 값을 업데이트 해주고
        //로그인하지 않았던 사용자면 db에 값을 추가 할 예정.

        //API를 받아온 서버이름,고유유저ID
        String loginFrom = oAuth2Response.getProvider()+","+oAuth2Response.getProviderId();

        //랜덤 패스워드값 생성
        String randomPassword = bCryptPasswordEncoder.encode(generateRandomPassword(10));

        //유저 데이터 Dto에 세팅
        UserDto userDto = new UserDto();
        userDto.setEmail(oAuth2Response.getEmail());
        userDto.setPhoneNumber(oAuth2Response.getMobile());
        userDto.setUsername(oAuth2Response.getName());

        //데이터가 저장돼있지 않으면 (LoginFrom으로 확인)
        if(usersMapper.selectLoginFromByLoginFrom(loginFrom) == null){

            //데이터 추가 세팅
            userDto.setLoginFrom(loginFrom);
            userDto.setPassword(randomPassword);

            //회원 정보 저장
            usersMapper.signUp(userDto);

            //회원 ROLE_USER권한 삽입
            usersMapper.insertUserRole(userDto.getId(),1);

        //저장 돼있으면
        }else{

            //회원 정보 업데이트
            usersMapper.updateUser(userDto);

        }

        //유저 정보를 받아서 권한을 collection으로 만들어 반환하거나 하면 됨
        List<String> roles = usersMapper.selectUserRoles(userDto.getId());

        //유저정보를 받아와서 데이터를 뽑아서 CustomOAuth2User 생성(생성자), 후 세션에 등록함
        return new CustomOAuth2User(oAuth2Response, roles);
    }

    public String generateRandomPassword(int length) {
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charset.length());
            password.append(charset.charAt(index));
        }
        return password.toString();
    }
}
