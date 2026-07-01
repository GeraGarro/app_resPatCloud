package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.DTO.response.CertificadoDTO;
import com.appResP.residuosPatologicos.DTO.request.CertificadoRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.HojaRutaConTicketsDTO;
import com.appResP.residuosPatologicos.models.enums.Meses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICertificadoService {
    CertificadoDTO findById(Long id);
    Page<CertificadoDTO> findAll(Pageable pageable);
    Page<CertificadoDTO> findByTransportista(Long idTransportista, Pageable pageable);

    CertificadoDTO create(CertificadoRequestDTO dto);
    CertificadoDTO update(Long id, CertificadoRequestDTO dto);
    void deleteById(Long id);
    byte[] generarCertificadoPdf(Long id);
    List<HojaRutaConTicketsDTO> findHojasConTickets(Long id);
    Page<CertificadoDTO> findByMesAndAnio(Meses mes, int anio, Pageable pageable);
    // lógica mensual
    void verificarYCrearCertificadosSiEsNecesario();
}
