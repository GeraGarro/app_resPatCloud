package com.appResP.residuosPatologicos.models;

import com.appResP.residuosPatologicos.models.enums.Meses;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity


@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"mes", "año", "id_transportista"})
})public class Certificado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Transportista.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transportista")
    private Transportista transportista;

    @Enumerated(EnumType.STRING)
    private Meses mes;
    private int año;

    @OneToMany(targetEntity = Ticket_control.class, fetch = FetchType.EAGER, mappedBy = "certificado")
    @JsonIgnore
    private List<Ticket_control> listaTickets;

}
