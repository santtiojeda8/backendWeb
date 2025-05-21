package com.ecommerce.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Proporciona getters, setters, toString, equals y hashCode
@Builder // Para construir instancias de manera fluida
@AllArgsConstructor // Genera un constructor con todos los campos
@NoArgsConstructor // Genera un constructor sin argumentos (necesario para la deserialización de JSON)
public class ProfileImageDTO {
    private Long id; // El ID de la imagen, si ya está persistida
    private String url; // La URL accesible públicamente de la imagen (ej. en S3, Cloudinary)
    private String name; // El nombre original del archivo de la imagen (ej. "mi_foto.jpg")
}