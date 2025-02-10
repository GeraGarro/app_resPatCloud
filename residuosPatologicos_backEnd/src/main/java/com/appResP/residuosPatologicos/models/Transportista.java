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

public class Transportista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(unique = true)
    private Long id_transportista;

    private String nombre;
    private String apellido;
    private String cuit;
    private String telefono;
    private String domicilio;
    private boolean estado;

    @OneToMany(targetEntity = Ticket_control.class, fetch = FetchType.LAZY,mappedBy = "transportista")
    @JsonIgnore
    private List <Ticket_control> listaTickets;

    @OneToMany(targetEntity = Certificado.class, fetch = FetchType.LAZY,mappedBy="transportista")
    @JsonIgnore
    private List <Certificado> listaCertificados;


}
