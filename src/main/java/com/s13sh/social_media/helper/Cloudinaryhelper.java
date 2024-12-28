package com.s13sh.social_media.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class Cloudinaryhelper {

    @Value("${cloudinary.properties.api_key}")
    private String key;
    @Value("${cloudinary.properties.secret_key}")
    private String secret;
    @Value("${cloudinary.properties.cloud_name}")
    private String cloudname;

    public String uploadProfilePicture(byte[] bytes) {
        Cloudinary cloudinary = new Cloudinary("cloudinary://" + key + ":" + secret + "@" + cloudname);
        try {
            return cloudinary.uploader().upload(bytes, ObjectUtils.asMap("folder", "profile_pictures")).get("url")
                    .toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
