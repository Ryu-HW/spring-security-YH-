package com.example.prac_ss.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {

    private Integer id;
    private String email;
    private String password;

//    private List<Role> roles;
}
