package com.teamsphere.backend.dto;

public class DashboardResponse {

    private Long totalMembers;
    private Long totalTasks;
    private Long todoTasks;
    private Long inProgressTasks;
    private Long completedTasks;
    private Long totalEvents;

    public DashboardResponse(
            Long totalMembers,
            Long totalTasks,
            Long todoTasks,
            Long inProgressTasks,
            Long completedTasks,
            Long totalEvents) {

        this.totalMembers = totalMembers;
        this.totalTasks = totalTasks;
        this.todoTasks = todoTasks;
        this.inProgressTasks = inProgressTasks;
        this.completedTasks = completedTasks;
        this.totalEvents = totalEvents;
    }

    public Long getTotalMembers() {
        return totalMembers;
    }

    public Long getTotalTasks() {
        return totalTasks;
    }

    public Long getTodoTasks() {
        return todoTasks;
    }

    public Long getInProgressTasks() {
        return inProgressTasks;
    }

    public Long getCompletedTasks() {
        return completedTasks;
    }

    public Long getTotalEvents() {
        return totalEvents;
    }
}