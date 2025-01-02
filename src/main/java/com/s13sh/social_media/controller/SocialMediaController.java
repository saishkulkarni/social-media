package com.s13sh.social_media.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.s13sh.social_media.dto.User;
import com.s13sh.social_media.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class SocialMediaController {

    @Autowired
    UserService userService;

    @GetMapping({ "/", "/login" })
    public String loadLogin() {
        return "login.html";
    }

    @GetMapping("/register")
    public String loadRegister(ModelMap map, User user) {
        return userService.loadRegister(map, user);
    }

    @PostMapping("/register")
    public String register(@Valid User user, BindingResult result, HttpSession session) throws Exception {
        return userService.register(user, result, session);
    }

    @GetMapping("/verify-otp/{id}")
    public String verifyOtp(@PathVariable("id") int id, ModelMap map, HttpSession session) {
        return userService.verifyOtp(id, map, session);
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam int id, @RequestParam int otp, HttpSession session) throws IOException {
        return userService.verifyOtp(id, otp, session);
    }

    @GetMapping("/resend-otp/{id}")
    public String resendOtp(@PathVariable("id") int id, HttpSession session) {
        return userService.resendOtp(id, session);
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        return userService.login(username, password, session);
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        return userService.loadHome(session);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        return userService.logout(session);
    }
    
    @GetMapping("/profile")
    public String profile(HttpSession session, ModelMap map) {
        return userService.loadProfile(session, map);
    }
}
