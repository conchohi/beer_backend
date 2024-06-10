package com.zipbeer.beerbackend.service.impl;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.jwt.CustomUserDetails;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUserId(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        UserDto userDto = UserDto.builder()
                .userId(user.getUserId())
                .password(user.getPassword())
                .role(user.getRole())
                .nickname(user.getNickname())
                .build();

        return new CustomUserDetails(userDto);
    }
}
