package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.DTO.request.AuthResponse;
import com.appResP.residuosPatologicos.DTO.request.LoginRequest;
import com.appResP.residuosPatologicos.DTO.request.RegisterRequest;
import com.appResP.residuosPatologicos.DTO.request.RegisterResponse;
import com.appResP.residuosPatologicos.DTO.request.ResendConfirmationRequest;
import com.appResP.residuosPatologicos.api.error.exceptions.DuplicateResourceException;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.models.EmailVerificationToken;
import com.appResP.residuosPatologicos.models.Usuario;
import com.appResP.residuosPatologicos.models.enums.EstadoCuenta;
import com.appResP.residuosPatologicos.models.enums.Rol;
import com.appResP.residuosPatologicos.repository.IEmailVerificationTokenRepository;
import com.appResP.residuosPatologicos.repository.IUsuarioRepository;
import com.appResP.residuosPatologicos.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUsuarioRepository usuarioRepository;
    private final IEmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationSender emailVerificationSender;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${app.email.confirmation-expiration-minutes:60}")
    private long confirmationExpirationMinutes;

    @Value("${app.email.expose-confirmation-link:false}")
    private boolean exposeConfirmationLink;

    @Value("${app.email.require-confirmation:false}")
    private boolean requireEmailConfirmation;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (request.rol() != Rol.TRANSPORTISTA) {
            throw new IllegalArgumentException(
                    "El registro publico solo esta habilitado para transportistas.");
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }

        Usuario usuario = Usuario.builder()
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .rol(request.rol())
                .emailVerificado(false)
                .estadoCuenta(EstadoCuenta.PENDIENTE_APROBACION)
                .build();

        usuarioRepository.save(usuario);

        if (!requireEmailConfirmation) {
            return new RegisterResponse(
                    usuario.getEmail(),
                    usuario.getRol().name(),
                    "Registro creado. Tu cuenta queda pendiente de aprobacion por el administrador.",
                    null
            );
        }

        String confirmationUrl = createAndSendConfirmation(usuario);
        return new RegisterResponse(
                usuario.getEmail(),
                usuario.getRol().name(),
                "Registro creado. Revisa tu correo para confirmar la cuenta; luego quedara pendiente de aprobacion.",
                exposeConfirmationLink ? confirmationUrl : null
        );
    }

    @Transactional(noRollbackFor = DisabledException.class)
    public AuthResponse confirmEmail(String rawToken) {
        String tokenValue = rawToken == null ? "" : rawToken.trim();
        EmailVerificationToken token = emailVerificationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Token de confirmacion invalido"));

        if (Boolean.TRUE.equals(token.getUsado())) {
            throw new IllegalArgumentException("El link de confirmacion ya fue utilizado");
        }

        if (token.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El link de confirmacion expiro. Solicita uno nuevo.");
        }

        Usuario usuario = token.getUsuario();
        usuario.setEmailVerificado(true);
        token.setUsado(true);
        token.setFechaUso(LocalDateTime.now());

        usuarioRepository.save(usuario);
        emailVerificationTokenRepository.save(token);

        if (!isApprovedForAccess(usuario)) {
            throw new DisabledException("Correo confirmado. Tu cuenta esta pendiente de aprobacion por el administrador.");
        }

        String jwt = jwtUtil.generateToken(usuario);
        return new AuthResponse(jwt, usuario.getEmail(), usuario.getRol().name());
    }

    @Transactional
    public RegisterResponse resendConfirmation(ResendConfirmationRequest request) {
        String email = normalizeEmail(request.email());
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        if (!Boolean.FALSE.equals(usuario.getEmailVerificado())) {
            return new RegisterResponse(
                    usuario.getEmail(),
                    usuario.getRol().name(),
                    "El correo ya esta confirmado.",
                    null
            );
        }

        String confirmationUrl = createAndSendConfirmation(usuario);

        return new RegisterResponse(
                usuario.getEmail(),
                usuario.getRol().name(),
                "Se envio un nuevo correo de confirmacion.",
                exposeConfirmationLink ? confirmationUrl : null
        );
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.password()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() != request.rol()) {
            throw new BadCredentialsException("El rol seleccionado no corresponde al usuario");
        }

        if (requireEmailConfirmation && Boolean.FALSE.equals(usuario.getEmailVerificado())) {
            throw new DisabledException("Debes confirmar tu correo antes de iniciar sesion");
        }

        if (!isApprovedForAccess(usuario)) {
            throw new DisabledException(resolveApprovalMessage(usuario));
        }

        String token = jwtUtil.generateToken(usuario);
        return new AuthResponse(token, usuario.getEmail(), usuario.getRol().name());
    }

    private boolean isApprovedForAccess(Usuario usuario) {
        return usuario.getRol() != Rol.TRANSPORTISTA
                || usuario.getEstadoCuenta() == null
                || usuario.getEstadoCuenta() == EstadoCuenta.APROBADO;
    }

    private String resolveApprovalMessage(Usuario usuario) {
        EstadoCuenta estado = usuario.getEstadoCuenta();

        if (estado == EstadoCuenta.RECHAZADO) {
            return "Tu solicitud fue rechazada por el administrador.";
        }

        if (estado == EstadoCuenta.SUSPENDIDO) {
            return "Tu cuenta fue suspendida por el administrador.";
        }

        return "Tu cuenta esta pendiente de aprobacion por el administrador.";
    }

    private String createAndSendConfirmation(Usuario usuario) {
        emailVerificationTokenRepository.findByUsuarioAndUsadoFalse(usuario).forEach(existing -> {
            existing.setUsado(true);
            existing.setFechaUso(LocalDateTime.now());
            emailVerificationTokenRepository.save(existing);
        });

        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(usuario)
                .fechaExpiracion(LocalDateTime.now().plusMinutes(confirmationExpirationMinutes))
                .build();

        emailVerificationTokenRepository.save(token);

        String confirmationUrl = buildConfirmationUrl(token.getToken());
        emailVerificationSender.sendConfirmationEmail(usuario, confirmationUrl);
        return confirmationUrl;
    }

    private String buildConfirmationUrl(String token) {
        String baseUrl = frontendUrl.endsWith("/")
                ? frontendUrl.substring(0, frontendUrl.length() - 1)
                : frontendUrl;

        return baseUrl + "/confirmar-email?token=" + token;
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
