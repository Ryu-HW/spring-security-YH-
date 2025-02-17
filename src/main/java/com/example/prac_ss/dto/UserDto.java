package com.example.prac_ss.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Integer id;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String loginFrom;

}
