package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.models.Hoja_ruta;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface IHojaRuta_service {


    public void crearHojaRutaSemanal();

    public Hoja_ruta obtenerHojaRutaPorFecha(LocalDate fechaEmision);

    public Optional<Hoja_ruta> findLatestForCurrentWeek();

    Optional<Hoja_ruta> findById(Long id);

    public List<Hoja_ruta> getHojasRutaByCertificado(Long certificadoId);
}
