package com.appResP.residuosPatologicos.persistence;

import com.appResP.residuosPatologicos.models.Hoja_ruta;

import java.time.LocalDate;
import java.util.Optional;

public interface IHoja_rutaDAO {

    public void crearHojaRutaSemanal();
    public Hoja_ruta obtenerHojaRutaPorFecha(LocalDate fechaEmision);
    public Optional<Hoja_ruta> findHojaRutaForCurrentDate();
    void verificarYCrearHojaRutaSiEsNecesario();
    Optional<Hoja_ruta> findById(Long Id);
}
