package com.appResP.residuosPatologicos.security.jwt;


import com.appResP.residuosPatologicos.models.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    //-------------------------
    //GENERACIÓN DEL TOKEN
    //-------------------------

    public String generateToken (Usuario usuario) {
    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("rol", usuario.getRol().name());
    extraClaims.put("id", usuario.getId());

        return buildToken(extraClaims, usuario.getEmail() , jwtExpiration);
    }

    private String buildToken( Map<String, Object> extraClaims,
                               String subject,
                               long expiration
                                ) {
        return Jwts.builder()
                .claims(extraClaims)                                           // ← setClaims() en 0.11.x
                .subject(subject)                                              // ← setSubject() en 0.11.x
                .issuedAt(new Date(System.currentTimeMillis()))                // ← setIssuedAt() en 0.11.x
                .expiration(new Date(System.currentTimeMillis() + expiration)) // ← setExpiration() en 0.11.x
                .signWith(getSignInKey())                                       // ← ya no requiere algoritmo
                .compact();
    }

    // ─────────────────────────────────────────
    // VALIDACIÓN DEL TOKEN
    // ─────────────────────────────────────────

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    // ─────────────────────────────────────────
    // EXTRACCIÓN DE CLAIMS
    // ─────────────────────────────────────────

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public String extractRol(String token) {
        return extractClaim(token, claims -> claims.get("rol", String.class));
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()                              // ← parserBuilder() en 0.11.x
                .verifyWith(getSignInKey())               // ← setSigningKey() en 0.11.x
                .build()
                .parseSignedClaims(token)                 // ← parseClaimsJws() en 0.11.x
                .getPayload();
    }

    // ─────────────────────────────────────────
    // CLAVE DE FIRMA
    // ─────────────────────────────────────────

    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
