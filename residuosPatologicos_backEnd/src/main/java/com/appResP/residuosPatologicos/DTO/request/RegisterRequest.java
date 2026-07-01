package com.appResP.residuosPatologicos.DTO.request;

import com.appResP.residuosPatologicos.models.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email(message = "Formato de email invalido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, message = "La contrasena debe tener al menos 8 caracteres")
        String password,

        @NotNull(message = "El rol es obligatorio")
        Rol rol
) {
}
