package com.appResP.residuosPatologicos.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Table(name = "email_verification_tokens")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    @Builder.Default
    @Column(nullable = false)
    private Boolean usado = false;

    private LocalDateTime fechaUso;
}
