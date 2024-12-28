package com.s13sh.social_media.helper;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Service
public class MessageRemover {

    public void remove() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        @SuppressWarnings("null")
        HttpSession session = attributes.getRequest().getSession();
        session.removeAttribute("success");
        session.removeAttribute("error");
    }
}
