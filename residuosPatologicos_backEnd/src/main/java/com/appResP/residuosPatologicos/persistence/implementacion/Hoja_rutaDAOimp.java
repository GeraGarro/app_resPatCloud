package com.appResP.residuosPatologicos.persistence.implementacion;

import com.appResP.residuosPatologicos.models.Hoja_ruta;
import com.appResP.residuosPatologicos.persistence.IHoja_rutaDAO;
import com.appResP.residuosPatologicos.persistence.repositories.IHoja_ruta_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Component
public class Hoja_rutaDAOimp implements IHoja_rutaDAO {
    @Autowired
    IHoja_ruta_Repository hoja_ruta_repository;



 /*  @Override
   @Scheduled(cron = "0 0 0 * * MON")
   public void crearHojaRutaSemanal() {
       LocalDate hoy = LocalDate.now();

       // Obtenemos la última hoja de ruta registrada
       Hoja_ruta ultimaHojaRuta = hoja_ruta_repository.findTopByOrderByFechaFinDesc().orElse(null);
       LocalDate fechaInicioNuevaHoja;

       if (ultimaHojaRuta == null) {
           // Si no hay hojas de ruta previas, iniciar la primera hoy
           fechaInicioNuevaHoja = hoy;
       } else {
           // La nueva hoja de ruta comienza un día después del fin de la última hoja
           fechaInicioNuevaHoja = ultimaHojaRuta.getFechaFin().plusDays(1);
       }

       // Calcular el fin de la nueva hoja de ruta, terminando en el próximo domingo
       LocalDate fechaFinNuevaHoja = fechaInicioNuevaHoja.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

       // Ajuste si la hoja de ruta cruza al siguiente mes
       if (fechaInicioNuevaHoja.getMonth() != fechaFinNuevaHoja.getMonth()) {
           fechaFinNuevaHoja = fechaInicioNuevaHoja.with(TemporalAdjusters.lastDayOfMonth());
       }

       // Verificar si ya existe una hoja de ruta con las mismas fechas
       if (hoja_ruta_repository.findByFechaInicioAndFechaFin(fechaInicioNuevaHoja, fechaFinNuevaHoja).isEmpty()) {
           // Crear y guardar la nueva hoja de ruta si no existe duplicado
           Hoja_ruta nuevaHojaRuta = Hoja_ruta.builder()
                   .fechaInicio(fechaInicioNuevaHoja)
                   .fechaFin(fechaFinNuevaHoja)
                   .build();

           hoja_ruta_repository.save(nuevaHojaRuta);
       } else {
           System.out.println("La hoja de ruta con las fechas especificadas ya existe. No se creará una nueva.");
       }

       // Si la hoja de ruta termina en el último día del mes, iniciar una nueva hoja de ruta al día siguiente
       if (fechaFinNuevaHoja.equals(fechaFinNuevaHoja.with(TemporalAdjusters.lastDayOfMonth()))) {
           LocalDate inicioProximaHoja = fechaFinNuevaHoja.plusDays(1);
           LocalDate finProximaHoja = inicioProximaHoja.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

           // Verificar si existe ya la próxima hoja de ruta
           if (hoja_ruta_repository.findByFechaInicioAndFechaFin(inicioProximaHoja, finProximaHoja).isEmpty()) {
               Hoja_ruta proximaHojaRuta = Hoja_ruta.builder()
                       .fechaInicio(inicioProximaHoja)
                       .fechaFin(finProximaHoja)
                       .build();

               hoja_ruta_repository.save(proximaHojaRuta);
           } else {
               System.out.println("La próxima hoja de ruta con las fechas especificadas ya existe. No se creará una nueva.");
           }
       }
   }*/

    @Override
    @Scheduled(cron = "0 0 0 * * MON") // Ejecutar los lunes a las 00:00 horas
    public void crearHojaRutaSemanal() {
        verificarYCrearHojaRutaSiEsNecesario();
    }

