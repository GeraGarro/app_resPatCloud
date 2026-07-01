package com.appResP.residuosPatologicos.DTO.response;

import com.appResP.residuosPatologicos.models.enums.EstadoCuenta;
import com.appResP.residuosPatologicos.models.enums.Rol;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AdminUsuarioDTO(
        Long id,
        String email,
        Rol rol,
        EstadoCuenta estadoCuenta,
        Boolean emailVerificado,
        LocalDateTime fechaRegistro,
        Long transportistaId,
        String nombre,
        String apellido,
        String nombreFantasia
) {
}
