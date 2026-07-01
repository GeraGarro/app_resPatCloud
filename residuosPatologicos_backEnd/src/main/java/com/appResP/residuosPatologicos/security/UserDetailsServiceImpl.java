package com.appResP.residuosPatologicos.security;

import com.appResP.residuosPatologicos.models.Usuario;
import com.appResP.residuosPatologicos.models.enums.EstadoCuenta;
import com.appResP.residuosPatologicos.models.enums.Rol;
import com.appResP.residuosPatologicos.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email));

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        );
    }

    public boolean isUserEnabledForAccess(String email) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> usuario.getRol() != Rol.TRANSPORTISTA
                        || usuario.getEstadoCuenta() == null
                        || usuario.getEstadoCuenta() == EstadoCuenta.APROBADO)
                .orElse(false);
    }

}