    /**
     * Método que verifica y crea la hoja de ruta en caso de que no exista.
     */
    public void verificarYCrearHojaRutaSiEsNecesario() {
        LocalDate hoy = LocalDate.now();

        // Obtener la última hoja de ruta registrada
        Hoja_ruta ultimaHojaRuta = hoja_ruta_repository.findTopByOrderByFechaFinDesc().orElse(null);
        LocalDate fechaInicioNuevaHoja;

        if (ultimaHojaRuta == null) {
            // Si no hay hojas de ruta previas, iniciar la primera hoy
            fechaInicioNuevaHoja = hoy;
        } else {
            // La nueva hoja de ruta comienza un día después del fin de la última hoja
            fechaInicioNuevaHoja = ultimaHojaRuta.getFechaFin().plusDays(1);

            // Si hoy está dentro del rango de la última hoja de ruta, no hacer nada
            if (!hoy.isAfter(ultimaHojaRuta.getFechaFin())) {
                System.out.println("La hoja de ruta existente aún es válida. No se requiere crear una nueva.");
                return;
            }
        }

        // Calcular el fin de la nueva hoja de ruta, terminando en el próximo domingo
        LocalDate fechaFinNuevaHoja = fechaInicioNuevaHoja.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Ajuste si la hoja de ruta cruza al siguiente mes
        if (fechaInicioNuevaHoja.getMonth() != fechaFinNuevaHoja.getMonth()) {
            fechaFinNuevaHoja = fechaInicioNuevaHoja.with(TemporalAdjusters.lastDayOfMonth());
        }

        // Verificar si ya existe una hoja de ruta con las mismas fechas
        if (hoja_ruta_repository.findByFechaInicioAndFechaFin(fechaInicioNuevaHoja, fechaFinNuevaHoja).isEmpty()) {
            // Crear y guardar la nueva hoja de ruta si no existe duplicado
            Hoja_ruta nuevaHojaRuta = Hoja_ruta.builder()
                    .fechaInicio(fechaInicioNuevaHoja)
                    .fechaFin(fechaFinNuevaHoja)
                    .build();

            hoja_ruta_repository.save(nuevaHojaRuta);
            System.out.println("Hoja de ruta creada: " + nuevaHojaRuta);
        } else {
            System.out.println("La hoja de ruta con las fechas especificadas ya existe. No se creará una nueva.");
        }

        // Si la hoja de ruta termina en el último día del mes, iniciar una nueva hoja de ruta al día siguiente
        if (fechaFinNuevaHoja.equals(fechaFinNuevaHoja.with(TemporalAdjusters.lastDayOfMonth()))) {
            LocalDate inicioProximaHoja = fechaFinNuevaHoja.plusDays(1);
            LocalDate finProximaHoja = inicioProximaHoja.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            // Verificar si existe ya la próxima hoja de ruta
            if (hoja_ruta_repository.findByFechaInicioAndFechaFin(inicioProximaHoja, finProximaHoja).isEmpty()) {
                Hoja_ruta proximaHojaRuta = Hoja_ruta.builder()
                        .fechaInicio(inicioProximaHoja)
                        .fechaFin(finProximaHoja)
                        .build();

                hoja_ruta_repository.save(proximaHojaRuta);
                System.out.println("Hoja de ruta creada para el siguiente mes: " + proximaHojaRuta);
            } else {
                System.out.println("La próxima hoja de ruta con las fechas especificadas ya existe. No se creará una nueva.");
            }
        }
    }




    @Override
    public Hoja_ruta obtenerHojaRutaPorFecha(LocalDate fechaEmision) {
        return null;

    }

    @Override
    public Optional<Hoja_ruta> findHojaRutaForCurrentDate() {
       LocalDate hoy= LocalDate.now();

        return hoja_ruta_repository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(hoy,hoy);
    }

    @Override
    public Optional<Hoja_ruta> findById(Long id) {
        return hoja_ruta_repository.findById(id);
    }
}
