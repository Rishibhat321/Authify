package com.fullstack.authify.service.impl;

import com.fullstack.authify.entity.UserEntity;
import com.fullstack.authify.io.ProfileRequest;
import com.fullstack.authify.io.ProfileResponse;
import com.fullstack.authify.repository.UserRepository;
import com.fullstack.authify.service.EmailService;
import com.fullstack.authify.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final EmailService emailService;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        // convert request to entity
        UserEntity newProfile = convertToUserEntity(request);

        // If user is creating account for the first time
        if(!userRepository.existsByEmail(request.getEmail())) {
            newProfile = userRepository.save(newProfile);
            // convert to profile response
            return convertToProfileResponse(newProfile);
        }

        // else throw an error if duplicate email
        // 409 conflict status
        // 1. Specify a custom http status code. 2. Custom error message.
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");

        
    }


    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
        return ProfileResponse.builder()
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }


    private UserEntity convertToUserEntity(ProfileRequest request) {
        // using builder pattern

      return  UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
             //   .password(request.getPassword())
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();
    }

    @Override
    public ProfileResponse getProfile(String email) {
       UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + email));

       // convert to profile Response
        return convertToProfileResponse(existingUser);

    }

    @Override
    public void sendResetOtp(String email) {

        // First get the existing profile
        UserEntity existingEntity =  userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + email));

        // Generate 6-digit OTP
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        // Calculate expiry time (current time + 15 minutes in milliseconds
        long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);

        // Update the profile/user
        existingEntity.setResetOtp(otp);
        existingEntity.setResetOtpExpireAt(expiryTime);

        // Save into the database
        userRepository.save(existingEntity);


        try{
             // TODO: send the reset otp email
            emailService.sendResetOtpEmail(existingEntity.getEmail(), otp);
        }
        catch(Exception ex) {
            throw new RuntimeException("Unable to send email");
        }

    }


}
