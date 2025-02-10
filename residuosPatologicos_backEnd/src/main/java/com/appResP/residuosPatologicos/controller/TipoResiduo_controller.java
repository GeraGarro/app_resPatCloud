package com.appResP.residuosPatologicos.controller;
import com.appResP.residuosPatologicos.DTO.TipoResiduoDTO;
import com.appResP.residuosPatologicos.models.Tipo_residuo;
import com.appResP.residuosPatologicos.services.imp.TipoResiduo_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RestController

@RequestMapping("/api/tipoResiduo")

public class TipoResiduo_controller {

    @Autowired
    TipoResiduo_Service tipoResiduoService;

    //Muestra el un unico Elemento de Tipo de Residuo
@GetMapping("/unTipoResiduo/{id}")
    public ResponseEntity <?> findTipoResiduobyId(@PathVariable Long id){
    Optional<Tipo_residuo> tipoResiduoOptional=tipoResiduoService.findByID(id);
if (tipoResiduoOptional.isPresent()){
    Tipo_residuo tipoResiduo=tipoResiduoOptional.get();
    TipoResiduoDTO tipoResiduoDTO= TipoResiduoDTO.builder()
            .id(tipoResiduo.getId())
            .nombre(tipoResiduo.getNombre())
            .codigo(tipoResiduo.getCodigo())
            .estado(tipoResiduo.isEstado()).build();

  return ResponseEntity.ok(tipoResiduoDTO);
}
return ResponseEntity.notFound().build();
}
//Muestra todos los tipos de residuos
@GetMapping("/verTodos")
    public ResponseEntity <?> findTipoAll (){
    List<TipoResiduoDTO> listaDeTipos= tipoResiduoService.findAll().stream()
            .map(tipoResiduo -> TipoResiduoDTO.builder()
                    .id(tipoResiduo.getId())
                    .nombre(tipoResiduo.getNombre())
                    .estado(tipoResiduo.isEstado())
                    .codigo(tipoResiduo.getCodigo())
                    .build()).toList();
    return ResponseEntity.ok(listaDeTipos);
    }

    //Creación de un Nuevo Tipo de Residuo
    @PostMapping("/crear")
    public ResponseEntity <?> saveTipoResiduo(@RequestBody TipoResiduoDTO tipoResiduoDTO) throws URISyntaxException {

    try{
        if(tipoResiduoDTO.getNombre().isBlank()){

            return ResponseEntity.badRequest().body((Map.of("message","Faltan Datos Por Ingresar")));


        }
        Tipo_residuo tipoResiduo= Tipo_residuo.builder()
                .nombre(tipoResiduoDTO.getNombre())
                .codigo(tipoResiduoDTO.getCodigo())
                .estado(tipoResiduoDTO.isEstado())
                .build();

        tipoResiduoService.save(tipoResiduo);
        URI location= URI.create("/api/tipoResiduo/crear");
        Map<String,String> response= new HashMap<>();
        response.put("message", " Nueva Clasificación de Residuo Incorporado");
        return  ResponseEntity.created(location).body(response);
    }catch (Exception e){
        return  ResponseEntity.badRequest().body(Map.of("message","Error, no es posible Realizar la incorporacion:"+e));
    }

    }


    //Eliminacion de Un Tipo de Residuo
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity <?> deleteTipoResiduo(@PathVariable Long id){
    if(id!=null){
        tipoResiduoService.deletebyID(id);
        return ResponseEntity.ok("El Tipo Residuo con "+id+" ha sido Eliminado");
    }
    return ResponseEntity.notFound().build();
    }
//Actualizacion de un Elemento de Tipo Residuo
  @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTipoResiduo(@PathVariable Long id, @RequestBody TipoResiduoDTO tipoResiduoDTO){
    Optional<Tipo_residuo> tipoResiduoOptional=tipoResiduoService.findByID(id);
try {
    if(tipoResiduoOptional.isPresent()){
        Tipo_residuo tipoResiduoEdit=tipoResiduoOptional.get();

        tipoResiduoEdit.setNombre(tipoResiduoDTO.getNombre());
        tipoResiduoEdit.setCodigo(tipoResiduoDTO.getCodigo());
        tipoResiduoEdit.setEstado(tipoResiduoDTO.isEstado());

        tipoResiduoService.save(tipoResiduoEdit);

        URI location= URI.create("/api/tipoResiduo/update/"+id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Generador Modificado");
        return ResponseEntity.ok(response);
    } else {
        return ResponseEntity.badRequest().body("El generador con ID " + id + " no existe");
    }
}catch (Exception e) {
    return ResponseEntity.badRequest().body("No ha sido posible realizar la modificación "+e);
}
  }

  @PatchMapping("/cambioEstado/{id}")
    public ResponseEntity <?> cambioEstadoTipo(@PathVariable Long id, @RequestParam boolean nuevoEstado){
    try {
        Optional<Tipo_residuo> tipoOptional= tipoResiduoService.findByID(id);

        if(tipoOptional.isPresent()){
            Tipo_residuo tipoResiduo=tipoOptional.get();

            //cambiar estado tipo de residuo
            tipoResiduo.setEstado(nuevoEstado);

            //Guardar los cambios
            tipoResiduoService.save(tipoResiduo);

            //Construir la respuesta
            Map<String, Object> response=new HashMap<>();
            response.put("message","Estado actualizado correctamente");
            response.put("id",tipoResiduo.getId());
            response.put("nuevoEstado", tipoResiduo.isEstado());

            return  ResponseEntity.ok(response);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message","El tipo residuo con ID "+id+" no existe"));
        }

    }catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error al intentar cambiar el estado del generador"));
  }
}
}