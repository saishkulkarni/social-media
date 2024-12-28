package com.s13sh.social_media.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.s13sh.social_media.dto.User;

import jakarta.mail.internet.MimeMessage;

@Service
public class MyEmailSender {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    TemplateEngine templateEngine;

    public void sendOtp(User user) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setFrom("saishkulkarni7@gmail.com", "s13sh- APP");
            helper.setTo(user.getEmail());
            helper.setSubject("OTP Verification");
            Context context = new Context();
            context.setVariable("otp", user.getOtp());
            context.setVariable("username", user.getUsername());
            context.setVariable("name",user.getFirstname());

            String htmlContent = templateEngine.process("otp.html", context);

            helper.setText(htmlContent, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mailSender.send(message);
    }

}