package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.DTO.response.HojaRutaDTO;
import com.appResP.residuosPatologicos.DTO.response.HojaRutaDetalleDTO;
import com.appResP.residuosPatologicos.models.HojaRuta;
import com.appResP.residuosPatologicos.models.Transportista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IHojaRutaService {

    void crearHojaRutaSemanal();
    void verificarYCrearHojaRutaSiEsNecesario();
    void verificarYCrearHojaRutaSiEsNecesario(Transportista transportista);
    Optional<HojaRuta> findHojaRutaPorFecha(LocalDate fecha);
    Optional<HojaRuta> findHojaRutaForCurrentDate();
    HojaRutaDTO  findById(Long id);
    Page<HojaRutaDTO> findAll(Pageable pageable);
    HojaRutaDTO findDTOByFecha(LocalDate fecha);
    HojaRutaDTO  findDTOForCurrentDate();
    HojaRutaDTO  findUltima();
    HojaRutaDetalleDTO findDetalleById(Long id);  // nuevo
    List<HojaRutaDTO> findByCertificadoId(Long certificadoId);
    List<HojaRutaDTO> findPendientesCertificadoByTransportista(Long idTransportista);
    byte[] generarInformePdf(Long id);
}
