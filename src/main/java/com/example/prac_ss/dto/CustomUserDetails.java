package com.example.prac_ss.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    //객체를 받아올 수 있는 필드 생성
    private final UserDto userDto; //final에대해 공부해야함
    private final List<String> roles;


    //Dto객체로 생성했을 때의 생성자
    public CustomUserDetails(UserDto userDto,List<String> roles){
        this.userDto = userDto;
        this.roles = roles; //userDto에 해당하는 id값으로, users_roles를 통해 role의 권한String을 리스트로 받아옴.
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();


        for(String role : roles){
            collection.add(new SimpleGrantedAuthority(role));
        }

//        for(String role : roles){
//            collection.add(new GrantedAuthority() {
//
//                @Override
//                public String getAuthority() {
//                    return role;
//                }
//
//            });
//        }


        return collection;
    }

    @Override
    public String getPassword() {
        return userDto.getPassword();
    }

    @Override
    public String getUsername() {
        return userDto.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
