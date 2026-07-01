package com.appResP.residuosPatologicos.DTO.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TicketRequestDTO {

    @NotNull private Long transportistaId;
    @NotNull private Long hojaRutaId;
    @NotNull
    private Long generadorId;

    // si querés setearla desde backend, podés omitirla en request
    private LocalDate fechaEmision;
    private LocalTime horario;


    private Boolean estado;

    @Builder.Default
    @Valid
    private List<ResiduoRequestDTO> residuos = new ArrayList<>();
}
