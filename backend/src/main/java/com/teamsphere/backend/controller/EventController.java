package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.EventRequest;
import com.teamsphere.backend.dto.EventResponse;
import com.teamsphere.backend.entity.Event;
import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.exception.ResourceNotFoundException;
import com.teamsphere.backend.repository.EventRepository;
import com.teamsphere.backend.repository.MemberRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

        private final EventRepository eventRepository;
        private final MemberRepository memberRepository;

        public EventController(
                        EventRepository eventRepository,
                        MemberRepository memberRepository) {

                this.eventRepository = eventRepository;
                this.memberRepository = memberRepository;
        }

        @GetMapping
        public ResponseEntity<List<EventResponse>> getAllEvents(
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Long organizationId = currentUser.getOrganization().getId();

                List<EventResponse> events = eventRepository
                                .findByOrganizationId(organizationId)
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(events);
        }

        @GetMapping("/organization/{organizationId}")
        public ResponseEntity<List<EventResponse>> getEventsByOrganization(
                        @PathVariable Long organizationId,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                validateSameOrganization(
                                organizationId,
                                currentUser.getOrganization().getId());

                List<EventResponse> events = eventRepository
                                .findByOrganizationId(organizationId)
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(events);
        }

        @GetMapping("/{id}")
        public ResponseEntity<EventResponse> getEventById(
                        @PathVariable Long id,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Event event = getEventFromSameOrganization(
                                id,
                                currentUser.getOrganization().getId());

                return ResponseEntity.ok(toResponse(event));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ResponseEntity<EventResponse> createEvent(
                        @Valid @RequestBody EventRequest request,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Event event = new Event();

                event.setTitle(request.getTitle());
                event.setDescription(request.getDescription());
                event.setStartDate(request.getStartDate());
                event.setEndDate(request.getEndDate());

                /*
                 * La organización siempre se obtiene del usuario autenticado.
                 * No se utiliza request.getOrganizationId().
                 */
                event.setOrganization(currentUser.getOrganization());

                Event savedEvent = eventRepository.save(event);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(toResponse(savedEvent));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<EventResponse> updateEvent(
                        @PathVariable Long id,
                        @Valid @RequestBody EventRequest request,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Event event = getEventFromSameOrganization(
                                id,
                                currentUser.getOrganization().getId());

                event.setTitle(request.getTitle());
                event.setDescription(request.getDescription());
                event.setStartDate(request.getStartDate());
                event.setEndDate(request.getEndDate());

                /*
                 * El evento conserva la organización del usuario autenticado.
                 */
                event.setOrganization(currentUser.getOrganization());

                Event updatedEvent = eventRepository.save(event);

                return ResponseEntity.ok(toResponse(updatedEvent));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteEvent(
                        @PathVariable Long id,
                        Authentication authentication) {

                Member currentUser = getAuthenticatedMember(authentication);

                Event event = getEventFromSameOrganization(
                                id,
                                currentUser.getOrganization().getId());

                eventRepository.delete(event);

                return ResponseEntity.noContent().build();
        }

        private Member getAuthenticatedMember(
                        Authentication authentication) {

                String email = authentication.getName();

                return memberRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario autenticado no encontrado"));
        }

        private Event getEventFromSameOrganization(
                        Long eventId,
                        Long organizationId) {

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Evento no encontrado"));

                if (!event.getOrganization()
                                .getId()
                                .equals(organizationId)) {

                        throw new ResourceNotFoundException(
                                        "Evento no encontrado");
                }

                return event;
        }

        private void validateSameOrganization(
                        Long requestedOrganizationId,
                        Long authenticatedOrganizationId) {

                if (!requestedOrganizationId.equals(authenticatedOrganizationId)) {
                        throw new ResourceNotFoundException(
                                        "Recurso no encontrado");
                }
        }

        private EventResponse toResponse(Event event) {

                return new EventResponse(
                                event.getId(),
                                event.getTitle(),
                                event.getDescription(),
                                event.getStartDate(),
                                event.getEndDate(),
                                event.getOrganization().getId());
        }
}