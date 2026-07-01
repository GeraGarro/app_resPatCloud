package com.appResP.residuosPatologicos.config;

import com.appResP.residuosPatologicos.services.ITicketService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupTicketValidator implements ApplicationListener<ApplicationReadyEvent> {

    private final ITicketService ticketService;

    public StartupTicketValidator(ITicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ticketService.procesarTicketsDeHojasVencidas();
    }
}
