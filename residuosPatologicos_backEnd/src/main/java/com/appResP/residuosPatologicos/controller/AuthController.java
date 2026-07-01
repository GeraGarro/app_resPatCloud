package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.request.AuthResponse;
import com.appResP.residuosPatologicos.DTO.request.LoginRequest;
import com.appResP.residuosPatologicos.DTO.request.RegisterRequest;
import com.appResP.residuosPatologicos.DTO.request.RegisterResponse;
import com.appResP.residuosPatologicos.DTO.request.ResendConfirmationRequest;
import com.appResP.residuosPatologicos.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<AuthResponse> confirmEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.confirmEmail(token));
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<RegisterResponse> resendConfirmation(@Valid @RequestBody ResendConfirmationRequest request) {
        return ResponseEntity.ok(authService.resendConfirmation(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
