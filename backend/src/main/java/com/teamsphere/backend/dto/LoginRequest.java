package com.teamsphere.backend.dto;

public record LoginRequest(
        String email,
        String password) {
}