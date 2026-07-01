package com.appResP.residuosPatologicos.config;

import com.appResP.residuosPatologicos.models.Usuario;
import com.appResP.residuosPatologicos.models.enums.EstadoCuenta;
import com.appResP.residuosPatologicos.models.enums.Rol;
import com.appResP.residuosPatologicos.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminSeedConfig {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.reset-password:false}")
    private boolean resetAdminPassword;

    @Bean
    CommandLineRunner seedAdminUser() {
        return args -> {
            String email = normalizeEmail(adminEmail);
            String password = normalize(adminPassword);

            if (email.isBlank() || password.isBlank()) {
                log.info("Seed de administrador omitido: app.admin.email/app.admin.password no configurados.");
                return;
            }

            usuarioRepository.findByEmail(email).ifPresentOrElse(existing -> {
                if (existing.getRol() != Rol.ADMIN) {
                    throw new IllegalStateException(
                            "El email configurado para admin ya existe con otro rol: " + email);
                }

                existing.setEmailVerificado(true);
                existing.setEstadoCuenta(EstadoCuenta.APROBADO);

                if (resetAdminPassword) {
                    existing.setPassword(passwordEncoder.encode(password));
                    usuarioRepository.save(existing);
                    log.info("Usuario administrador actualizado por seed: {}. Password reseteado.", email);
                    return;
                }

                usuarioRepository.save(existing);
                log.info("Seed de administrador omitido: el admin {} ya existe.", email);
            }, () -> {
                Usuario admin = Usuario.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .rol(Rol.ADMIN)
                        .emailVerificado(true)
                        .estadoCuenta(EstadoCuenta.APROBADO)
                        .build();

                usuarioRepository.save(admin);
                log.info("Usuario administrador creado por seed: {}", email);
            });
        };
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeEmail(String value) {
        return normalize(value).toLowerCase(Locale.ROOT);
    }
}
