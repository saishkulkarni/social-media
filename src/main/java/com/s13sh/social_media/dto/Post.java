package com.s13sh.social_media.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String caption;
    @Transient
    private MultipartFile image;
    private String imageUrl;   
    @UpdateTimestamp
    private LocalDateTime postedAt;

    @ManyToOne
    private User user;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Like> likes=new ArrayList<>();    

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Comment> comments=new ArrayList<>();

    public boolean check(int id){
        for(Like like:this.likes){
            if(like.getUser().getId()==id){
                return true;
            }
        }
        return false;
    }

}
