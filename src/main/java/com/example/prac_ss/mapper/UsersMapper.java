package com.example.prac_ss.mapper;

import com.example.prac_ss.dto.Role;
import com.example.prac_ss.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UsersMapper {

    public void signUp(UserDto userDto);
    public List<Role> selectRoles(int limit);
    public void insertUserRole(int userId, int roleId);
    public String selectUserEmailByEmail(String email);


}
