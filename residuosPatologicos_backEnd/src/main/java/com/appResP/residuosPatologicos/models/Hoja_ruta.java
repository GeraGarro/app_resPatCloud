package com.appResP.residuosPatologicos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Hoja_ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;

    @AssertTrue(message = "La diferencia entre las fechas debe ser menor o igual a 7 días")
    public boolean isFechaDentroDeSemana() {
        return fechaInicio != null && fechaFin != null &&
                fechaInicio.plusDays(7).compareTo(fechaFin) >= 0;
    }

    @OneToMany(targetEntity = Ticket_control.class, fetch = FetchType.LAZY, mappedBy = "hojaRuta")
    @JsonIgnore
    private List<Ticket_control> listaTickets;

}
