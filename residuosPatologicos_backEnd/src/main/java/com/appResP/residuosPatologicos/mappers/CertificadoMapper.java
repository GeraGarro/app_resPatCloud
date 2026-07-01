package com.appResP.residuosPatologicos.mappers;

import com.appResP.residuosPatologicos.DTO.response.CertificadoDTO;
import com.appResP.residuosPatologicos.DTO.response.CertificadoDetalleDTO;
import com.appResP.residuosPatologicos.models.Certificado;
import com.appResP.residuosPatologicos.repository.IHojaRutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CertificadoMapper {

    private final HojaRutaMapper hojaRutaMapper;
    private final IHojaRutaRepository hojaRutaRepository;

    //para listados
    public CertificadoDTO toDTO(Certificado c) {
        if (c == null) return null;

        return  CertificadoDTO.builder()
                .id(c.getId())
                .numeroCertificado(c.getNumeroCertificado())
                .transportistaId(
                        c.getTransportista() != null
                            ? c.getTransportista().getIdTransportista()
                            : null
                )
                .mes(c.getMes())
                .anio(c.getAnio())
                .cantidadHojasRuta(contarHojasDelPeriodo(c))
                .build();

    }

    // Para detalle — expone la lista de hojas
    public CertificadoDetalleDTO toDetalleDTO(Certificado c) {
        if (c == null) return null;

        return CertificadoDetalleDTO.builder()
                .id(c.getId())
                .numeroCertificado(c.getNumeroCertificado())
                .transportistaId(
                        c.getTransportista() != null
                                ? c.getTransportista().getIdTransportista()
                                : null
                )
                .mes(c.getMes())
                .anio(c.getAnio())
                .hojasRuta(c.getTransportista() == null
                        ? List.of()
                        : hojasDelPeriodo(c))
                .build();
    }

    private int contarHojasDelPeriodo(Certificado c) {
        if (c.getTransportista() == null || c.getMes() == null) {
            return 0;
        }

        YearMonth periodo = YearMonth.of(c.getAnio(), c.getMes().getId());

        return hojaRutaRepository.countDistinctByTransportistaAndFechaEmisionBetween(
                c.getTransportista().getIdTransportista(),
                periodo.atDay(1),
                periodo.atEndOfMonth()
        );
    }

    private List<com.appResP.residuosPatologicos.DTO.response.HojaRutaDTO> hojasDelPeriodo(Certificado c) {
        YearMonth periodo = YearMonth.of(c.getAnio(), c.getMes().getId());

        return hojaRutaRepository.findDistinctByTransportistaAndFechaEmisionBetween(
                        c.getTransportista().getIdTransportista(),
                        periodo.atDay(1),
                        periodo.atEndOfMonth()
                )
                .stream()
                .map(hojaRutaMapper::toDTO)
                .toList();
    }
}
