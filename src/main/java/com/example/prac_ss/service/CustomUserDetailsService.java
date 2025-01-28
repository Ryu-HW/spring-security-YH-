package com.example.prac_ss.service;

import com.example.prac_ss.dto.CustomUserDetails;
import com.example.prac_ss.dto.Role;
import com.example.prac_ss.dto.UserDto;
import com.example.prac_ss.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UsersMapper usersMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDto userDto = usersMapper.selectUserByEmail(username);

        if(userDto != null){
            List<Role> rolesDto = usersMapper.selectRolesByUserId(userDto.getId());

            //역할 이름만 추출해서 roles 리스트로 반환.
            List<String> roles = rolesDto.stream()
                    .map(Role::getRole)//각Role객체의 getRole매서드를 이용해서 값 추출
                    .collect(Collectors.toList()); //모아서 list로 리턴

//            List<String> roles = new ArrayList<>();
//
//            for (int i = 0; i < rolesDto.size(); i++) {
//                roles.add(rolesDto.get(i).getRole());
//            }

            return new CustomUserDetails(userDto,roles);
        }

        return null; //가입한 회원정보가 없음.
    }

}
