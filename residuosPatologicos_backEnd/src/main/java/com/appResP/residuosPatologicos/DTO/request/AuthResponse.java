package com.appResP.residuosPatologicos.DTO.request;

public record AuthResponse(
        String token,
        String email,
        String rol
) {
}
