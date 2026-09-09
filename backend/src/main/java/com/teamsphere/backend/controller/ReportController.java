package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.ReportRequest;
import com.teamsphere.backend.dto.ReportResponse;
import com.teamsphere.backend.entity.Organization;
import com.teamsphere.backend.entity.Report;
import com.teamsphere.backend.exception.ResourceNotFoundException;
import com.teamsphere.backend.repository.OrganizationRepository;
import com.teamsphere.backend.repository.ReportRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportRepository reportRepository;
    private final OrganizationRepository organizationRepository;

    public ReportController(
            ReportRepository reportRepository,
            OrganizationRepository organizationRepository) {

        this.reportRepository = reportRepository;
        this.organizationRepository = organizationRepository;
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() {

        List<ReportResponse> reports = reportRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReportById(
            @PathVariable Long id) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));

        return ResponseEntity.ok(toResponse(report));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @Valid @RequestBody ReportRequest request) {

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organización no encontrada"));

        Report report = new Report();

        report.setTitle(request.getTitle());
        report.setDescription(request.getDescription());
        report.setOrganization(organization);

        Report savedReport = reportRepository.save(report);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedReport));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReportResponse> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody ReportRequest request) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organización no encontrada"));

        report.setTitle(request.getTitle());
        report.setDescription(request.getDescription());
        report.setOrganization(organization);

        Report updatedReport = reportRepository.save(report);

        return ResponseEntity.ok(toResponse(updatedReport));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable Long id) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));

        reportRepository.delete(report);

        return ResponseEntity.noContent().build();
    }

    private ReportResponse toResponse(Report report) {

        return new ReportResponse(
                report.getId(),
                report.getTitle(),
                report.getDescription(),
                report.getCreatedAt(),
                report.getOrganization().getId());
    }
}