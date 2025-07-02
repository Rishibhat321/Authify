package com.fullstack.authify.service;

import com.fullstack.authify.io.ProfileRequest;
import com.fullstack.authify.io.ProfileResponse;

public interface ProfileService {

    ProfileResponse createProfile(ProfileRequest request);

    ProfileResponse getProfile(String email);

    void sendResetOtp(String email);

    void resetPassword(String email, String otp, String newPassword);

}
