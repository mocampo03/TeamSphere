package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.TaskRequest;
import com.teamsphere.backend.dto.TaskResponse;
import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.entity.Organization;
import com.teamsphere.backend.entity.Task;
import com.teamsphere.backend.exception.ResourceNotFoundException;
import com.teamsphere.backend.repository.MemberRepository;
import com.teamsphere.backend.repository.OrganizationRepository;
import com.teamsphere.backend.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final OrganizationRepository organizationRepository;

    public TaskController(
            TaskRepository taskRepository,
            MemberRepository memberRepository,
            OrganizationRepository organizationRepository) {

        this.taskRepository = taskRepository;
        this.memberRepository = memberRepository;
        this.organizationRepository = organizationRepository;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {

        List<TaskResponse> tasks = taskRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<TaskResponse>> getTasksByOrganization(
            @PathVariable Long organizationId) {

        List<TaskResponse> tasks = taskRepository
                .findByOrganizationId(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<TaskResponse>> getTasksByMember(
            @PathVariable Long memberId) {

        List<TaskResponse> tasks = taskRepository
                .findByAssignedMemberId(memberId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        return ResponseEntity.ok(toResponse(task));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request) {

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada"));

        task.setOrganization(organization);

        if (request.getAssignedMemberId() != null) {

            Member member = memberRepository
                    .findById(request.getAssignedMemberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado"));

            task.setAssignedMember(member);
        }

        Task savedTask = taskRepository.save(task);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedTask));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada"));

        task.setOrganization(organization);

        if (request.getAssignedMemberId() != null) {

            Member member = memberRepository
                    .findById(request.getAssignedMemberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado"));

            task.setAssignedMember(member);

        } else {

            task.setAssignedMember(null);
        }

        Task updatedTask = taskRepository.save(task);

        return ResponseEntity.ok(toResponse(updatedTask));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        taskRepository.delete(task);

        return ResponseEntity.noContent().build();
    }

    private TaskResponse toResponse(Task task) {

        Long assignedMemberId = null;

        if (task.getAssignedMember() != null) {
            assignedMemberId = task.getAssignedMember().getId();
        }

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                assignedMemberId,
                task.getOrganization().getId());
    }
}