package com.appResP.residuosPatologicos.models;

import com.appResP.residuosPatologicos.models.embeddables.Domicilio;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

public class Transportista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_transportista")
    private Long idTransportista;

    @NonNull
    private String nombre;

    @NonNull
    private String apellido;

    private String nombreFantasia;

    @Column(unique = true, length = 11)
    private String cuit;

    @Column(unique = true, length = 11)
    private String cuil;
    private String telefono;

    @NonNull
    @Column(unique = true, nullable = false)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;


    @Embedded
    private Domicilio domicilio;
    private boolean estado;

    @OneToMany(targetEntity = TicketControl.class, fetch = FetchType.LAZY,mappedBy = "transportista")
    @JsonIgnore
    @Builder.Default
    private List<TicketControl> listaTickets = new ArrayList<>();

    @OneToMany(targetEntity = Certificado.class, fetch = FetchType.LAZY,mappedBy="transportista")
    @JsonIgnore

    private List <Certificado> listaCertificados;

    @OneToMany (mappedBy = "transportista", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Vehiculo> vehiculos= new ArrayList<>();

    @OneToMany(mappedBy = "transportista", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Generador> generadores = new ArrayList<>();

}
