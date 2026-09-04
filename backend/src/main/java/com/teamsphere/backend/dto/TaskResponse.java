package com.teamsphere.backend.dto;

import java.time.LocalDate;

public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private String status;

    private String priority;

    private LocalDate dueDate;

    private Long assignedMemberId;

    private Long organizationId;

    public TaskResponse(
            Long id,
            String title,
            String description,
            String status,
            String priority,
            LocalDate dueDate,
            Long assignedMemberId,
            Long organizationId) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.assignedMemberId = assignedMemberId;
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

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Long getAssignedMemberId() {
        return assignedMemberId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }
}