package com.appResP.residuosPatologicos.services;

import net.sf.jasperreports.engine.JRException;

public interface IreporteService {
    public byte[] generateReportByTicketId(Long ticketId) throws JRException;
}
