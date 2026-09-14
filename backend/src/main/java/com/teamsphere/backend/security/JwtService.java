package com.teamsphere.backend.security;

import com.teamsphere.backend.entity.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

        private static final String SECRET_KEY = "TeamSphereSecretKeyForJWTAuthentication2026Secure";

        private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24;

        private final SecretKey key = Keys.hmacShaKeyFor(
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        public String generateToken(UserDetails userDetails, Member member) {

                String role = userDetails.getAuthorities()
                                .stream()
                                .findFirst()
                                .map(authority -> authority.getAuthority())
                                .orElse("ROLE_USER");

                String fullName = (member.getFirstName() + " " + member.getLastName()).trim();

                return Jwts.builder()
                                .subject(userDetails.getUsername())
                                .claim("email", member.getEmail())
                                .claim("name", fullName)
                                .claim("role", role)
                                .claim("organizationId", member.getOrganization().getId())
                                .issuedAt(new Date())
                                .expiration(new Date(
                                                System.currentTimeMillis() + EXPIRATION_TIME))
                                .signWith(key)
                                .compact();
        }

        public String extractUsername(String token) {

                return Jwts.parser()
                                .verifyWith(key)
                                .build()
                                .parseSignedClaims(token)
                                .getPayload()
                                .getSubject();
        }

        public boolean isTokenValid(
                        String token,
                        UserDetails userDetails) {

                String username = extractUsername(token);

                return username.equals(userDetails.getUsername())
                                && !isTokenExpired(token);
        }

        private boolean isTokenExpired(String token) {

                Date expiration = Jwts.parser()
                                .verifyWith(key)
                                .build()
                                .parseSignedClaims(token)
                                .getPayload()
                                .getExpiration();

                return expiration.before(new Date());
        }
}