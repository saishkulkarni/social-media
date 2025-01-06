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

import com.s13sh.social_media.dto.Post;
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
    public String home(HttpSession session,ModelMap map) {
        return userService.loadHome(session,map);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        return userService.logout(session);
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, ModelMap map) {
        return userService.loadProfile(session, map);
    }

    @GetMapping("/profile/edit/{id}")
    public String editProfile(@PathVariable("id") int id, ModelMap map, HttpSession session) {
        return userService.loadEditProfile(id, map, session);
    }

    @PostMapping("/profile/update")
    public String updateProfile(User user, HttpSession session) throws IOException {
        return userService.updateProfile(user, session);
    }

    @PostMapping("/post/create")
    public String createPost(HttpSession session,Post post) throws Exception{
        return userService.createPost(session, post);
    }

    @GetMapping("/post/delete/{id}")
    public String deletePost(@PathVariable int id, HttpSession session) {
        return userService.deletePost(id, session);
    }

    @GetMapping("/post/edit/{id}")
    public String editPost(@PathVariable int id, ModelMap map, HttpSession session) {
        return userService.editPost(id, map, session);
    }

    @PostMapping("/post/update")
    public String updatePost(Post post, HttpSession session) throws IOException {
        return userService.updatePost(post, session);
    }

    @GetMapping("/followers")
    public String loadFollowers(HttpSession session, ModelMap map) {
        return userService.loadFollowers(session, map);
    }

    @GetMapping("/following")
    public String loadFollowing(HttpSession session, ModelMap map) {
        return userService.loadFollowing(session, map);
    }

    @GetMapping("/follow/{id}")
    public String follow(@PathVariable int id, HttpSession session) {
        return userService.follow(id, session);
    }

    @GetMapping("/unfollow/{id}")
    public String unfollow(@PathVariable int id, HttpSession session) {
        return userService.unfollow(id, session);
    }

    @GetMapping("/profile/{id}")
    public String loadProfile(@PathVariable int id, ModelMap map, HttpSession session) {
        return userService.loadProfile(id, map, session);
    }

}