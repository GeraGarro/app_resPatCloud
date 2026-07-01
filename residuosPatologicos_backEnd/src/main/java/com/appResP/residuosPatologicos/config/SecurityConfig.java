package com.appResP.residuosPatologicos.config;

import com.appResP.residuosPatologicos.security.UserDetailsServiceImpl;
import com.appResP.residuosPatologicos.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))   // ✅ HABILITA CORS
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests( auth -> auth

                        //Rutas Públicas
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        //Solo GENERADOR
                        .requestMatchers("/api/certificados/**").hasRole("TRANSPORTISTA")
                        .requestMatchers("/api/generadores/**").hasAnyRole("TRANSPORTISTA", "ADMIN")
                        //Solo Transportista
                        .requestMatchers("/api/hojas-ruta/**").hasAnyRole("TRANSPORTISTA", "ADMIN")
                        .requestMatchers("/api/tipos-residuo/**").hasAnyRole("TRANSPORTISTA", "ADMIN")
                        //ambos
                        .requestMatchers("/api/residuos/**").hasAnyRole("TRANSPORTISTA", "GENERADOR")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

@Bean
    public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
}

@Bean
public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception{
    return config.getAuthenticationManager();

}

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new org.springframework.web.cors.CorsConfiguration();

        config.setAllowedOrigins(java.util.List.of(
                "http://localhost",
                "http://localhost:80",
                "http://localhost:4200"
        ));
        config.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("*"));
        config.setAllowCredentials(true);

        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
