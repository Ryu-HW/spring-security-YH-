package com.example.prac_ss.controller;

import com.example.prac_ss.dto.UserDto;
import com.example.prac_ss.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;
import java.util.Iterator;

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

    @GetMapping("/admin")
    public String adminPage(){
        return "/admin";
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

    @GetMapping("/my/info")
    public String myPage(Model model){

        //세션의 현재 사용자 id, 현 프로젝트에서는 email
        String id = SecurityContextHolder.getContext().getAuthentication().getName();

        //세션의 현재 사용자의 role ROLE_USER or ROLE_ADMIN
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();
        //하나의 role만 가져오는 코드. 이코드가 아니어도 id값으로 원하는 정보를 가져오는 로직을 service에서 짜면 됨.


        model.addAttribute("id",id);
        model.addAttribute("role",role);

        return "/mypage";
    }


    //get메서드 로그아웃 코드
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return "redirect:/";
    }

}
