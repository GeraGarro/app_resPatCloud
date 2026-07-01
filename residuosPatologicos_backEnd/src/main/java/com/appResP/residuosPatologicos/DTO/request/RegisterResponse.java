package com.appResP.residuosPatologicos.DTO.request;

public record RegisterResponse(
        String email,
        String rol,
        String message,
        String confirmationUrl
) {
}
