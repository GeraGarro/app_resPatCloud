package com.appResP.residuosPatologicos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

public class Generador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String cuit;
    private String direccion;
    private String legajo;
    private String telefono;
    private boolean estado;

    @OneToMany(targetEntity = Ticket_control.class, fetch = FetchType.LAZY,mappedBy = "generador")
    @JsonIgnore
    private List <Ticket_control> listaTickets;
}
