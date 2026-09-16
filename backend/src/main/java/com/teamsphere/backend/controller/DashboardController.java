package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.DashboardResponse;
import com.teamsphere.backend.entity.Event;
import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.entity.Report;
import com.teamsphere.backend.entity.Task;
import com.teamsphere.backend.repository.EventRepository;
import com.teamsphere.backend.repository.MemberRepository;
import com.teamsphere.backend.repository.ReportRepository;
import com.teamsphere.backend.repository.TaskRepository;

import com.teamsphere.backend.exception.ResourceNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

        private final MemberRepository memberRepository;
        private final TaskRepository taskRepository;
        private final EventRepository eventRepository;
        private final ReportRepository reportRepository;

        public DashboardController(
                        MemberRepository memberRepository,
                        TaskRepository taskRepository,
                        EventRepository eventRepository,
                        ReportRepository reportRepository) {

                this.memberRepository = memberRepository;
                this.taskRepository = taskRepository;
                this.eventRepository = eventRepository;
                this.reportRepository = reportRepository;
        }

        @GetMapping
        public ResponseEntity<DashboardResponse> getDashboard(
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Long organizationId = currentUser.getOrganization().getId();

                long totalMembers = memberRepository
                                .findByOrganizationId(organizationId)
                                .size();

                List<Task> tasks = taskRepository
                                .findByOrganizationId(organizationId);

                long totalTasks = tasks.size();

                long todoTasks = tasks.stream()
                                .filter(task -> "TODO".equals(task.getStatus()))
                                .count();

                long inProgressTasks = tasks.stream()
                                .filter(task -> "IN_PROGRESS".equals(task.getStatus()))
                                .count();

                long completedTasks = tasks.stream()
                                .filter(task -> "DONE".equals(task.getStatus()))
                                .count();

                long totalEvents = eventRepository
                                .findByOrganizationId(organizationId)
                                .size();

                long totalReports = reportRepository
                                .findByOrganizationId(organizationId)
                                .size();

                DashboardResponse dashboard = new DashboardResponse(
                                totalMembers,
                                totalTasks,
                                todoTasks,
                                inProgressTasks,
                                completedTasks,
                                totalEvents,
                                totalReports);

                return ResponseEntity.ok(dashboard);
        }

        private Member getAuthenticatedMember(
                        Authentication authentication) {

                String email = authentication.getName();

                return memberRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario autenticado no encontrado"));
        }
}