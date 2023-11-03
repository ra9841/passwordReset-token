package com.rabin.securityproject.service;

import com.rabin.securityproject.dto.AuthenticateRequest;
import com.rabin.securityproject.dto.AuthenticateResponse;
import com.rabin.securityproject.dto.PasswordResetDto;
import com.rabin.securityproject.dto.UserDto;

import com.rabin.securityproject.entity.UserInfo;

import com.rabin.securityproject.repository.UserRepository;
import com.rabin.securityproject.utils.EmailUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailUtils emailUtils;

    @Override
    public AuthenticateResponse registrationOfUserInfo(UserDto userDto) throws Exception {
        Optional<UserInfo> existuser = userRepository.findByUsername(userDto.getUsername());
        if (existuser.isPresent()) {
            throw new Exception("user already present");
        } else {
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(userDto, userInfo);
            userInfo.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userInfo.setGeneratedTime(LocalDateTime.now());

            String jwtToken = jwtService.generateToken(userInfo);
            userInfo.setEmailVerificationsToken(jwtToken);
            userRepository.save(userInfo);

            try {
                emailUtils.sendTokenEmail(userDto.getEmail(), jwtToken);
            } catch (Exception e) {
                throw new RuntimeException("Unable to send token,plz try again");
            }

//        return AuthenticateResponse.builder()
//                .token(jwtToken)
//                .build();
            return new AuthenticateResponse(jwtToken);
        }
    }

    @Override
    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        Optional<UserInfo> existUser = userRepository.findByUsername(request.getUsername());
        if (existUser.isPresent()) {
            String jwtToken = jwtService.generateToken(existUser.get());
            return new AuthenticateResponse(jwtToken);
        } else {
            return new AuthenticateResponse("User not found");
        }

    }

    @Override
    public List<UserDto> getAllRecord() {
        return userRepository.findAll().stream()
                .map(userInfo -> {
                    UserDto userDto = new UserDto();
                    BeanUtils.copyProperties(userInfo, userDto);
                    return userDto;
                }).collect(Collectors.toList());

    }

    @Override
    public UserDto getRecordByUsername(String username) {
        Optional<UserInfo> existUser = userRepository.findByUsername(username);
        if (existUser.isPresent()) {
            UserInfo userInfo = existUser.get();
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userInfo, userDto);
            return userDto;
        } else {
            throw new UsernameNotFoundException("User not found");
        }

    }

    @Override
    public String deleteRecord(String username) throws Exception {
        Optional<UserInfo> existUser = userRepository.findByUsername(username);
        if (existUser.isPresent()) {
            UserInfo userInfo = existUser.get();
            userRepository.delete(userInfo);
            return "delete successfully";
        } else {
            throw new Exception("user not found....");
        }

    }

    @Override
    public UserDto updatinguserInforamtion(UserDto userDto, String username) throws Exception {
        Optional<UserInfo> existUser = userRepository.findByUsername(username);
        if (existUser.isPresent()) {
            UserInfo userInfo = existUser.get();
            userInfo.setEmail(userDto.getEmail());
            userInfo.setPassword(userDto.getPassword());
            userInfo.setRoles(userDto.getRoles());
            userInfo.setUsername(userDto.getUsername());
            userRepository.save(userInfo);
            UserDto userDto1 = new UserDto();
            BeanUtils.copyProperties(userInfo, userDto1);
            return userDto1;
        } else {
            throw new Exception("user not found....");
        }

    }

    @Override
    public boolean veifyEmailToken(String token) {
        boolean returnValue = false;

        UserInfo userInfo = userRepository.findByEmailVerificationsToken(token);

        if (userInfo != null) {
            boolean hasTokenExpired = jwtService.isTokenExpired(token);

            if (!hasTokenExpired) {
                userInfo.setEmailVerificationsToken(null);
                userInfo.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userInfo);
                returnValue = true;


            }
        }

        return returnValue;
    }

    @Override
    public AuthenticateResponse passwordResetRequest(String email) throws Exception {
        Optional<UserInfo> existEmail=userRepository.findByEmail(email);
        if(existEmail.isPresent()){
            UserInfo userInfo=existEmail.get();
            String jwtToken = jwtService.generateToken(userInfo);
            userInfo.setEmailVerificationsToken(jwtToken);
            userRepository.save(userInfo);

            try {
                emailUtils.sendTokenForPasswordReset(email, jwtToken);
            } catch (Exception e) {
                throw new RuntimeException("Unable to send token,plz try again");
            }

            return new AuthenticateResponse(jwtToken);

        }else{
            throw new Exception("unable to find email");
        }

    }

    @Override
    public boolean resetPassword(String token, PasswordResetDto passwordResetDto) {
        boolean returnValue = false;

        if(jwtService.isTokenExpired(token)){
            return returnValue;
        }else{
            UserInfo userInfo=userRepository.findByEmailVerificationsToken(token);

            if(userInfo==null){
                return returnValue;
            }else{
                userInfo.setPassword(passwordEncoder.encode(passwordResetDto.getPassword()));
                userInfo.setEmailVerificationsToken(null);
                userRepository.save(userInfo);

                returnValue = true;
            }
        }
        return returnValue;
    }


}
