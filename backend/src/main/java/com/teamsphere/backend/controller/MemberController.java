package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.MemberRequest;
import com.teamsphere.backend.dto.MemberResponse;
import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.entity.Organization;
import com.teamsphere.backend.exception.ResourceNotFoundException;
import com.teamsphere.backend.repository.MemberRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberController(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder) {

        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers(
            Authentication authentication) {

        Member currentUser = getAuthenticatedMember(authentication);

        Long organizationId = currentUser.getOrganization().getId();

        List<MemberResponse> members = memberRepository
                .findByOrganizationId(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(
            @PathVariable Long id,
            Authentication authentication) {

        Member currentUser = getAuthenticatedMember(authentication);

        Member member = getMemberFromSameOrganization(
                id,
                currentUser.getOrganization().getId());

        return ResponseEntity.ok(toResponse(member));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MemberResponse> createMember(
            @Valid @RequestBody MemberRequest request,
            Authentication authentication) {

        Member currentUser = getAuthenticatedMember(authentication);

        Member member = new Member();

        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());

        member.setPassword(
                passwordEncoder.encode(request.getPassword()));

        member.setPhone(request.getPhone());
        member.setPosition(request.getPosition());

        member.setActive(
                request.getActive() != null
                        ? request.getActive()
                        : true);

        /*
         * La organización se obtiene del usuario autenticado.
         * Ya no confiamos en organizationId enviado desde React.
         */
        member.setOrganization(currentUser.getOrganization());

        Member savedMember = memberRepository.save(member);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedMember));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request,
            Authentication authentication) {

        Member currentUser = getAuthenticatedMember(authentication);

        Member member = getMemberFromSameOrganization(
                id,
                currentUser.getOrganization().getId());

        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setPosition(request.getPosition());

        if (request.getActive() != null) {
            member.setActive(request.getActive());
        }

        /*
         * Solo actualizamos la contraseña si se envió una nueva.
         */
        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {

            member.setPassword(
                    passwordEncoder.encode(request.getPassword()));
        }

        /*
         * No permitimos modificar la organización.
         */
        member.setOrganization(currentUser.getOrganization());

        Member updatedMember = memberRepository.save(member);

        return ResponseEntity.ok(toResponse(updatedMember));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long id,
            Authentication authentication) {

        Member currentUser = getAuthenticatedMember(authentication);

        Member member = getMemberFromSameOrganization(
                id,
                currentUser.getOrganization().getId());

        memberRepository.delete(member);

        return ResponseEntity.noContent().build();
    }

    private Member getAuthenticatedMember(
            Authentication authentication) {

        String email = authentication.getName();

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario autenticado no encontrado"));
    }

    private Member getMemberFromSameOrganization(
            Long memberId,
            Long organizationId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Miembro no encontrado"));

        Long memberOrganizationId = member.getOrganization().getId();

        if (!memberOrganizationId.equals(organizationId)) {
            throw new ResourceNotFoundException(
                    "Miembro no encontrado");
        }

        return member;
    }

    private MemberResponse toResponse(Member member) {

        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getPhone(),
                member.getPosition(),
                member.getActive(),
                member.getOrganization().getId());
    }
}