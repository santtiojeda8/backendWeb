package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequestDTO {
    private Long id; // Null para creación, presente para actualización
    private String denominacion;
    private Double precioOriginal; // Precio de venta del producto (para el backend)
    private boolean tienePromocion; // <--- CORREGIDO: ahora es un boolean
    private Sexo sexo;
    private boolean activo;

    // --- Relaciones: Enviamos solo los IDs o datos mínimos para las colecciones ---

    // Para Categorías (ManyToMany): Enviamos una lista de IDs de categoría.
    private List<Long> categoriaIds;

    // Para Imágenes (OneToMany): Enviamos una lista de DTOs simplificados.
    // Esto asume que las imágenes se enviarán con URL o denominación,
    // y que el backend las manejará (creará si no tienen ID, actualizará si tienen).
    private List<ImagenRequestDTO> imagenes;

    // Para Detalles de Producto (OneToMany): Enviamos una lista de DTOs de solicitud para detalles.
    private List<ProductoDetalleRequestDTO> productos_detalles;

    // Puedes añadir campos para descuentos si los gestionas al crear/actualizar productos
    // private List<Long> descuentoIds; // O un DTO para descuentos si la lógica es más compleja
}