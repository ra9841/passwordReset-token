package com.rabin.securityproject.repository;

import com.rabin.securityproject.entity.UserInfo;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo,Integer> {

    Optional<UserInfo> findByUsername(String username);


    UserInfo findByEmailVerificationsToken(String token);

    Optional<UserInfo> findByEmail(String email);
}
