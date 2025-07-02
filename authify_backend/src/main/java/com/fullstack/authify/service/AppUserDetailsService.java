package com.fullstack.authify.service;

import com.fullstack.authify.entity.UserEntity;
import com.fullstack.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    // Load user-specific data from db when user logs in
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found for the email : " + email));

       // for roles, we initially passes an empty arraylist
        return new User(existingUser.getEmail(), existingUser.getPassword(), new ArrayList<>());
    }

}
