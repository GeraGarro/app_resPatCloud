package com.appResP.residuosPatologicos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Ticket_control {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_Ticket;

    @ManyToOne(targetEntity = Transportista.class, fetch = FetchType.LAZY)

    @JoinColumn(name = "id_transportista")
    private Transportista transportista;

    @ManyToOne(targetEntity = Hoja_ruta.class, fetch = FetchType.LAZY)
    @JoinColumn( nullable = false)
    private Hoja_ruta hojaRuta;

    @JoinColumn(name = "fecha_emision")
    private LocalDate fechaEmision;
    private boolean estado;

    @ManyToOne  (targetEntity = Generador.class, fetch = FetchType.LAZY)
    private Generador generador;

    @OneToMany(mappedBy = "ticketControl",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    List<Residuo> listaResiduos;

    @ManyToOne(targetEntity = Certificado.class, fetch = FetchType.EAGER)
    private Certificado certificado;



}
