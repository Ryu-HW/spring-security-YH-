package com.example.prac_ss.service;

import com.example.prac_ss.dto.CustomOAuth2User;
import com.example.prac_ss.dto.NaverResponse;
import com.example.prac_ss.dto.OAuth2Response;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    //로그인 후 데이터를 잡아오는 클래스
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        //resultcode=00, message=success, response={id=123123123, name=개호주}
        if (registrationId.equals("naver")){

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());


        //resultcode=00, message=success, id=123123123, name=개호주
        }else if(registrationId.equals("google")){

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }else {

            return null;
        }

        //뽑아온 데이터 사용

        String role = "ROLE_USER";

        return new CustomOAuth2User(oAuth2Response, role);
    }
}
