package com.rabin.securityproject.service;

import com.rabin.securityproject.dto.AuthenticateRequest;
import com.rabin.securityproject.dto.AuthenticateResponse;
import com.rabin.securityproject.dto.PasswordResetDto;
import com.rabin.securityproject.dto.UserDto;

import java.util.List;

public interface UserService {
    AuthenticateResponse registrationOfUserInfo(UserDto userDto) throws Exception;

    AuthenticateResponse authenticate(AuthenticateRequest request);

    List<UserDto> getAllRecord();

    UserDto getRecordByUsername(String username);

    String deleteRecord(String username) throws Exception;

    UserDto updatinguserInforamtion(UserDto userDto, String username) throws Exception;


    boolean veifyEmailToken(String token);

    AuthenticateResponse passwordResetRequest(String email) throws Exception;


    boolean resetPassword(String token, PasswordResetDto passwordResetDto);
}