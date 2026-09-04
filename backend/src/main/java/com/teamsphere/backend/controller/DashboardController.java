package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.DashboardResponse;
import com.teamsphere.backend.repository.EventRepository;
import com.teamsphere.backend.repository.MemberRepository;
import com.teamsphere.backend.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final MemberRepository memberRepository;
    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;

    public DashboardController(
            MemberRepository memberRepository,
            TaskRepository taskRepository,
            EventRepository eventRepository) {

        this.memberRepository = memberRepository;
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {

        long totalMembers = memberRepository.count();

        long totalTasks = taskRepository.count();

        long todoTasks = taskRepository.findAll()
                .stream()
                .filter(task -> "TODO".equals(task.getStatus()))
                .count();

        long inProgressTasks = taskRepository.findAll()
                .stream()
                .filter(task -> "IN_PROGRESS".equals(task.getStatus()))
                .count();

        long completedTasks = taskRepository.findAll()
                .stream()
                .filter(task -> "DONE".equals(task.getStatus()))
                .count();

        long totalEvents = eventRepository.count();

        DashboardResponse dashboard = new DashboardResponse(
                totalMembers,
                totalTasks,
                todoTasks,
                inProgressTasks,
                completedTasks,
                totalEvents);

        return ResponseEntity.ok(dashboard);
    }
}