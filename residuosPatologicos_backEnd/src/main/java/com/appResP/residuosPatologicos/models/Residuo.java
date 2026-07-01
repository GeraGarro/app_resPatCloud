package com.appResP.residuosPatologicos.models;

import com.appResP.residuosPatologicos.repository.persistence.ResiduoListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
@Entity
@Table(
        name = "residuo",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ticket_tipo_residuo",
                columnNames = {"ticket_id", "tipo_residuo_id"}
        ),
        indexes = {
                @Index(name = "idx_residuo_ticket", columnList = "ticket_id"),
                @Index(name = "idx_residuo_tipo", columnList = "tipo_residuo_id")
        }
)
@EntityListeners(ResiduoListener.class) // 👈 Agregar esto
public class Residuo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

  private Long id;

 @NotNull
 @ManyToOne( fetch = FetchType.LAZY, optional = false)
 @JoinColumn(name = "tipo_residuo_id", nullable = false)
  private TipoResiduo tipoResiduo;

 @NotNull
 @DecimalMin(value= "0.0001", inclusive = true)
 @Column(nullable = false, precision = 12, scale= 3) //ej: 999999999.999
  private BigDecimal peso;

 @NotNull
  @ManyToOne(fetch=FetchType.LAZY, optional = false)
  @JoinColumn(name = "ticket_id", nullable = false)
  @JsonIgnore
  private TicketControl ticketControl;

}


