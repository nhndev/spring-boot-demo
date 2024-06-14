package com.nhn.demo.security;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nhn.demo.service.UserDetailsServiceImpl;
import com.nhn.demo.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        String       email = null;
        final String token = this.getToken(request);
        if (StringUtils.isNotBlank(token)) {
            try {
                email = this.jwtUtil.getUsername(token);
            } catch (final IllegalArgumentException e) {
                log.error("Unable to get JWT Token", e);
            } catch (final ExpiredJwtException e) {
                log.error("JWT Token has expired", e);
            }
        }

        if (StringUtils.isNotBlank(email)
            && Objects.isNull(SecurityContextHolder.getContext()
                                                   .getAuthentication())) {
            final UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
            if (Objects.nonNull(userDetails)) {
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                                                                                                                   null,
                                                                                                                   userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext()
                                     .setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(final HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(bearerToken)
            && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
