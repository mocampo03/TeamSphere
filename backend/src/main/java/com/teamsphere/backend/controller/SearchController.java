package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.SearchResult;
import com.teamsphere.backend.entity.Event;
import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.entity.Report;
import com.teamsphere.backend.entity.Task;
import com.teamsphere.backend.repository.EventRepository;
import com.teamsphere.backend.repository.MemberRepository;
import com.teamsphere.backend.repository.ReportRepository;
import com.teamsphere.backend.repository.TaskRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final MemberRepository memberRepository;
    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;
    private final ReportRepository reportRepository;

    public SearchController(
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
    public ResponseEntity<List<SearchResult>> search(
            @RequestParam String query,
            Authentication authentication) {

        Member currentUser = memberRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException(
                        "Usuario autenticado no encontrado"));

        Long organizationId = currentUser
                .getOrganization()
                .getId();

        String searchText = query.trim().toLowerCase(Locale.ROOT);

        if (searchText.isBlank()) {
            return ResponseEntity.ok(List.of());
        }

        List<SearchResult> results = new ArrayList<>();

        // Members
        List<Member> members = memberRepository
                .findByOrganizationId(organizationId);

        for (Member member : members) {
            String fullName = member.getFirstName() + " "
                    + member.getLastName();

            if (contains(fullName, searchText)
                    || contains(member.getEmail(), searchText)
                    || contains(member.getPosition(), searchText)) {

                results.add(new SearchResult(
                        "Member",
                        member.getId(),
                        fullName,
                        member.getPosition() != null
                                ? member.getPosition()
                                : member.getEmail(),
                        "/members"));
            }
        }

        // Tasks
        List<Task> tasks = taskRepository
                .findByOrganizationId(organizationId);

        for (Task task : tasks) {
            if (contains(task.getTitle(), searchText)
                    || contains(task.getDescription(), searchText)
                    || contains(task.getStatus(), searchText)) {

                results.add(new SearchResult(
                        "Task",
                        task.getId(),
                        task.getTitle(),
                        task.getStatus(),
                        "/tasks"));
            }
        }

        // Events
        List<Event> events = eventRepository
                .findByOrganizationId(organizationId);

        for (Event event : events) {
            if (contains(event.getTitle(), searchText)
                    || contains(event.getDescription(), searchText)) {

                results.add(new SearchResult(
                        "Event",
                        event.getId(),
                        event.getTitle(),
                        "Event",
                        "/events"));
            }
        }

        // Reports
        List<Report> reports = reportRepository
                .findByOrganizationId(organizationId);

        for (Report report : reports) {
            if (contains(report.getTitle(), searchText)
                    || contains(report.getDescription(), searchText)) {

                results.add(new SearchResult(
                        "Report",
                        report.getId(),
                        report.getTitle(),
                        "Report",
                        "/reports"));
            }
        }

        return ResponseEntity.ok(results);
    }

    private boolean contains(String value, String searchText) {
        return value != null
                && value.toLowerCase(Locale.ROOT)
                        .contains(searchText);
    }
}