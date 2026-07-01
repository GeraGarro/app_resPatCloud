package com.appResP.residuosPatologicos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "hoja_ruta",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fecha_inicio", "id_transportista"}),
                @UniqueConstraint(name = "uk_hoja_ruta_numero_transportista", columnNames = {"numero_hoja_ruta", "id_transportista"})
        }
)
public class HojaRuta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "numero_hoja_ruta")
    private Long numeroHojaRuta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transportista")
    private Transportista transportista;

    @JsonIgnore
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hojaRuta")
    private List<TicketControl> listaTickets = new ArrayList<>();
}
