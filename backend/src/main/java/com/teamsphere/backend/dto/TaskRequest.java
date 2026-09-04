package com.teamsphere.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class TaskRequest {

    @NotBlank(message = "El título es obligatorio")
    private String title;

    private String description;

    @NotBlank(message = "El estado es obligatorio")
    private String status;

    @NotBlank(message = "La prioridad es obligatoria")
    private String priority;

    private LocalDate dueDate;

    private Long assignedMemberId;

    @NotNull(message = "La organización es obligatoria")
    private Long organizationId;

    public TaskRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getAssignedMemberId() {
        return assignedMemberId;
    }

    public void setAssignedMemberId(Long assignedMemberId) {
        this.assignedMemberId = assignedMemberId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}