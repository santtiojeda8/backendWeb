package com.ecommerce.ecommerce.Services;

// Importaciones necesarias (asegúrate de que tengas todas estas)
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails; // Importa UserDetails
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Asegúrate de que estas propiedades estén definidas en tu application.properties o .yml
    // Por ejemplo:
    // application.jwt.secret-key=unaClaveSecretaMuyLargaParaJWTDeAlMenos256BitsQueNadieDebeAdivinar
    // application.jwt.expiration=86400000 (24 horas en milisegundos)
    @Value("${application.security.jwt.secret-key}") // <-- ¡Añadir .security aquí!
    private String secretKey;

    @Value("${application.security.jwt.expiration}") // <-- ¡Y aquí también!
    private long jwtExpiration;

    /**
     * Genera un token JWT para un usuario, sin claims adicionales específicos.
     * Este es el método que tu AuthService debe llamar.
     *
     * @param userDetails Los detalles del usuario para quien se generará el token.
     * @return El token JWT generado como String.
     */
    // >>> CAMBIO CLAVE AQUÍ: DEBE SER PUBLIC <<<
    public String generateToken(UserDetails userDetails) {
        // Llama a la versión privada del método, pasando un mapa de claims vacío por defecto.
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT con claims adicionales y una fecha de expiración.
     * Este método suele ser privado y llamado internamente por el método público generateToken.
     *
     * @param extraClaims Un mapa de claims adicionales a incluir en el token.
     * @param userDetails Los detalles del usuario para quien se generará el token.
     * @return El token JWT generado como String.
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims) // Añade los claims adicionales
                .setSubject(userDetails.getUsername()) // Establece el 'subject' (generalmente el username/email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Fecha de expiración
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Firma el token con la clave secreta
                .compact(); // Construye el token final
    }

    /**
     * Valida un token JWT.
     *
     * @param token El token JWT a validar.
     * @param userDetails Los detalles del usuario con los que se comparará el token.
     * @return true si el token es válido para el usuario y no ha expirado, false de lo contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Extrae la fecha de expiración de un token JWT.
     *
     * @param token El token JWT.
     * @return La fecha de expiración.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Verifica si un token JWT ha expirado.
     *
     * @param token El token JWT a verificar.
     * @return true si el token ha expirado, false de lo contrario.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae el nombre de usuario (subject) de un token JWT.
     *
     * @param token El token JWT.
     * @return El nombre de usuario.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Método genérico para extraer un claim específico de un token JWT.
     *
     * @param token El token JWT.
     * @param claimsResolver Una función que resuelve el claim deseado del objeto Claims.
     * @param <T> El tipo del claim a extraer.
     * @return El valor del claim extraído.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims (cuerpo) de un token JWT.
     *
     * @param token El token JWT.
     * @return El objeto Claims que contiene todos los claims del token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtiene la clave de firma (signing key) decodificada.
     *
     * @return La clave de firma.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}