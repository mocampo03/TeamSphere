package com.teamsphere.backend.dto;

import java.time.LocalDateTime;

public class ReportResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Long organizationId;

    public ReportResponse(
            Long id,
            String title,
            String description,
            LocalDateTime createdAt,
            Long organizationId) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.organizationId = organizationId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getOrganizationId() {
        return organizationId;
    }
}