package com.ecommerce.ecommerce.Specifications;

import com.ecommerce.ecommerce.Entities.Categoria;
import com.ecommerce.ecommerce.Entities.Color;
import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Entities.ProductoDetalle; // Importar ProductoDetalle
import com.ecommerce.ecommerce.Entities.Talle; // Importar Talle
import com.ecommerce.ecommerce.Entities.enums.Sexo;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Clase de utilidad para crear especificaciones de filtrado para la entidad Producto
public class ProductoSpecification {

    // Método para filtrar por el campo 'activo'
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

    // Especificación para filtrar por TODAS las categorías proporcionadas (lógica AND)
    public static Specification<Producto> byCategoriasAll(List<String> categorias) {
        return (root, query, criteriaBuilder) -> {
            if (categorias == null || categorias.isEmpty()) {
                return criteriaBuilder.conjunction(); // No aplicar filtro si la lista está vacía
            }

            // Crear una subconsulta para encontrar los IDs de productos que tienen TODAS las categorías
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Producto> subRoot = subquery.from(Producto.class);
            Join<Producto, Categoria> subCategoryJoin = subRoot.join("categorias"); // 'categorias' es la colección en tu entidad Producto

            // Convertir la lista de categorías a minúsculas para una comparación insensible a mayúsculas/minúsculas
            List<String> lowerCaseCategorias = categorias.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            subquery.select(subRoot.get("id")) // Selecciona el ID del producto
                    .where(criteriaBuilder.lower(subCategoryJoin.get("denominacion")).in(lowerCaseCategorias)) // Donde la categoría está en la lista
                    .groupBy(subRoot.get("id")) // Agrupa por ID de producto
                    // Y el conteo de categorías únicas para ese producto debe ser igual al número de categorías en el filtro
                    .having(criteriaBuilder.equal(criteriaBuilder.count(subRoot.get("id")), (long) categorias.size()));

            // El producto en la consulta principal debe estar en el resultado de la subconsulta
            return criteriaBuilder.in(root.get("id")).value(subquery);
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
                criteriaBuilder.equal(root.get("tienePromocion"), tienePromocion);
    }

    // Especificación para filtrar por rango de precio de VENTA
    public static Specification<Producto> byPrecioVentaBetween(BigDecimal precioMin, BigDecimal precioMax) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            Path<BigDecimal> precioVentaPath = root.get("precioVenta");

            if (precioMin != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(precioVentaPath, precioMin));
            }
            if (precioMax != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(precioVentaPath, precioMax));
            }

            return predicate;
        };
    }

    // Especificación para filtrar por PRODUCTOS que tienen DETALLES con TODOS los colores en la lista (lógica AND)
    public static Specification<Producto> hasDetalleWithAllColors(List<String> colores) {
        return (root, query, criteriaBuilder) -> {
            if (colores == null || colores.isEmpty()) {
                return criteriaBuilder.conjunction(); // No aplicar filtro si la lista está vacía
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ProductoDetalle> subRoot = subquery.from(ProductoDetalle.class);
            Join<ProductoDetalle, Color> colorJoin = subRoot.join("color");

            List<String> lowerCaseColores = colores.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            subquery.select(subRoot.get("producto").get("id")) // Selecciona el ID del producto asociado al detalle
                    .where(criteriaBuilder.and(
                            criteriaBuilder.lower(colorJoin.get("nombreColor")).in(lowerCaseColores),
                            criteriaBuilder.isTrue(subRoot.get("activo")) // Solo detalles activos
                    ))
                    .groupBy(subRoot.get("producto").get("id")) // Agrupa por ID de producto
                    // El producto debe tener un número de colores distintos (a través de sus detalles activos)
                    // que sea igual al número de colores buscados
                    .having(criteriaBuilder.equal(
                            criteriaBuilder.countDistinct(colorJoin.get("nombreColor")),
                            (long) colores.size()
                    ));

            // El producto en la consulta principal debe estar en el resultado de la subconsulta
            return criteriaBuilder.in(root.get("id")).value(subquery);
        };
    }

    // Especificación para filtrar por PRODUCTOS que tienen DETALLES con TODOS los talles en la lista (lógica AND)
    public static Specification<Producto> hasDetalleWithAllTalles(List<String> talles) {
        return (root, query, criteriaBuilder) -> {
            if (talles == null || talles.isEmpty()) {
                return criteriaBuilder.conjunction(); // No aplicar filtro si la lista está vacía
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ProductoDetalle> subRoot = subquery.from(ProductoDetalle.class);
            Join<ProductoDetalle, Talle> talleJoin = subRoot.join("talle");

            List<String> lowerCaseTalles = talles.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            subquery.select(subRoot.get("producto").get("id")) // Selecciona el ID del producto asociado al detalle
                    .where(criteriaBuilder.and(
                            criteriaBuilder.lower(talleJoin.get("nombreTalle")).in(lowerCaseTalles),
                            criteriaBuilder.isTrue(subRoot.get("activo")) // Solo detalles activos
                    ))
                    .groupBy(subRoot.get("producto").get("id")) // Agrupa por ID de producto
                    // El producto debe tener un número de talles distintos (a través de sus detalles activos)
                    // que sea igual al número de talles buscados
                    .having(criteriaBuilder.equal(
                            criteriaBuilder.countDistinct(talleJoin.get("nombreTalle")),
                            (long) talles.size()
                    ));

            // El producto en la consulta principal debe estar en el resultado de la subconsulta
            return criteriaBuilder.in(root.get("id")).value(subquery);
        };
    }

    // Especificación para filtrar por stock actual mínimo en ProductoDetalle
    public static Specification<Producto> hasDetalleWithStockActualGreaterThan(Integer stockMinimo) {
        return (root, query, criteriaBuilder) -> {
            if (stockMinimo == null || stockMinimo <= 0) { // Considerar stockMinimo <= 0 como "no filtrar"
                return criteriaBuilder.conjunction();
            }

            // Usamos un subquery EXISTS para verificar si AL MENOS UN detalle cumple la condición.
            Subquery<ProductoDetalle> subquery = query.subquery(ProductoDetalle.class);
            Root<ProductoDetalle> subRoot = subquery.from(ProductoDetalle.class);

            subquery.select(subRoot);

            Predicate productoMatch = criteriaBuilder.equal(subRoot.get("producto"), root);
            Predicate stockMatch = criteriaBuilder.greaterThanOrEqualTo(subRoot.get("stockActual"), stockMinimo);
            Predicate activoMatch = criteriaBuilder.isTrue(subRoot.get("activo")); // Solo detalles activos

            subquery.where(criteriaBuilder.and(productoMatch, stockMatch, activoMatch));

            return criteriaBuilder.exists(subquery);
        };
    }

    // Método para combinar múltiples especificaciones con AND
    public static Specification<Producto> withFilters(
            String denominacion,
            List<String> categorias,
            Sexo sexo,
            Boolean tienePromocion,
            BigDecimal precioMin,
            BigDecimal precioMax,
            List<String> colores,
            List<String> talles,
            Integer stockMinimo
    ) {
        Specification<Producto> spec = Specification.where(null); // Comienza con una especificación vacía (siempre true)

        if (denominacion != null && !denominacion.trim().isEmpty()) {
            spec = spec.and(byDenominacionLike(denominacion.trim()));
        }
        if (categorias != null && !categorias.isEmpty()) {
            spec = spec.and(byCategoriasAll(categorias)); // <--- USA LA NUEVA LÓGICA "ALL"
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
            spec = spec.and(hasDetalleWithAllColors(colores)); // <--- USA LA NUEVA LÓGICA "ALL"
        }
        if (talles != null && !talles.isEmpty()) {
            spec = spec.and(hasDetalleWithAllTalles(talles)); // <--- USA LA NUEVA LÓGICA "ALL"
        }
        if (stockMinimo != null) {
            spec = spec.and(hasDetalleWithStockActualGreaterThan(stockMinimo));
        }

        return spec;
    }
}