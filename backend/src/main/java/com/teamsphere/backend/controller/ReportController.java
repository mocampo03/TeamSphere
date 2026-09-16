package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.ReportRequest;
import com.teamsphere.backend.dto.ReportResponse;
import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.entity.Report;
import com.teamsphere.backend.exception.ResourceNotFoundException;
import com.teamsphere.backend.repository.MemberRepository;
import com.teamsphere.backend.repository.ReportRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

        private final ReportRepository reportRepository;
        private final MemberRepository memberRepository;

        public ReportController(
                        ReportRepository reportRepository,
                        MemberRepository memberRepository) {

                this.reportRepository = reportRepository;
                this.memberRepository = memberRepository;
        }

        @GetMapping
        public ResponseEntity<List<ReportResponse>> getAllReports(
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Long organizationId = currentUser.getOrganization().getId();

                List<ReportResponse> reports = reportRepository
                                .findByOrganizationId(organizationId)
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(reports);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ReportResponse> getReportById(
                        @PathVariable Long id,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Report report = getReportFromSameOrganization(
                                id,
                                currentUser.getOrganization().getId());

                return ResponseEntity.ok(toResponse(report));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ResponseEntity<ReportResponse> createReport(
                        @Valid @RequestBody ReportRequest request,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Report report = new Report();

                report.setTitle(request.getTitle());
                report.setDescription(request.getDescription());

                // La organización viene del usuario autenticado.
                report.setOrganization(currentUser.getOrganization());

                Report savedReport = reportRepository.save(report);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(toResponse(savedReport));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<ReportResponse> updateReport(
                        @PathVariable Long id,
                        @Valid @RequestBody ReportRequest request,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Report report = getReportFromSameOrganization(
                                id,
                                currentUser.getOrganization().getId());

                report.setTitle(request.getTitle());
                report.setDescription(request.getDescription());

                Report updatedReport = reportRepository.save(report);

                return ResponseEntity.ok(toResponse(updatedReport));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteReport(
                        @PathVariable Long id,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Report report = getReportFromSameOrganization(
                                id,
                                currentUser.getOrganization().getId());

                reportRepository.delete(report);

                return ResponseEntity.noContent().build();
        }

        private Member getAuthenticatedMember(
                        Authentication authentication) {

                String email = authentication.getName();

                return memberRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario autenticado no encontrado"));
        }

        private Report getReportFromSameOrganization(
                        Long reportId,
                        Long organizationId) {

                Report report = reportRepository.findById(reportId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Reporte no encontrado"));

                if (!report.getOrganization()
                                .getId()
                                .equals(organizationId)) {

                        throw new ResourceNotFoundException(
                                        "Reporte no encontrado");
                }

                return report;
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