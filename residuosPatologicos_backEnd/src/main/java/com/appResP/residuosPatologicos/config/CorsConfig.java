package com.appResP.residuosPatologicos.config;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://vps-4679263-x.dattaweb.com",
                        "http://localhost:4200"
                )
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(@NonNull CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOriginPatterns(
//                                "http://localhost",        // Añade esto
//                                "http://localhost:*",      // Todos los puertos
//                                "http://127.0.0.1",
//                                "http://127.0.0.1:*",
//                                "http://localhost:4200",
//                                "http://localhost:80",
//                                "http://angular_respat:80",
//                                "http://frontEnd:80",
//                                "http://app_respat:8080",
//                                "http://vps-4679263-x.dattaweb.com:80",
//                                "https://vps-4679263-x.dattaweb.com:443",
//                                "http://149.50.147.200:80",
//                                "https://149.50.147.200:443"
//                        )
//                        .allowedMethods("*")
//                        .allowedHeaders("*")
//                        .exposedHeaders("Authorization")  // Asegúrate de exponer headers necesarios
//                        .allowCredentials(true)
//                        .maxAge(3600);
//            }
//        };
//    }
//}