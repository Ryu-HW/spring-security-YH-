package com.example.prac_ss.dto;

//OAuth2로 타 API서버에서 받아온 Response의 정보를 받아오는 메서드를 정의한
public interface OAuth2Response {

    String getProvider();

    String getProviderId();

    String getEmail();

    String getName();

    String getMobile();
}
