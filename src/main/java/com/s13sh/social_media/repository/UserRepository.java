package com.s13sh.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.s13sh.social_media.dto.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByMobile(long mobile);

    User findByUsername(String username);

}
