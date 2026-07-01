package com.appResP.residuosPatologicos.config;

import com.appResP.residuosPatologicos.services.IHojaRutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupHojaRutaValidator implements ApplicationListener<ApplicationReadyEvent> {

    private final IHojaRutaService hojaRutaService;

    public StartupHojaRutaValidator(IHojaRutaService hojaRutaService) {
        this.hojaRutaService = hojaRutaService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("Validando y creando hoja de ruta si es necesario al iniciar la aplicación...");
        // Llama al método que verifica y crea la hoja de ruta
        hojaRutaService.verificarYCrearHojaRutaSiEsNecesario();
    }
}
