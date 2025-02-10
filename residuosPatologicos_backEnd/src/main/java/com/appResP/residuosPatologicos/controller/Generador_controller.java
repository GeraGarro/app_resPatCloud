package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.GeneradorDTO;
import com.appResP.residuosPatologicos.models.Generador;
import com.appResP.residuosPatologicos.services.imp.Generador_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController

@RequestMapping("/api/Generador")

public class Generador_controller{

    @Autowired
    private Generador_service generadorService;

    @GetMapping("/infogenerador/{id}")
    public ResponseEntity<?> findByGeneradorId(@PathVariable Long id){
        Optional<Generador> generadorOptional=generadorService.findByID(id);

        if (generadorOptional.isPresent()) {
            Generador generador = generadorOptional.get();
            GeneradorDTO generadorDTO = GeneradorDTO.builder()
                    .id(generador.getId())
                    .nombre(generador.getNombre())
                    .direccion(generador.getDireccion())
                    .cuit(generador.getCuit())
                    .estado(generador.isEstado())
                    .telefono(generador.getTelefono())
                    .legajo(generador.getLegajo())
                    .estado(generador.isEstado())
                    .build();

            return ResponseEntity.ok(generadorDTO);
        }
        return ResponseEntity.notFound().build();
    }


   @GetMapping("/verTodos")
public ResponseEntity<?> findGeneradorAll(){
    List<GeneradorDTO> listaGeneradores= generadorService.findAll().stream()
            .map(generador -> GeneradorDTO.builder()
                    .id(generador.getId())
                    .nombre(generador.getNombre())
                    .direccion(generador.getDireccion())
                    .cuit(generador.getCuit())
                    .telefono(generador.getTelefono())
                    .legajo(generador.getLegajo())
                    .estado(generador.isEstado())
                    .build())
            .toList();
    return ResponseEntity.ok(listaGeneradores);
    }
@PostMapping("/crear")
    public ResponseEntity<?> saveGenerador(@RequestBody GeneradorDTO generadorDTO){

    try {

        if(generadorDTO.getNombre().isBlank()||
            generadorDTO.getCuit().isBlank()||
            generadorDTO.getDireccion().isBlank()){
            return ResponseEntity.badRequest().body(Map.of("message", "Faltan Datos Por Ingresar"));
         }

        Generador generador=Generador.builder()
                .nombre(generadorDTO.getNombre())
                .direccion(generadorDTO.getDireccion())
                .cuit(generadorDTO.getCuit())
                .telefono(generadorDTO.getTelefono())
                .legajo(generadorDTO.getLegajo())
                .estado(generadorDTO.isEstado())
                .build();

        generadorService.save(generador);
        URI location = URI.create("/api/generador/crear"); // Construir la URL
        Map<String, String> response = new HashMap<>();
        response.put("message", "Generador Incorporado");
        return ResponseEntity.created(location).body(response);
    }catch (Exception e){
        return ResponseEntity.badRequest().body(Map.of("message", "Faltante de Datos, no he posible Realizar la Incorporacion del Generador"));
    }

    }
@DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> deleteGenerador(@PathVariable Long id){
        if (id!=null){
            try {
                generadorService.deletebyID(id);

                return ResponseEntity.ok("El Generador con ID "+id+ " ha sido Eliminado");
            }catch (DataIntegrityViolationException e){
                String mensajeError = "No se puede eliminar el Generador con ID " + id + " ya que tiene restriccion por estar en Ticket";
                return ResponseEntity.status(HttpStatus.CONFLICT).body(mensajeError);

            }

        }
        return ResponseEntity.notFound().build();
}

@PutMapping("/update/{id}")
    public ResponseEntity<?> updateGenerador(@PathVariable Long id, @RequestBody GeneradorDTO generadorDTO){
        Optional<Generador> generadorOptional=generadorService.findByID(id);
try {
        if(generadorOptional.isPresent()){

            Generador generadorEdit=generadorOptional.get();

            generadorEdit.setNombre(generadorDTO.getNombre());
            generadorEdit.setDireccion(generadorDTO.getDireccion());
            generadorEdit.setCuit(generadorDTO.getCuit());
            generadorEdit.setTelefono(generadorDTO.getTelefono());
            generadorEdit.setLegajo(generadorDTO.getLegajo());
            generadorEdit.setEstado(generadorDTO.isEstado());
            generadorService.save(generadorEdit);

            URI location = URI.create("/api/generador/update/" + id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Generador Modificado");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body("El generador con ID " + id + " no existe");
        }
} catch (Exception e) {
    return ResponseEntity.badRequest().body("No ha sido posible realizar la modificación");
}
}

    @PatchMapping("/cambioEstado/{id}")
    public ResponseEntity<?> cambiarEstadoGenerador(@PathVariable Long id, @RequestParam boolean nuevoEstado) {
        try {
            Optional<Generador> generadorOptional = generadorService.findByID(id);

            if (generadorOptional.isPresent()) {
                Generador generador = generadorOptional.get();

                // Cambiar el estado del generador
                generador.setEstado(nuevoEstado);

                // Guardar los cambios en la base de datos
                generadorService.save(generador);

                // Construir la respuesta
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Estado actualizado correctamente");
                response.put("id", generador.getId());
                response.put("nuevoEstado", generador.isEstado());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "El generador con ID " + id + " no existe"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al intentar cambiar el estado del generador"));
        }
    }

    @GetMapping("/activos")
    public ResponseEntity<?> findGeneradoresActivos() {
        List <GeneradorDTO> listaGeneradoresActivos= generadorService.obtenerGeneradoresActivos().stream()
                .map(generador -> GeneradorDTO.builder()
                        .id(generador.getId())
                        .nombre(generador.getNombre())
                        .direccion(generador.getDireccion())
                        .cuit(generador.getCuit())
                        .telefono(generador.getTelefono())
                        .legajo(generador.getLegajo())
                        .build()).toList();
        return ResponseEntity.ok(listaGeneradoresActivos);

    }

    @GetMapping("/inactivos")
    public ResponseEntity<?> findGeneradoresInactivos() {
        List <GeneradorDTO> listaGeneradoresInactivos= generadorService.obtenerGeneradoresInactivos().stream()
                .map(generador -> GeneradorDTO.builder()
                        .id(generador.getId())
                        .nombre(generador.getNombre())
                        .direccion(generador.getDireccion())
                        .cuit(generador.getCuit())
                        .telefono(generador.getTelefono())
                        .legajo(generador.getLegajo())
                        .build()).toList();
        return ResponseEntity.ok(listaGeneradoresInactivos);

    }
}



