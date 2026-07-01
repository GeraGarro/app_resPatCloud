package com.appResP.residuosPatologicos.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendConfirmationRequest(
        @Email(message = "Formato de email invalido")
        @NotBlank(message = "El email es obligatorio")
        String email
) {
}
