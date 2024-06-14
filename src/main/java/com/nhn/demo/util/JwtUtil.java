package com.nhn.demo.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private Long expirationTime;

    public Claims getClaims(final String token) {
        final SecretKey secretKey = Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                   .parseClaimsJws(token).getBody();
    }

    public <T> T getClaim(final String token,
                          final Function<Claims, T> claimsResolver) {
        final Claims claims = this.getClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getUsername(final String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public Date getExpirationDate(final String token) {
        return this.getClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(final String token) {
        final Date expiration = this.getExpirationDate(token);
        return expiration.before(new Date());
    }

    public String generateToken(final String email) {
        final SecretKey secretKey = Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder().setSubject(email).setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() +
                                           this.expirationTime))
                   .claim("email", email).signWith(secretKey).compact();
    }
}
