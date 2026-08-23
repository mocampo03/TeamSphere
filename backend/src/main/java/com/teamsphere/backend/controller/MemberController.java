package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.MemberRequest;
import com.teamsphere.backend.dto.MemberResponse;
import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.entity.Organization;
import com.teamsphere.backend.exception.ResourceNotFoundException;
import com.teamsphere.backend.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<MemberResponse>> getAllMembers() {

        List<MemberResponse> members = memberRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado"));

        return ResponseEntity.ok(toResponse(member));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {

        Member member = new Member();

        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setPassword(
                passwordEncoder.encode(request.getPassword()));
        member.setPhone(request.getPhone());
        member.setPosition(request.getPosition());
        member.setActive(request.getActive());

        Organization organization = new Organization();
        organization.setId(request.getOrganizationId());

        member.setOrganization(organization);

        Member savedMember = memberRepository.save(member);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedMember));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado"));

        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setPosition(request.getPosition());
        member.setActive(request.getActive());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            member.setPassword(
                    passwordEncoder.encode(request.getPassword()));
        }

        if (request.getOrganizationId() != null) {
            Organization organization = new Organization();
            organization.setId(request.getOrganizationId());
            member.setOrganization(organization);
        }

        Member updatedMember = memberRepository.save(member);

        return ResponseEntity.ok(toResponse(updatedMember));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado"));

        memberRepository.delete(member);

        return ResponseEntity.noContent().build();
    }
}