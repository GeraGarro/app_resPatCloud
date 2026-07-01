package com.appResP.residuosPatologicos.models;

import com.appResP.residuosPatologicos.models.embeddables.Domicilio;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@Entity
@Inheritance (strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_generador")
@SuperBuilder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public abstract class Generador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = true)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportista_id")
    private Transportista transportista;

    private String legajo;


    @Column(nullable = false)
    @Builder.Default
    private boolean estado=true;

    @Embedded
    private Domicilio domicilio;

    @Builder.Default
    @OneToMany(mappedBy = "generador",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Telefono> telefonos= new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany( fetch = FetchType.LAZY,
            mappedBy = "generador"
    )
    private List <TicketControl> listaTickets = new ArrayList<>();;

    public void setEmail(String email) {
        this.email = normalizeOptional(email);
    }

    @PrePersist
    @PreUpdate
    private void normalizeBeforeSave() {
        email = normalizeOptional(email);
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
