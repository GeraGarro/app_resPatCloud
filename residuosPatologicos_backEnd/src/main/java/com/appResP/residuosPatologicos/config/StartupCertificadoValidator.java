package com.appResP.residuosPatologicos.config;

import com.appResP.residuosPatologicos.services.imp.Certificado_service;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupCertificadoValidator implements ApplicationListener<ApplicationReadyEvent> {

       private final Certificado_service certificadoService;

        public StartupCertificadoValidator(Certificado_service certificadoService) {
            this.certificadoService = certificadoService;
        }

       @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            System.out.println("Validando y creando certificados si es necesario al iniciar la aplicación...");
            certificadoService.verificarYCrearCertificadosSiEsNecesario();

    }

}
