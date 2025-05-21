package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Categoria;
import com.ecommerce.ecommerce.Entities.Descuento;
import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Entities.ProductoSpecification;
import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.ecommerce.ecommerce.Repositories.BaseRepository;
import com.ecommerce.ecommerce.Repositories.ProductoRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

// Importar los DTOs necesarios
import com.ecommerce.ecommerce.dto.ProductoDTO;
import com.ecommerce.ecommerce.dto.ProductoDetalleDTO;
import com.ecommerce.ecommerce.dto.ImagenDTO;
import com.ecommerce.ecommerce.dto.CategoriaDTO;

// Importar Entidades necesarias para el mapeo
import com.ecommerce.ecommerce.Entities.ProductoDetalle;
import com.ecommerce.ecommerce.Entities.Imagen;


@Service
public class ProductoService extends BaseService<Producto, Long> {

    private final ProductoRepository productoRepository;

    // Constructor
    public ProductoService(ProductoRepository productoRepository, BaseRepository<Producto, Long> baseRepository) {
        super(baseRepository);
        this.productoRepository = productoRepository;
    }

    // --- Métodos de búsqueda y obtención de listas de DTOs ---

    @Transactional(readOnly = true)
    public List<Producto> findProductosConPromocion() throws Exception  {
        try{
            Specification<Producto> spec = ProductoSpecification.byTienePromocion(true);
            return productoRepository.findAll(spec);
        }catch (Exception e){
            System.err.println("Error en findProductosConPromocion: " + e.getMessage()); e.printStackTrace();
            throw new Exception("Error al buscar productos con promoción: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String keyword)  throws Exception {
        try{
            Specification<Producto> spec = ProductoSpecification.byDenominacionLike(keyword);
            return productoRepository.findAll(spec);
        }catch (Exception e){
            System.err.println("Error en buscarPorNombre: " + e.getMessage()); e.printStackTrace();
            throw new Exception("Error al buscar productos por nombre: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerTodosLosProductosDTO() throws Exception {
        try {
            List<Producto> todosLosProductos = productoRepository.findAll();
            return todosLosProductos.stream()
                    .map(this::mapearProductoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error en obtenerTodosLosProductosDTO: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener todos los productos DTO: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerProductosPromocionalesDTO() throws Exception {
        try {
            Specification<Producto> spec = ProductoSpecification.byTienePromocion(true);
            List<Producto> productosPromocionales = productoRepository.findAll(spec);
            return productosPromocionales.stream()
                    .map(this::mapearProductoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error en obtenerProductosPromocionalesDTO: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener productos promocionales DTO: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ProductoDTO obtenerProductoDTOPorId(Long id) throws Exception {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new Exception("Producto no encontrado con ID: " + id));
            return mapearProductoADTO(producto);
        } catch (Exception e) {
            System.err.println("Error en obtenerProductoDTOPorId: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener Producto DTO por ID: " + e.getMessage());
        }
    }

    // --- Método para obtener la lista de categorías disponibles (como Strings) ---
    @Transactional(readOnly = true)
    public List<String> getAllAvailableCategories() throws Exception {
        try {
            List<Producto> allProducts = productoRepository.findAll();

            Set<String> uniqueCategories = new HashSet<>();

            for (Producto producto : allProducts) {
                if (producto.getCategorias() != null) {
                    for (Categoria categoria : producto.getCategorias()) {
                        if (categoria.getDenominacion() != null) {
                            uniqueCategories.add(categoria.getDenominacion());
                        }
                    }
                }
            }

            return uniqueCategories.stream()
                    .sorted()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error al obtener categorías disponibles: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener categorías disponibles: " + e.getMessage());
        }
    }

    // --- Método para obtener listas de colores y talles disponibles (como Strings) ---
    @Transactional(readOnly = true)
    public List<String> getAllAvailableColors() throws Exception {
        try {
            List<Producto> todosLosProductos = productoRepository.findAll();

            Set<String> uniqueColors = new HashSet<>();

            for (Producto producto : todosLosProductos) {
                if (producto.getProductos_detalles() != null) {
                    for (ProductoDetalle detalle : producto.getProductos_detalles()) {
                        if (detalle.getColor() != null) {
                            uniqueColors.add(detalle.getColor().toString());
                        }
                    }
                }
            }

            return uniqueColors.stream()
                    .sorted()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error al obtener colores disponibles: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener colores disponibles: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<String> getAllAvailableTalles() throws Exception {
        try {
            List<Producto> todosLosProductos = productoRepository.findAll();

            Set<String> uniqueTalles = new HashSet<>();

            for (Producto producto : todosLosProductos) {
                if (producto.getProductos_detalles() != null) {
                    for (ProductoDetalle detalle : producto.getProductos_detalles()) {
                        if (detalle.getTalle() != null) {
                            uniqueTalles.add(detalle.getTalle().toString());
                        }
                    }
                }
            }

            return uniqueTalles.stream()
                    .sorted()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error al obtener talles disponibles: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener talles disponibles: " + e.getMessage());
        }
    }

    // --- Método para filtrar y ordenar productos (devuelve DTOs) ---
    @Transactional(readOnly = true)
    public List<ProductoDTO> filtrarYOrdenarProductos(
            String denominacion,
            List<String> categorias,
            Sexo sexo,
            Boolean tienePromocion,
            Double precioMin,
            Double precioMax,
            List<String> colores,
            List<String> talles,
            Integer stockMinimo,
            String sortBy,
            String sortDir
    ) throws Exception {
        try {
            Specification<Producto> combinedSpec = ProductoSpecification.withFilters(
                    denominacion,
                    categorias,
                    sexo,
                    tienePromocion,
                    precioMin,
                    precioMax,
                    colores,
                    talles,
                    stockMinimo
            );

            Sort sort = Sort.unsorted();
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                Sort.Direction direction = Sort.Direction.ASC;
                if (sortDir != null && sortDir.equalsIgnoreCase("desc")) {
                    direction = Sort.Direction.DESC;
                }
                String actualSortBy = sortBy.trim();
                try {
                    sort = Sort.by(direction, actualSortBy);
                } catch (IllegalArgumentException e) {
                    System.err.println("Advertencia: Campo de ordenamiento '" + actualSortBy + "' no válido. Usando ordenamiento por defecto.");
                    sort = Sort.unsorted();
                }

            }

            List<Producto> productosFiltradosYOrdenados = productoRepository.findAll(combinedSpec, sort);

            return productosFiltradosYOrdenados.stream()
                    .map(this::mapearProductoADTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error al filtrar y ordenar productos: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al filtrar y ordenar productos: " + e.getMessage());
        }
    }


    // --- Métodos de mapeo y cálculo de precio (los que ya tenías) ---

    private ProductoDTO mapearProductoADTO(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoDTO productoDTO = new ProductoDTO();

        productoDTO.setId(producto.getId());
        productoDTO.setDenominacion(producto.getDenominacion());

        productoDTO.setPrecioOriginal(producto.getPrecioVenta());

        Double precioFinal = calcularPrecioFinal(producto);
        productoDTO.setPrecioFinal(precioFinal);

        productoDTO.setTienePromocion(producto.isTienePromocion());

        productoDTO.setSexo(producto.getSexo());

        // Mapear Categorias (de Entidad a DTO)
        List<CategoriaDTO> categoriasDTO = new ArrayList<>();
        if (producto.getCategorias() != null) {
            for (Categoria categoriaEntity : producto.getCategorias()) {
                categoriasDTO.add(mapearCategoriaADTO(categoriaEntity));
            }
        }
        productoDTO.setCategorias(categoriasDTO);

        // Mapear Imagenes (de Entidad a DTO)
        List<ImagenDTO> imagenesDTO = new ArrayList<>();
        if (producto.getImagenes() != null) {
            for (Imagen imagenEntity : producto.getImagenes()) {
                imagenesDTO.add(new ImagenDTO(imagenEntity.getId(), imagenEntity.getDenominacion()));
            }
        }
        productoDTO.setImagenes(imagenesDTO);


        // Mapear Detalles de Producto (de Entidad a DTO)
        List<ProductoDetalleDTO> productosDetallesDTO = new ArrayList<>();
        if (producto.getProductos_detalles() != null) {
            for (ProductoDetalle detalleEntity : producto.getProductos_detalles()) {
                productosDetallesDTO.add(new ProductoDetalleDTO(
                        detalleEntity.getId(),
                        detalleEntity.getPrecioCompra(),
                        detalleEntity.getStockActual(),
                        detalleEntity.getCantidad(),
                        detalleEntity.getStockMaximo(),
                        detalleEntity.getColor() != null ? detalleEntity.getColor().toString() : null,
                        detalleEntity.getTalle() != null ? detalleEntity.getTalle().toString() : null
                ));
            }
        }
        productoDTO.setProductos_detalles(productosDetallesDTO);


        System.out.println("--- DEBUG DTO ANTES DE SERIALIZAR ---");
        System.out.println("Producto ID: " + productoDTO.getId());
        System.out.println("Denominacion: " + productoDTO.getDenominacion());
        System.out.println("Precio Original (en DTO): " + productoDTO.getPrecioOriginal());
        System.out.println("Precio Final (en DTO): " + productoDTO.getPrecioFinal());
        System.out.println("Tiene Promocion (en DTO): " + productoDTO.isTienePromocion());
        System.out.println("Categorias Count (en DTO): " + (productoDTO.getCategorias() != null ? productoDTO.getCategorias().size() : 0));
        System.out.println("Imagenes Count (en DTO): " + (productoDTO.getImagenes() != null ? productoDTO.getImagenes().size() : 0));
        System.out.println("Productos_Detalles Count (en DTO): " + (productoDTO.getProductos_detalles() != null ? productoDTO.getProductos_detalles().size() : 0));
        System.out.println("-------------------------------------");

        return productoDTO;
    }

    private CategoriaDTO mapearCategoriaADTO(Categoria categoriaEntity) {
        if (categoriaEntity == null) {
            return null;
        }

        CategoriaDTO categoriaDTO = new CategoriaDTO(categoriaEntity.getId(), categoriaEntity.getDenominacion());

        if (categoriaEntity.getSubcategorias() != null) {
            for (Categoria subcategoriaEntity : categoriaEntity.getSubcategorias()) {
                categoriaDTO.addSubcategoria(mapearCategoriaADTO(subcategoriaEntity));
            }
        }

        return categoriaDTO;
    }


    private Double calcularPrecioFinal(Producto producto) {
        Double precioActual = producto.getPrecioVenta();

        if (!producto.isTienePromocion() || producto.getDescuentos() == null || producto.getDescuentos().isEmpty()) {
            return precioActual;
        }

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        Double precioConDescuentoMasAlto = precioActual;

        for (Descuento descuento : producto.getDescuentos()) {
            boolean fechaValida = (descuento.getFechaDesde() == null || !descuento.getFechaDesde().isAfter(hoy)) &&
                    (descuento.getFechaHasta() == null || !descuento.getFechaHasta().isBefore(hoy));
            boolean horaValida = (descuento.getHoraDesde() == null || !descuento.getHoraDesde().isAfter(ahora)) &&
                    (descuento.getHoraHasta() == null || !descuento.getHoraHasta().isBefore(ahora));


            if (fechaValida && horaValida) {
                double precioAplicandoEsteDescuento = precioActual * (1 - descuento.getPrecioPromocional());
                if (precioAplicandoEsteDescuento < precioConDescuentoMasAlto) {
                    precioConDescuentoMasAlto = precioAplicandoEsteDescuento;
                }
            }
        }
        return precioConDescuentoMasAlto;
    }
}