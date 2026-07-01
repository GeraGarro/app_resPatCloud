package com.appResP.residuosPatologicos.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
@Entity
@Table(
        name = "ticket_control",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ticket_numero_transportista",
                columnNames = {"numero_ticket", "id_transportista"}
        )
)
public class TicketControl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket")
    private Long id;

    @Column(name = "numero_ticket")
    private Long numeroTicket;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transportista", nullable = false)
    private Transportista transportista;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name= "id_hoja_ruta", nullable = false)
    private HojaRuta hojaRuta;

    @NotNull
    @Column(nullable=false)
    private LocalDate fechaEmision;


    @NotNull
    @Column(nullable=false)
    private LocalTime horario;

    @Column(nullable = false)
    private boolean estado;

    @NotNull
    @ManyToOne  ( fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_generador", nullable = false)
    private Generador generador;


    @OneToMany(
            mappedBy = "ticketControl",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
   private  List<Residuo> listaResiduos = new ArrayList<>();


    @Column(name = "peso_total")
    @Builder.Default
    private BigDecimal pesoTotal = BigDecimal.ZERO;



    // Opcional: helpers para mantener sincronizada la relación bidireccional
    public void addResiduo(Residuo r) {
        listaResiduos.add(r);
        r.setTicketControl(this);
    }

    public void removeResiduo(Residuo r) {
        listaResiduos.remove(r);
        r.setTicketControl(null);
    }

    public void recalcularPesoTotal() {
        this.pesoTotal = listaResiduos.stream()
                .map(Residuo::getPeso)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
