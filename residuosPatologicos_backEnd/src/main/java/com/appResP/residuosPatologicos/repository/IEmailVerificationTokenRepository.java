package com.appResP.residuosPatologicos.repository;

import com.appResP.residuosPatologicos.models.EmailVerificationToken;
import com.appResP.residuosPatologicos.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);

    List<EmailVerificationToken> findByUsuarioAndUsadoFalse(Usuario usuario);
}
