package com.s13sh.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.s13sh.social_media.dto.Like;

public interface LikeRepository extends JpaRepository<Like, Integer> {

}
