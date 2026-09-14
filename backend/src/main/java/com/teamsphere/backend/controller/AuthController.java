package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.LoginRequest;
import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.repository.MemberRepository;
import com.teamsphere.backend.security.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final MemberRepository memberRepository;

        public AuthController(
                        AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        MemberRepository memberRepository) {

                this.authenticationManager = authenticationManager;
                this.jwtService = jwtService;
                this.memberRepository = memberRepository;
        }

        @PostMapping("/login")
        public ResponseEntity<Map<String, String>> login(
                        @RequestBody LoginRequest request) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.email(),
                                                request.password()));

                Member member = memberRepository
                                .findByEmail(request.email())
                                .orElseThrow(() -> new RuntimeException("Member not found"));

                String token = jwtService.generateToken(
                                (org.springframework.security.core.userdetails.UserDetails) authentication
                                                .getPrincipal(),
                                member);

                return ResponseEntity.ok(
                                Map.of("token", token));
        }
}