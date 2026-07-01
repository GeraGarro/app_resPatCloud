package com.appResP.residuosPatologicos.models;

import com.appResP.residuosPatologicos.models.enums.Meses;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity


@Table(
        name = "certificado",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_certificado_periodo_transportista",
                        columnNames = {"mes", "anio", "id_transportista"}
                ),
                @UniqueConstraint(
                        name = "uk_certificado_numero_transportista",
                        columnNames = {"numero_certificado", "id_transportista"}
                )
        },
        indexes = {
                @Index(name = "idx_cert_transportista_periodo", columnList = "id_transportista, anio, mes")
        }
)
public class Certificado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_certificado")
    private Long numeroCertificado;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_transportista", nullable = false)
    private Transportista transportista;

    @Enumerated(EnumType.STRING)
    private Meses mes;

    private int anio;

}
