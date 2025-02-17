package com.example.prac_ss.controller;

import com.example.prac_ss.dto.UserDto;
import com.example.prac_ss.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsersController {

    @Autowired
    UsersService usersService;

    @GetMapping("/")
    public String mainPage(){
        return "main";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "/login";
    }

//    @PostMapping("/login")
//    public String loginForm(@ModelAttribute UserDto userDto){
//
//        usersService.signUp(userDto);
//
//        return "redirect:/login";
//    }

    @GetMapping("/admin")
    public String adminPage(){
        return "/admin";
    }

    @GetMapping("/my/page")
    public String mypage(){
        return "/mypage";
    }

    @GetMapping("/signup")
    public String signup(){
        return "/signup";
    }

    @PostMapping("/signup")
    public String signupForm(@ModelAttribute UserDto userDto){

        usersService.signUp(userDto);

        return "redirect:/login";
    }


}
