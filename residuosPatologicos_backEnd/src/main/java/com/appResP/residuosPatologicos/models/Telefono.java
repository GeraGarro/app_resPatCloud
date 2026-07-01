package com.appResP.residuosPatologicos.models;

import com.appResP.residuosPatologicos.models.enums.TipoTelefono;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Telefono {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    @Enumerated(EnumType.STRING)
    private TipoTelefono tipo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generador_id", nullable = false)
    private Generador generador;

    private boolean estado;
}
