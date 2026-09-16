package com.teamsphere.backend.service;

import com.teamsphere.backend.entity.Event;
import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.entity.Report;
import com.teamsphere.backend.entity.Task;
import com.teamsphere.backend.repository.EventRepository;
import com.teamsphere.backend.repository.MemberRepository;
import com.teamsphere.backend.repository.ReportRepository;
import com.teamsphere.backend.repository.TaskRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AIContextService {

    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    public AIContextService(
            TaskRepository taskRepository,
            EventRepository eventRepository,
            ReportRepository reportRepository,
            MemberRepository memberRepository) {

        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
        this.reportRepository = reportRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public String buildOrganizationContext(Long organizationId) {

        List<Member> members = memberRepository.findByOrganizationId(organizationId);

        List<Task> tasks = taskRepository.findByOrganizationId(organizationId);

        List<Event> events = eventRepository.findByOrganizationId(organizationId);

        List<Report> reports = reportRepository.findByOrganizationId(organizationId);

        StringBuilder context = new StringBuilder();

        context.append("DATOS DE LA ORGANIZACIÓN AUTENTICADA\n");
        context.append("Organization ID: ")
                .append(organizationId)
                .append("\n\n");

        context.append("MIEMBROS:\n");

        for (Member member : members) {
            context.append("- ID: ")
                    .append(member.getId())
                    .append(", Nombre: ")
                    .append(member.getFirstName())
                    .append(" ")
                    .append(member.getLastName())
                    .append(", Email: ")
                    .append(member.getEmail())
                    .append(", Puesto: ")
                    .append(member.getPosition())
                    .append(", Rol: ")
                    .append(member.getRole())
                    .append(", Activo: ")
                    .append(member.getActive())
                    .append("\n");
        }

        context.append("\nTAREAS:\n");

        for (Task task : tasks) {

            String assignedMember = "Sin asignar";

            if (task.getAssignedMember() != null) {
                assignedMember = task.getAssignedMember().getFirstName()
                        + " "
                        + task.getAssignedMember().getLastName();
            }

            context.append("- ID: ")
                    .append(task.getId())
                    .append(", Título: ")
                    .append(task.getTitle())
                    .append(", Descripción: ")
                    .append(task.getDescription())
                    .append(", Estado: ")
                    .append(task.getStatus())
                    .append(", Prioridad: ")
                    .append(task.getPriority())
                    .append(", Fecha límite: ")
                    .append(task.getDueDate())
                    .append(", Responsable: ")
                    .append(assignedMember)
                    .append("\n");
        }

        context.append("\nEVENTOS:\n");

        for (Event event : events) {
            context.append("- ID: ")
                    .append(event.getId())
                    .append(", Título: ")
                    .append(event.getTitle())
                    .append(", Descripción: ")
                    .append(event.getDescription())
                    .append(", Inicio: ")
                    .append(event.getStartDate())
                    .append(", Fin: ")
                    .append(event.getEndDate())
                    .append("\n");
        }

        context.append("\nREPORTES:\n");

        for (Report report : reports) {
            context.append("- ID: ")
                    .append(report.getId())
                    .append(", Título: ")
                    .append(report.getTitle())
                    .append(", Descripción: ")
                    .append(report.getDescription())
                    .append(", Creado: ")
                    .append(report.getCreatedAt())
                    .append("\n");
        }

        context.append("\nFECHA ACTUAL: ")
                .append(LocalDate.now())
                .append("\n");

        return context.toString();
    }
}