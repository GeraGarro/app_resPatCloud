package com.appResP.residuosPatologicos.models;

import com.appResP.residuosPatologicos.models.enums.Rol;
import com.appResP.residuosPatologicos.models.enums.EstadoCuenta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter @Getter
@Table(name = "usuarios")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Builder.Default
    @Column(name = "email_verificado")
    private Boolean emailVerificado = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cuenta", length = 32)
    private EstadoCuenta estadoCuenta = EstadoCuenta.APROBADO;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    void prePersist() {
        if (emailVerificado == null) {
            emailVerificado = false;
        }

        if (estadoCuenta == null) {
            estadoCuenta = EstadoCuenta.APROBADO;
        }
    }
}
