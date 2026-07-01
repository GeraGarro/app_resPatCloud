package com.appResP.residuosPatologicos.config;

import com.appResP.residuosPatologicos.services.ICertificadoService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupCertificadoValidator implements ApplicationListener<ApplicationReadyEvent> {

       private final ICertificadoService certificadoService;

        public StartupCertificadoValidator(ICertificadoService certificadoService) {
            this.certificadoService = certificadoService;
        }

       @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            System.out.println("Validando y creando certificados si es necesario al iniciar la aplicación...");
            certificadoService.verificarYCrearCertificadosSiEsNecesario();

    }

}
