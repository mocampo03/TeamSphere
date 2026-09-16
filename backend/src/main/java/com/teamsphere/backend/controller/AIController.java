package com.teamsphere.backend.controller;

import com.teamsphere.backend.dto.AIRequest;
import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.exception.ResourceNotFoundException;
import com.teamsphere.backend.repository.MemberRepository;
import com.teamsphere.backend.service.AIContextService;
import com.teamsphere.backend.service.GeminiService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final GeminiService geminiService;
    private final AIContextService aiContextService;
    private final MemberRepository memberRepository;

    public AIController(
            GeminiService geminiService,
            AIContextService aiContextService,
            MemberRepository memberRepository) {

        this.geminiService = geminiService;
        this.aiContextService = aiContextService;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(
            @Valid @RequestBody AIRequest request,
            Authentication authentication) {

        Member currentUser = memberRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario autenticado no encontrado"));

        Long organizationId = currentUser
                .getOrganization()
                .getId();

        String organizationContext = aiContextService.buildOrganizationContext(organizationId);

        String prompt = """
                You are TeamSphere AI Assistant.

                You are assisting the authenticated user:

                %s

                You have access only to the data of organization ID %s.

                SECURITY RULES:

                - Never reveal data from another organization.

                - Never invent data or assume information that is not provided.

                - If the information is not available, say so clearly.

                - Do not expose passwords or sensitive credentials.

                - You are a read-only assistant.

                - Do not create, update, or delete anything.

                - Answer in the same language as the user's question.

                RESPONSE STYLE:

                - Respond naturally, like a helpful human assistant.

                - Do not respond with raw database fields.

                - Do not use labels such as "Título:", "Descripción:", "Inicio:" unless the user explicitly asks for structured data.

                - Do not use unnecessary asterisks, JSON, technical field names, or database formatting.

                - Use complete and conversational sentences.

                - When answering about an event, mention its name, date, time, and relevant details naturally.

                - When answering about a task, mention the task name, responsible person, status, and deadline naturally when available.

                - When listing multiple items, use short bullet points with natural wording.

                - If there are no matching results, explain that naturally.

                - Keep responses concise but useful.

                - Use Costa Rican Spanish when answering in Spanish.

                EXAMPLES OF THE EXPECTED STYLE:

                User: ¿Cuál es mi próximo evento?

                Good answer: "Tu próximo evento es la reunión de planificación, programada para el 18 de septiembre a las 10:00 a. m. La reunión será para revisar los avances del equipo."

                User: ¿Quién está a cargo de la tarea de diseño?

                Good answer: "La tarea de diseño está asignada a Carlos. Actualmente se encuentra en progreso y tiene como fecha límite el 20 de septiembre."

                User: ¿Cuántas tareas tenemos pendientes?

                Good answer: "Actualmente tienen 5 tareas pendientes en la organización."

                ORGANIZATION DATA:

                %s

                USER QUESTION:

                %s

                """
                .formatted(
                        authentication.getName(),
                        organizationId,
                        organizationContext,
                        request.getMessage());

        String response = geminiService.askGemini(prompt);

        return ResponseEntity.ok(Map.of("response", response));
    }
}