package com.appResP.residuosPatologicos.repository;

import com.appResP.residuosPatologicos.models.Usuario;
import com.appResP.residuosPatologicos.models.enums.EstadoCuenta;
import com.appResP.residuosPatologicos.models.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByRolAndEstadoCuentaOrderByIdDesc(Rol rol, EstadoCuenta estadoCuenta);
}
