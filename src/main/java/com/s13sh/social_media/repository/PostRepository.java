package com.s13sh.social_media.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.s13sh.social_media.dto.Post;
import com.s13sh.social_media.dto.User;

public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByUser(User user);

    List<Post> findByUserIn(List<User> following);

}
