package com.appResP.residuosPatologicos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity


@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tipo_residuo", "ticket_control"})
})
public class Residuo implements Comparable<Residuo> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
  private Long id;
 @ManyToOne( targetEntity = Tipo_residuo.class,fetch = FetchType.LAZY)

 @JoinColumn(name = "tipo_residuo", referencedColumnName = "id")
  private Tipo_residuo tipoResiduo;

  private float peso;

  @ManyToOne(optional = false,targetEntity = Ticket_control.class)
  @JoinColumn(name = "ticket_control")
  @JsonIgnore
  private Ticket_control ticketControl;

    @Override
    public int compareTo(Residuo otro) {
        // Compara los nombres de los tipos de residuo
        return otro.getTipoResiduo().getCodigo().compareTo(this.tipoResiduo.getCodigo());
    }


}


