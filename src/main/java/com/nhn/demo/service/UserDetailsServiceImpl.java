package com.nhn.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nhn.demo.entity.LoginUser;
import com.nhn.demo.entity.User;
import com.nhn.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = this.userRepository.findByEmail(username)
                                             .orElseThrow(() -> new UsernameNotFoundException("User not found with email: "
                                                                                              + username));
        return new LoginUser(user);
    }
}
