package com.teamsphere.backend.dto;

import java.time.LocalDateTime;

public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long organizationId;

    public EventResponse(
            Long id,
            String title,
            String description,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long organizationId) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public Long getOrganizationId() {
        return organizationId;
    }
}