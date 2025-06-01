package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.ecommerce.ecommerce.Entities.enums.Color; // Importar Color si es un Enum
import com.ecommerce.ecommerce.Entities.enums.Talle; // Importar Talle si es un Enum

import jakarta.persistence.criteria.*; // Importar las clases de Criteria API
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

// Clase de utilidad para crear especificaciones de filtrado para la entidad Producto
// Ahora incluyendo filtros basados en ProductoDetalle con listas de valores
public class ProductoSpecification {

    // Método para filtrar por el campo 'activo' - ¡ESTE ES EL QUE FALTABA!
    public static Specification<Producto> byActivo(boolean activo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("activo"), activo);
    }

    // Especificación para filtrar por denominación (búsqueda por palabra clave)
    public static Specification<Producto> byDenominacionLike(String denominacion) {
        return (root, query, criteriaBuilder) ->
                denominacion == null || denominacion.isEmpty() ?
                        criteriaBuilder.conjunction() : // No aplicar filtro si la denominación es nula o vacía
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("denominacion")), "%" + denominacion.toLowerCase() + "%");
    }

    // Especificación para filtrar por una o más categorías
    public static Specification<Producto> byCategoriasIn(List<String> categorias) {
        return (root, query, criteriaBuilder) -> {
            if (categorias == null || categorias.isEmpty()) {
                return criteriaBuilder.conjunction(); // No aplicar filtro si la lista está vacía
            }

            // Usamos un Predicate disjuntivo (OR) para verificar si el producto tiene *cualquiera* de las categorías en la lista
            List<Predicate> categoriaPredicates = new ArrayList<>();
            // INNER JOIN asegura que solo obtengas productos que tienen categorías
            Join<Producto, Categoria> categoriasJoin = root.join("categorias", JoinType.INNER);

            for (String categoria : categorias) {
                if (categoria != null && !categoria.trim().isEmpty()) {
                    categoriaPredicates.add(criteriaBuilder.equal(criteriaBuilder.lower(categoriasJoin.get("denominacion")), categoria.trim().toLowerCase()));
                }
            }

            // Si la lista de categorías está vacía después de limpiar, retornar true para no filtrar por categoría
            if (categoriaPredicates.isEmpty()) {
                return criteriaBuilder.conjunction(); // Siempre verdadero
            }

            // Combinamos los predicados con OR
            return criteriaBuilder.or(categoriaPredicates.toArray(new Predicate[0]));
        };
    }

    // Especificación para filtrar por sexo
    public static Specification<Producto> bySexo(Sexo sexo) {
        return (root, query, criteriaBuilder) ->
                sexo == null ? criteriaBuilder.conjunction() : // No aplicar filtro si el sexo es nulo
                        criteriaBuilder.equal(root.get("sexo"), sexo); // Compara con el enum Sexo
    }

    // Especificación para filtrar por si tiene promoción
    public static Specification<Producto> byTienePromocion(boolean tienePromocion) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tienePromocion"), tienePromocion); // Asumiendo propiedad booleana 'tienePromocion'
    }

    // Especificación para filtrar por rango de precio de VENTA
    // Filtra por 'precioVenta' en lugar del calculado 'precioFinal'
    public static Specification<Producto> byPrecioVentaBetween(Double precioMin, Double precioMax) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction(); // Predicado inicial que siempre es verdadero

            // Usamos el campo existente 'precioVenta' de la entidad Producto
            Path<Double> precioVentaPath = root.get("precioVenta");

            if (precioMin != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(precioVentaPath, precioMin));
            }
            if (precioMax != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(precioVentaPath, precioMax));
            }

            return predicate;
        };
    }

    // --- Nuevas especificaciones basadas en ProductoDetalle, manejando LISTAS de Strings ---

    // Especificación para filtrar por AL MENOS UN detalle con color en la lista proporcionada
    public static Specification<Producto> hasDetalleWithAnyColor(List<String> colores) {
        return (root, query, criteriaBuilder) -> {
            if (colores == null || colores.isEmpty()) {
                return criteriaBuilder.conjunction(); // No aplicar filtro si la lista está vacía
            }

            // Usamos un subquery EXISTS para verificar si AL MENOS UN detalle cumple la condición.
            Subquery<ProductoDetalle> subquery = query.subquery(ProductoDetalle.class);
            Root<ProductoDetalle> subRoot = subquery.from(ProductoDetalle.class);

            subquery.select(subRoot);

            // 1. El producto del detalle en el subquery debe ser el mismo que el producto en la query principal.
            Predicate productoMatch = criteriaBuilder.equal(subRoot.get("producto"), root);

            // 2. El color del detalle debe estar en la lista de colores proporcionada (insensible a mayúsculas/minúsculas).
            Expression<String> detalleColorExpression = subRoot.get("color").as(String.class); // Tratar como String para comparación

            // Convertir la lista de colores del frontend a minúsculas para la comparación
            List<String> lowerCaseColores = colores.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            // Usamos criteriaBuilder.lower() en la expresión del detalle y luego .in() con la lista de minúsculas
            Predicate colorMatch = criteriaBuilder.lower(detalleColorExpression).in(lowerCaseColores);

            subquery.where(criteriaBuilder.and(productoMatch, colorMatch));

            // Devolvemos la predicado principal: existe AL MENOS UN detalle que cumple las condiciones del subquery
            return criteriaBuilder.exists(subquery);
        };
    }

    // Especificación para filtrar por AL MENOS UN detalle con talle en la lista proporcionada
    public static Specification<Producto> hasDetalleWithAnyTalle(List<String> talles) {
        return (root, query, criteriaBuilder) -> {
            if (talles == null || talles.isEmpty()) {
                return criteriaBuilder.conjunction(); // No aplicar filtro si la lista está vacía
            }

            // Similar al filtro por color, usamos un subquery EXISTS
            Subquery<ProductoDetalle> subquery = query.subquery(ProductoDetalle.class);
            Root<ProductoDetalle> subRoot = subquery.from(ProductoDetalle.class);

            subquery.select(subRoot);

            Predicate productoMatch = criteriaBuilder.equal(subRoot.get("producto"), root);

            // 2. El talle del detalle debe estar en la lista de talles proporcionada (insensible a mayúsculas/minúsculas).
            Expression<String> detalleTalleExpression = subRoot.get("talle").as(String.class); // Tratar como String para comparación

            // Convertir la lista de talles del frontend a minúsculas
            List<String> lowerCaseTalles = talles.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            Predicate talleMatch = criteriaBuilder.lower(detalleTalleExpression).in(lowerCaseTalles); // Usar lower().in()

            subquery.where(criteriaBuilder.and(productoMatch, talleMatch));

            return criteriaBuilder.exists(subquery);
        };
    }

    // Especificación para filtrar por stock actual mínimo en ProductoDetalle
    // Asumiendo que quieres productos que tienen AL MENOS UN detalle con stock >= stockMinimo
    public static Specification<Producto> hasDetalleWithStockActualGreaterThan(Integer stockMinimo) {
        return (root, query, criteriaBuilder) -> {
            if (stockMinimo == null) {
                return criteriaBuilder.conjunction(); // No aplicar filtro si el stockMinimo es nulo
            }

            // Similar al filtro por color, usamos un subquery EXISTS
            Subquery<ProductoDetalle> subquery = query.subquery(ProductoDetalle.class);
            Root<ProductoDetalle> subRoot = subquery.from(ProductoDetalle.class);

            subquery.select(subRoot);

            Predicate productoMatch = criteriaBuilder.equal(subRoot.get("producto"), root);
            Predicate stockMatch = criteriaBuilder.greaterThanOrEqualTo(subRoot.get("stockActual"), stockMinimo);

            subquery.where(criteriaBuilder.and(productoMatch, stockMatch));

            return criteriaBuilder.exists(subquery);
        };
    }


    // Método para combinar múltiples especificaciones con AND
    public static Specification<Producto> withFilters(
            String denominacion,
            List<String> categorias,
            Sexo sexo,
            Boolean tienePromocion,
            Double precioMin,
            Double precioMax,
            List<String> colores,
            List<String> talles,
            Integer stockMinimo
    ) {
        Specification<Producto> spec = Specification.where(null); // Comienza con una especificación vacía (siempre true)

        if (denominacion != null && !denominacion.trim().isEmpty()) {
            spec = spec.and(byDenominacionLike(denominacion.trim()));
        }
        if (categorias != null && !categorias.isEmpty()) {
            spec = spec.and(byCategoriasIn(categorias));
        }
        if (sexo != null) {
            spec = spec.and(bySexo(sexo));
        }
        if (tienePromocion != null) {
            spec = spec.and(byTienePromocion(tienePromocion));
        }
        if (precioMin != null || precioMax != null) {
            spec = spec.and(byPrecioVentaBetween(precioMin, precioMax));
        }

        if (colores != null && !colores.isEmpty()) {
            spec = spec.and(hasDetalleWithAnyColor(colores));
        }
        if (talles != null && !talles.isEmpty()) {
            spec = spec.and(hasDetalleWithAnyTalle(talles));
        }
        if (stockMinimo != null) {
            spec = spec.and(hasDetalleWithStockActualGreaterThan(stockMinimo));
        }

        return spec;
    }
}