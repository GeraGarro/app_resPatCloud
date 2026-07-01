package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.DTO.request.TransportistaRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.TransportistaDTO;
import com.appResP.residuosPatologicos.DTO.response.TransportistaProfileStatusDTO;

import java.util.List;

public interface ITransportistaService {

    public TransportistaDTO findByID(Long id);

    public List<TransportistaDTO> findAll();


    public TransportistaDTO save(TransportistaRequestDTO transportistaDto);

    public void deletebyId(Long id);

    public TransportistaDTO update(Long id, TransportistaRequestDTO dto);

    TransportistaDTO findAuthenticated();

    TransportistaProfileStatusDTO getAuthenticatedProfileStatus();

}
