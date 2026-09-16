package com.teamsphere.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class AIRequest {

    @NotBlank(message = "El mensaje es obligatorio")
    private String message;

    public AIRequest() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}