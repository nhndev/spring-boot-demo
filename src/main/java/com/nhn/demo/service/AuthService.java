package com.nhn.demo.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nhn.demo.dto.request.auth.UserLoginRequest;
import com.nhn.demo.entity.LoginUser;
import com.nhn.demo.exception.FuncErrorException;
import com.nhn.demo.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    public String login(final UserLoginRequest request) {
        final String email = request.getEmail();
        final String password = request.getPassword();
        try {
           final Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            final LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            return this.jwtUtil.generateToken(loginUser.getUsername());
        } catch (final BadCredentialsException e) {
            throw new FuncErrorException("Invalid email/password supplied");
        } catch (final Exception e) {
            throw new FuncErrorException("Internal server error: " + e.getMessage());
        }
    }
}
