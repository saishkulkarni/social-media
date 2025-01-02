package com.s13sh.social_media.service;

import java.io.IOException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.s13sh.social_media.dto.User;
import com.s13sh.social_media.helper.AES;
import com.s13sh.social_media.helper.Cloudinaryhelper;
import com.s13sh.social_media.helper.MyEmailSender;
import com.s13sh.social_media.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MyEmailSender emailSender;

    @Autowired
    Cloudinaryhelper cloudinaryhelper;

    public String loadRegister(ModelMap map, User user) {
        map.put("user", user);
        return "register.html";
    }

    public String register(User user, BindingResult result, HttpSession session) throws Exception {
        if (!user.getPassword().equals(user.getConfirmpassword()))
            result.rejectValue("confirmpassword", "error.confirmpassword",
                    "Password and Confirm Password does not match");
        if (userRepository.existsByEmail(user.getEmail()))
            result.rejectValue("email", "error.email", "Email already exists");
        if (userRepository.existsByUsername(user.getUsername()))
            result.rejectValue("username", "error.username", "Username already exists");
        if (userRepository.existsByMobile(user.getMobile()))
            result.rejectValue("mobile", "error.mobile", "Mobile number already exists");

        if (result.hasErrors()) {
            return "register.html";
        } else {
            user.setOtp(new Random().nextInt(100000, 1000000));
            System.err.println(user.getOtp());
            user.setPassword(AES.encrypt(user.getPassword()));
            user.setProfilepictureurl(cloudinaryhelper.uploadProfilePicture(user.getProfilepicture().getBytes()));
            userRepository.save(user);
            emailSender.sendOtp(user);
            session.setAttribute("success", "Otp Sent Success");
            return "redirect:/verify-otp/" + user.getId();
        }
    }

    public String verifyOtp(int id, ModelMap map, HttpSession session) {
        map.put("id", id);
        return "verify-otp.html";
    }

    public String verifyOtp(int id, int otp, HttpSession session) throws IOException {
        User user = userRepository.findById(id).orElseThrow();
        if (user.getOtp() == otp) {
            user.setOtp(0);
            user.setVerified(true);
            userRepository.save(user);
            session.setAttribute("success", "Registration Success");
            return "redirect:/login";
        } else {
            session.setAttribute("error", "Invalid Otp, Try Again!");
            return "redirect:/verify-otp/" + id;
        }
    }

    public String resendOtp(int id, HttpSession session) {
        User user = userRepository.findById(id).orElseThrow();
        user.setOtp(new Random().nextInt(100000, 1000000));
        System.err.println(user.getOtp());
        userRepository.save(user);
        emailSender.sendOtp(user);
        session.setAttribute("success", "Otp Re-Sent Success");
        return "redirect:/verify-otp/" + id;
    }

    public String login(String username, String password, HttpSession session) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            session.setAttribute("error", "Invalid Username");
            return "redirect:/login";
        } else {
            if (AES.decrypt(user.getPassword()).equals(password)) {
                if (user.isVerified()) {
                    session.setAttribute("user", user);
                    session.setAttribute("success", "Login Success");
                    return "redirect:/home";
                } else {
                    session.setAttribute("error", "Please Verify Your Account First");
                    user.setOtp(new Random().nextInt(100000, 1000000));
                    System.err.println(user.getOtp());
                    userRepository.save(user);
                    emailSender.sendOtp(user);
                    session.setAttribute("success", "Otp Re-Sent Success");
                    return "redirect:/verify-otp/" + user.getId();
                }
            } else {
                session.setAttribute("error", "Invalid Password");
                return "redirect:/login";
            }
        }
    }

    public String loadHome(HttpSession session) {
        if (session.getAttribute("user") == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            return "home.html";
        }
    }

    public String logout(HttpSession session) {
        session.removeAttribute("user");
        session.setAttribute("success", "Logout Success");
        return "redirect:/login";
    }

    public String loadProfile(HttpSession session, ModelMap map) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            map.put("user", user);
            return "profile.html";
        }
    }

}
