package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.TaskRequest;
import com.teamsphere.backend.dto.TaskResponse;
import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.entity.Task;
import com.teamsphere.backend.exception.ResourceNotFoundException;
import com.teamsphere.backend.repository.MemberRepository;
import com.teamsphere.backend.repository.TaskRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

        private final TaskRepository taskRepository;
        private final MemberRepository memberRepository;

        public TaskController(
                        TaskRepository taskRepository,
                        MemberRepository memberRepository) {

                this.taskRepository = taskRepository;
                this.memberRepository = memberRepository;
        }

        @GetMapping
        public ResponseEntity<List<TaskResponse>> getAllTasks(
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Long organizationId = currentUser.getOrganization().getId();

                List<TaskResponse> tasks = taskRepository
                                .findByOrganizationId(organizationId)
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(tasks);
        }

        @GetMapping("/organization/{organizationId}")
        public ResponseEntity<List<TaskResponse>> getTasksByOrganization(
                        @PathVariable Long organizationId,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                validateSameOrganization(
                                organizationId,
                                currentUser.getOrganization().getId());

                List<TaskResponse> tasks = taskRepository
                                .findByOrganizationId(organizationId)
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(tasks);
        }

        @GetMapping("/member/{memberId}")
        public ResponseEntity<List<TaskResponse>> getTasksByMember(
                        @PathVariable Long memberId,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Member assignedMember = memberRepository
                                .findById(memberId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Miembro no encontrado"));

                validateSameOrganization(
                                assignedMember.getOrganization().getId(),
                                currentUser.getOrganization().getId());

                List<TaskResponse> tasks = taskRepository
                                .findByAssignedMemberId(memberId)
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(tasks);
        }

        @GetMapping("/{id}")
        public ResponseEntity<TaskResponse> getTaskById(
                        @PathVariable Long id,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Task task = getTaskFromSameOrganization(
                                id,
                                currentUser.getOrganization().getId());

                return ResponseEntity.ok(toResponse(task));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ResponseEntity<TaskResponse> createTask(
                        @Valid @RequestBody TaskRequest request,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Task task = new Task();

                task.setTitle(request.getTitle());
                task.setDescription(request.getDescription());
                task.setStatus(request.getStatus());
                task.setPriority(request.getPriority());
                task.setDueDate(request.getDueDate());

                /*
                 * La organización siempre viene del usuario autenticado.
                 * Ignoramos request.getOrganizationId().
                 */
                task.setOrganization(currentUser.getOrganization());

                if (request.getAssignedMemberId() != null) {

                        Member assignedMember = memberRepository
                                        .findById(request.getAssignedMemberId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Miembro no encontrado"));

                        validateSameOrganization(
                                        assignedMember.getOrganization().getId(),
                                        currentUser.getOrganization().getId());

                        task.setAssignedMember(assignedMember);
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
                        @Valid @RequestBody TaskRequest request,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Task task = getTaskFromSameOrganization(
                                id,
                                currentUser.getOrganization().getId());

                task.setTitle(request.getTitle());
                task.setDescription(request.getDescription());
                task.setStatus(request.getStatus());
                task.setPriority(request.getPriority());
                task.setDueDate(request.getDueDate());

                /*
                 * La tarea conserva la organización autenticada.
                 */
                task.setOrganization(currentUser.getOrganization());

                if (request.getAssignedMemberId() != null) {

                        Member assignedMember = memberRepository
                                        .findById(request.getAssignedMemberId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Miembro no encontrado"));

                        validateSameOrganization(
                                        assignedMember.getOrganization().getId(),
                                        currentUser.getOrganization().getId());

                        task.setAssignedMember(assignedMember);

                } else {
                        task.setAssignedMember(null);
                }

                Task updatedTask = taskRepository.save(task);

                return ResponseEntity.ok(toResponse(updatedTask));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteTask(
                        @PathVariable Long id,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Task task = getTaskFromSameOrganization(
                                id,
                                currentUser.getOrganization().getId());

                taskRepository.delete(task);

                return ResponseEntity.noContent().build();
        }

        private Member getAuthenticatedMember(
                        Authentication authentication) {

                String email = authentication.getName();

                return memberRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario autenticado no encontrado"));
        }

        private Task getTaskFromSameOrganization(
                        Long taskId,
                        Long organizationId) {

                Task task = taskRepository.findById(taskId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Tarea no encontrada"));

                if (!task.getOrganization()
                                .getId()
                                .equals(organizationId)) {

                        throw new ResourceNotFoundException(
                                        "Tarea no encontrada");
                }

                return task;
        }

        private void validateSameOrganization(
                        Long requestedOrganizationId,
                        Long authenticatedOrganizationId) {

                if (!requestedOrganizationId.equals(authenticatedOrganizationId)) {
                        throw new ResourceNotFoundException(
                                        "Recurso no encontrado");
                }
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