package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.models.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationSender {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.email.from:no-reply@respat.local}")
    private String from;

    public void sendConfirmationEmail(Usuario usuario, String confirmationUrl) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        if (mailSender == null) {
            log.warn("SMTP no configurado. Link de confirmacion para {}: {}", usuario.getEmail(), confirmationUrl);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(usuario.getEmail());
            message.setSubject("Confirma tu registro en ResPat Cloud");
            message.setText("""
                    Hola,

                    Recibimos una solicitud para crear una cuenta en ResPat Cloud.
                    Para confirmar tu correo y activar el acceso, abre este enlace:

                    %s

                    Si no solicitaste este registro, ignora este mensaje.
                    """.formatted(confirmationUrl));

            mailSender.send(message);
        } catch (Exception ex) {
            log.error("No se pudo enviar el email de confirmacion a {}", usuario.getEmail(), ex);
        }
    }
}
