package com.appResP.residuosPatologicos.repository.persistence;

import com.appResP.residuosPatologicos.models.Residuo;
import com.appResP.residuosPatologicos.models.TicketControl;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public class ResiduoListener {
    @PreUpdate
    @PrePersist
    public void actualizarPesoTotalTicket(Residuo residuo) {
        if (residuo.getTicketControl() != null) {
            residuo.getTicketControl().recalcularPesoTotal();
        }
    }

    @PreRemove
    public void eliminarPesoDelTicket(Residuo residuo) {
        if (residuo.getTicketControl() != null) {
            TicketControl ticket = residuo.getTicketControl();
            // Restar el peso antes de eliminar
            ticket.setPesoTotal(
                    ticket.getPesoTotal().subtract(residuo.getPeso())
            );
        }
    }
}
