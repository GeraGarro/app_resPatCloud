package com.appResP.residuosPatologicos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
@Entity
@Table(
        name = "tipo_residuo",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tipo_residuo_codigo_transportista", columnNames = {"codigo", "id_transportista"})
        }
)
public class TipoResiduo  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String codigo;

    @NotBlank
    @Column(name = "nombre_tipo", nullable = false, length = 120)
    private String nombre;

    @Column(name = "estado_actividad", nullable = false)
    private boolean estadoActividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transportista")
    private Transportista transportista;

    @Builder.Default
    @OneToMany( fetch = FetchType.LAZY,mappedBy = "tipoResiduo")
    @JsonIgnore
    private List<Residuo> listaResiduos = new ArrayList<>();
}
