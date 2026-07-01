package com.appResP.residuosPatologicos.DTO.request;

import com.appResP.residuosPatologicos.DTO.embeddables.DomicilioDTO;
import com.appResP.residuosPatologicos.models.enums.TipoGenerador;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneradorRequestDTO {
    @NotNull(message = "El tipo de generador es obligatorio")
    private TipoGenerador tipo;

    // comunes
    private boolean estado;

    @NotNull(message = "El domicilio es obligatorio")
    private DomicilioDTO domicilio;
    private List<TelefonoRequestDTO> telefonos;
    private String legajo;

    @Email(message = "El email no tiene formato válido")
    private String email;
    // AUTONOMO
    private String nombre;
    private String apellido;

    @Size(min = 11, max = 11, message = "El CUIL debe tener 11 dígitos")
    private String cuil;

    // EMPRESA
    @Size(min = 11, max = 11, message = "El CUIT debe tener 11 dígitos")
    private String cuit;
    private String razonSocial;
    private String nombreFantasia;

    public void setEmail(String email) {
        this.email = normalizeOptional(email);
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
