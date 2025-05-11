package com.ecommerce.ecommerce.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service // Indica que es un componente de servicio gestionado por Spring
public class JwtService {

    // Inyecta la clave secreta desde tu archivo application.properties/yml
    // Asegúrate de tener la propiedad 'application.security.jwt.secret-key' configurada
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    // Inyecta el tiempo de expiración del token desde application.properties/yml
    // Asegúrate de tener la propiedad 'application.security.jwt.expiration' configurada (en milisegundos)
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;


    // >>> Métodos para extraer información del token <<<

    // Extrae el 'subject' (nombre de usuario) del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrae un claim específico del token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrae todos los claims (cuerpo) del token
    private Claims extractAllClaims(String token) {
        // Usa el parser de Jwts, configura la clave de firma y parsea el token
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) // Configura la clave para verificar la firma
                .build()
                .parseClaimsJws(token) // Parsea el token firmado (JWS)
                .getBody(); // Obtiene el cuerpo (claims)
    }

    // >>> Métodos para generar el token <<<

    // Genera un token para un usuario (UserDetails) sin claims extra
    public String getToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Genera el token con claims extra y detalles del usuario
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims) // Añade claims adicionales si existen
                .setSubject(userDetails.getUsername()) // Establece el nombre de usuario (del UserDetails) como subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de creación del token
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Fecha de expiración
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Firma el token con la clave secreta y el algoritmo HS256
                .compact(); // Construye el token final
    }

    // Obtiene la clave de firma a partir de la clave secreta Base64
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Decodifica la clave secreta de Base64 a bytes
        return Keys.hmacShaKeyFor(keyBytes); // Crea una clave HMAC Sha a partir de los bytes
    }

    // >>> Métodos para validar el token <<<

    // Valida si un token es válido para un usuario (UserDetails)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // Extrae el username del token
        // Verifica que el username del token coincida con el del UserDetails y que el token no haya expirado
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Verifica si el token ha expirado
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // Compara la fecha de expiración del token con la fecha actual
    }

    // Extrae la fecha de expiración del token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}