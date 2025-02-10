package com.appResP.residuosPatologicos.services.imp;

import com.appResP.residuosPatologicos.exceptions.UniqueConstraintViolationException;
import com.appResP.residuosPatologicos.models.Certificado;
import com.appResP.residuosPatologicos.models.Ticket_control;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.enums.Meses;
import com.appResP.residuosPatologicos.persistence.ICertificadoDAO;
import com.appResP.residuosPatologicos.persistence.ITicketControlDAO;
import com.appResP.residuosPatologicos.persistence.ITransportistaDAO;
import com.appResP.residuosPatologicos.services.ICertificado_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Certificado_service implements ICertificado_service {
    @Autowired
    ICertificadoDAO certificadoDAO;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    ITransportistaDAO transportistaDAO;

    @Autowired
    ITicketControlDAO ticketDao;

    @Override
    public Optional<Certificado> findByID(Long id) {
        return certificadoDAO.findByID(id);
    }

    @Override
    public List<Certificado> findAll() {
        return certificadoDAO.findAll();
    }


    @Override
    public void save(Certificado certificado) {
        certificadoDAO.save(certificado);
    }

   /* @Scheduled( cron = "0 0 0 1 * ?") //cada dia de mes a medianoche
    public  void crearCertificadoMensual() {
        LocalDate hoy = LocalDate.now();
        int anio = hoy.minusDays(1).getYear();
        Meses mesAnterior = Meses.values()[hoy.minusDays(1).getMonthValue()];

        int mesAnteriorId = hoy.minusMonths(1).getMonthValue();
        //Obtengo la Lista de Transportsitas para crear un certificado a cada uno
        List<Transportista> transportistas = transportistaDAO.findAll();
        for (Transportista transportista : transportistas) {
            Long transportistaId = transportista.getId_transportista();

            //verificacion si el certificado ya existe
            if (wouldViolateConstraint(anio, mesAnteriorId, transportistaId, null)) {
                System.out.println("El certificado ya existe para el transportista " + transportistaId + " en el periodo: " + mesAnteriorId + "/" + anio);
                continue; // Saltar a la siguiente iteración si ya existe
            }

            //crear Certificado

            Certificado certificado = Certificado.builder()
                    .mes(Meses.values()[mesAnteriorId - 1])
                    .año(anio)
                    .transportista(transportista)
                    .build();

            save(certificado);

            List<Ticket_control> ticketsDelMesAnterior = ticketDao.findTicketsByPeriodo(anio, mesAnteriorId, transportistaId);

            for (Ticket_control ticket : ticketsDelMesAnterior) {
                ticket.setCertificado(certificado);
                ticketDao.save(ticket);
            }
        }

    }*/


    /**
     * Método para verificar y crear certificados si es necesario.
     */
   public void verificarYCrearCertificadosSiEsNecesario() {
       LocalDate hoy = LocalDate.now();
       int anioActual = hoy.getYear();
       int mesActual = hoy.getMonthValue();

       // Obtener todos los transportistas
       List<Transportista> transportistas = transportistaDAO.findAll();

       //Si es enero(mes 1), verificamos diciembre del anio anterior
       int mesAnteriorId = (mesActual == 1) ? 12 : mesActual - 1;
       int anioVerif = (mesActual == 1) ? anioActual - 1 : anioActual;


       //Obtener la Lista de Transportistas
       List<Transportista> transportitas = transportistaDAO.findAll();
       for (Transportista transportista : transportistas) {
           Long transportistaId = transportista.getId_transportista();


           //verificacion si el certificado ya existe
           boolean certificadoExistente = wouldViolateConstraint(anioVerif, mesAnteriorId, transportistaId, null);
           if (certificadoExistente) {
               System.out.println("El certificado ya existe para el transportista " + transportistaId + " en el periodo: " + mesAnteriorId + "/" + anioVerif);
               continue;
               // Saltar a la siguiente iteración si ya existe
           }
           try {
               //Crear el certificado

               Certificado certificado = Certificado.builder()
                       .mes(Meses.values()[mesAnteriorId - 1])
                       .año(anioVerif)
                       .transportista(transportista)
                       .build();

               save(certificado);

               //Asociar los tickets del mes anterior al certificado
               List<Ticket_control> ticketsDelMesAnterior = ticketDao.findTicketsByPeriodo(anioVerif, mesAnteriorId, transportistaId);

               for (Ticket_control ticket : ticketsDelMesAnterior) {
                   ticket.setCertificado(certificado);
                   ticketDao.save(ticket);
               }

               System.out.println("Certificado creado para el transportista " + transportistaId +
                       " en el periodo: " + mesAnteriorId + "/" + anioVerif);
           } catch (DataIntegrityViolationException e) {
               // Si se intenta insertar un duplicado, se captura la excepción y no se hace nada
               System.out.println("Error: Ya existe un certificado para el transportista " + transportistaId + " en el periodo: " + mesAnteriorId + "/" + anioVerif);
           }

       }

   }

    /**
     * Método programado para crear certificados mensuales.
     */
    @Scheduled(cron = "0 0 0 1 * ?") // Ejecutar el primer día de cada mes a medianoche
    public void crearCertificadoMensual() {
        verificarYCrearCertificadosSiEsNecesario();
    }




    @Override
    public void deletebyID(Long id) {
        certificadoDAO.deletebyID(id);
    }

    @Override
    public List<Certificado>findListByTransportista(Long idTransportista) {
             List<Certificado> certificadosTodos=certificadoDAO.findAll();
          return certificadosTodos.stream().filter(certificado ->
                          certificado.getTransportista().getId_transportista().equals(idTransportista))
                        .collect(Collectors.toList());
    }

    @Override
    public void update(Certificado certificado) {

        // Valida la restricción antes de guardar:
        if (wouldViolateConstraint(certificado.getAño(), certificado.getMes().getId(), certificado.getTransportista().getId_transportista(), certificado.getId())) {
            throw new UniqueConstraintViolationException("Cambiar el año y el mes violaría una restricción única.");
        }

        certificadoDAO.save(certificado);
    }


    public boolean wouldViolateConstraint(int año, int mesId, Long transportistaId, Long currentCertificadoId) {
        String sql = "SELECT COUNT(*) FROM certificado WHERE año = ? AND mes = ? AND id_transportista = ? AND id != ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, año, mesId, transportistaId, currentCertificadoId) > 0;
    }


}


