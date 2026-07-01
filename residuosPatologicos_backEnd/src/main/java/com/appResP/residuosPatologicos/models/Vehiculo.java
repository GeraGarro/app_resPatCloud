package com.appResP.residuosPatologicos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "vehiculo",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_vehiculo_dominio" , columnNames = "dominio"),
            @UniqueConstraint(name= "uk_vechiulo_chasis", columnNames= "chasis")
          })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_vehiculo")
    private Long id;
    @Column(nullable = false, length = 60)
    private String marca;

    @Column(nullable = false, length = 60)
    private String modelo;

    @Column(nullable = false, length = 15, unique = true)
    private String dominio; // patente

    @Column(nullable = false, length = 50, unique = true)
    private String chasis;

    @Column(name = "anio")
    private Integer anio;

    @Column(nullable = false)
    private boolean activo =true;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_transportista", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vehiculo_transportista"))
    @JsonIgnore
    private Transportista transportista;
}
