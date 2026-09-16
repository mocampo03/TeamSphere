package com.teamsphere.backend.dto;

public record SearchResult(
        String type,
        Long id,
        String title,
        String subtitle,
        String path) {
}