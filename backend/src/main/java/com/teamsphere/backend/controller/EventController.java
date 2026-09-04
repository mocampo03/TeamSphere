package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.EventRequest;
import com.teamsphere.backend.dto.EventResponse;
import com.teamsphere.backend.entity.Event;
import com.teamsphere.backend.entity.Organization;
import com.teamsphere.backend.exception.ResourceNotFoundException;
import com.teamsphere.backend.repository.EventRepository;
import com.teamsphere.backend.repository.OrganizationRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;

    public EventController(
            EventRepository eventRepository,
            OrganizationRepository organizationRepository) {

        this.eventRepository = eventRepository;
        this.organizationRepository = organizationRepository;
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {

        List<EventResponse> events = eventRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

        return ResponseEntity.ok(toResponse(event));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest request) {

        Event event = new Event();

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada"));

        event.setOrganization(organization);

        Event savedEvent = eventRepository.save(event);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedEvent));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada"));

        event.setOrganization(organization);

        Event updatedEvent = eventRepository.save(event);

        return ResponseEntity.ok(toResponse(updatedEvent));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

        eventRepository.delete(event);

        return ResponseEntity.noContent().build();
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