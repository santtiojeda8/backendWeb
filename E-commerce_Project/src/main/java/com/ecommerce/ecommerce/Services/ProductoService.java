package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.Categoria;
import com.ecommerce.ecommerce.Entities.Descuento;
import com.ecommerce.ecommerce.Entities.Producto;
import com.ecommerce.ecommerce.Entities.ProductoSpecification;
import com.ecommerce.ecommerce.Entities.enums.Sexo;
// Importar Color y Talle si son Enums en tus entidades
// import com.ecommerce.ecommerce.Entities.enums.Color;
// import com.ecommerce.ecommerce.Entities.enums.Talle;

import com.ecommerce.ecommerce.Repositories.BaseRepository;
import com.ecommerce.ecommerce.Repositories.ProductoRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

// Importar los DTOs necesarios (ahora incluyendo CategoriaDTO e ImagenDTO)
import com.ecommerce.ecommerce.dto.ProductoDTO;
import com.ecommerce.ecommerce.dto.ProductoDetalleDTO;
import com.ecommerce.ecommerce.dto.ImagenDTO; // <-- Necesitamos este DTO para mapear
import com.ecommerce.ecommerce.dto.CategoriaDTO; // <-- Necesitamos este DTO para mapear
import com.ecommerce.ecommerce.dto.DescuentoDTO; // Necesario si mapeas descuentos

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

    @Transactional(readOnly = true) // Método para buscar productos con promoción (devuelve Entidades, puede ser útil internamente)
    public List<Producto> findProductosConPromocion() throws Exception  {
        try{
            Specification<Producto> spec = ProductoSpecification.byTienePromocion(true);
            return productoRepository.findAll(spec);
        }catch (Exception e){
            System.err.println("Error en findProductosConPromocion: " + e.getMessage()); e.printStackTrace();
            throw new Exception("Error al buscar productos con promoción: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true) // Método para buscar por nombre (devuelve Entidades, puede ser útil internamente)
    public List<Producto> buscarPorNombre(String keyword)  throws Exception {
        try{
            Specification<Producto> spec = ProductoSpecification.byDenominacionLike(keyword);
            return productoRepository.findAll(spec);
        }catch (Exception e){
            System.err.println("Error en buscarPorNombre: " + e.getMessage()); e.printStackTrace();
            throw new Exception("Error al buscar productos por nombre: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true) // Método para obtener todos los productos DTO
    public List<ProductoDTO> obtenerTodosLosProductosDTO() throws Exception {
        try {
            List<Producto> todosLosProductos = productoRepository.findAll();
            return todosLosProductos.stream()
                    .map(this::mapearProductoADTO) // Usa el método de mapeo actualizado
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error en obtenerTodosLosProductosDTO: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener todos los productos DTO: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true) // Método para obtener productos promocionales DTO
    public List<ProductoDTO> obtenerProductosPromocionalesDTO() throws Exception {
        try {
            Specification<Producto> spec = ProductoSpecification.byTienePromocion(true);
            List<Producto> productosPromocionales = productoRepository.findAll(spec);
            return productosPromocionales.stream()
                    .map(this::mapearProductoADTO) // Usa el método de mapeo actualizado
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error en obtenerProductosPromocionalesDTO: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener productos promocionales DTO: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true) // Método para obtener Producto DTO por ID
    public ProductoDTO obtenerProductoDTOPorId(Long id) throws Exception {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new Exception("Producto no encontrado con ID: " + id));
            return mapearProductoADTO(producto); // Usa el método de mapeo actualizado
        } catch (Exception e) {
            System.err.println("Error en obtenerProductoDTOPorId: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener Producto DTO por ID: " + e.getMessage());
        }
    }

    // --- Método para obtener la lista de categorías disponibles (como Strings) ---
    // Este método sigue devolviendo Strings ya que es para la lista de filtros
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
    // Estos métodos siguen devolviendo Strings para las listas de filtros
    @Transactional(readOnly = true)
    public List<String> getAllAvailableColors() throws Exception {
        try {
            List<Producto> todosLosProductos = productoRepository.findAll();

            Set<String> uniqueColors = new HashSet<>();

            for (Producto producto : todosLosProductos) {
                if (producto.getProductos_detalles() != null) {
                    for (ProductoDetalle detalle : producto.getProductos_detalles()) {
                        if (detalle.getColor() != null) {
                            uniqueColors.add(detalle.getColor().toString()); // <-- Ajusta según tu implementación de Color (Enum/Entidad a String)
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
                            uniqueTalles.add(detalle.getTalle().toString()); // <-- Ajusta según tu implementación de Talle (Enum/Entidad a String)
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
            // Llama a ProductSpecification.withFilters con los argumentos de filtro
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

            // Configura el ordenamiento
            Sort sort = Sort.unsorted();
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                Sort.Direction direction = Sort.Direction.ASC;
                if (sortDir != null && sortDir.equalsIgnoreCase("desc")) {
                    direction = Sort.Direction.DESC;
                }
                String actualSortBy = sortBy.trim();
                // *** IMPORTANTE: Asegúrate de que actualSortBy sea un campo válido de la entidad Producto para ordenar directamente ***
                // Si necesitas ordenar por campos anidados (ej: color de detalle), tu Specification o Repository necesitará lógica adicional.
                try {
                    sort = Sort.by(direction, actualSortBy);
                } catch (IllegalArgumentException e) {
                    System.err.println("Advertencia: Campo de ordenamiento '" + actualSortBy + "' no válido. Usando ordenamiento por defecto.");
                    sort = Sort.unsorted(); // O lanza una excepción si prefieres
                }

            }

            List<Producto> productosFiltradosYOrdenados = productoRepository.findAll(combinedSpec, sort);

            // Mapea las entidades filtradas a DTOs antes de devolver
            return productosFiltradosYOrdenados.stream()
                    .map(this::mapearProductoADTO) // Llama al método de mapeo actualizado
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error al filtrar y ordenar productos: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al filtrar y ordenar productos: " + e.getMessage());
        }
    }


    // --- Métodos de mapeo y cálculo de precio ---

    // *** Método de mapeo ACTUALIZADO para construir el ProductoDTO según tu última definición de backend ***
    private ProductoDTO mapearProductoADTO(Producto producto) {
        // Verifica si el producto es null antes de intentar mapearlo
        if (producto == null) {
            return null;
        }

        // Crea una nueva instancia del ProductoDTO
        ProductoDTO productoDTO = new ProductoDTO();

        // Mapea los campos directos de la entidad al DTO
        productoDTO.setId(producto.getId());
        productoDTO.setDenominacion(producto.getDenominacion());

        // Mapea el precio original (de la entidad al DTO)
        productoDTO.setPrecioOriginal(producto.getPrecioVenta());

        // Calcula y setea el precio final en el DTO
        Double precioFinal = calcularPrecioFinal(producto); // Asume que calcularPrecioFinal existe y funciona
        productoDTO.setPrecioFinal(precioFinal);

        // Mapea si tiene promoción
        productoDTO.setTienePromocion(producto.isTienePromocion());

        // Mapea el Sexo (si es un Enum en la Entidad, y esperas Sexo o String en el DTO)
        // Ajusta según si tu DTO espera Sexo (Enum) o String
        productoDTO.setSexo(producto.getSexo()); // Si tu DTO espera el Enum Sexo directamente
        // O si tu DTO espera un String:
        // productoDTO.setSexo(producto.getSexo() != null ? producto.getSexo().toString() : null);


        // Mapea las listas anidadas de Entidades a listas de DTOs
        // Asegúrate de que estos métodos auxiliares o el mapeo inline esté correcto
        // para mapear CategoriaEntity -> CategoriaDTO, ImagenEntity -> ImagenDTO, etc.
        // usando las definiciones de tus DTOs anidados.

        // Ejemplo de mapeo de listas (ajusta si usas un mapper como MapStruct o ModelMapper)

        // Mapear Categorias (de Entidad a DTO)
        List<CategoriaDTO> categoriasDTO = producto.getCategorias().stream()
                .map(this::mapearCategoriaADTO) // Necesitas un método mapearCategoriaADTO
                .collect(Collectors.toList());
        productoDTO.setCategorias(categoriasDTO);


        // Mapear Imagenes (de Entidad a DTO)
        List<ImagenDTO> imagenesDTO = producto.getImagenes().stream()
                .map(imagenEntity -> new ImagenDTO(imagenEntity.getId(), imagenEntity.getDenominacion())) // Crea ImagenDTOs
                .collect(Collectors.toList());
        productoDTO.setImagenes(imagenesDTO);


        // Mapear Detalles de Producto (de Entidad a DTO)
        List<ProductoDetalleDTO> productosDetallesDTO = producto.getProductos_detalles().stream()
                .map(detalleEntity -> new ProductoDetalleDTO(
                        detalleEntity.getId(),
                        detalleEntity.getPrecioCompra(),
                        detalleEntity.getStockActual(),
                        detalleEntity.getCantidad(),
                        detalleEntity.getStockMaximo(),
                        // Ajusta según cómo mapeas Color y Talle a String en ProductoDetalleDTO
                        detalleEntity.getColor() != null ? detalleEntity.getColor().toString() : null,
                        detalleEntity.getTalle() != null ? detalleEntity.getTalle().toString() : null
                ))
                .collect(Collectors.toList());
        productoDTO.setProductos_detalles(productosDetallesDTO); // Usa el nombre del campo del DTO


        // ... Mapea cualquier otro campo que esté en tu DTO y Entidad ...


        // *** AÑADE LAS LÍNEAS DE LOG AQUÍ, JUSTO ANTES DEL RETURN ***
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
        // ***********************************************************

        // Devuelve el DTO que acabas de crear
        return productoDTO;
    }

    // *** Método auxiliar para mapear entidades Categoria a CategoriaDTOs (puede ser recursivo si hay subcategorías) ***
    private CategoriaDTO mapearCategoriaADTO(Categoria categoriaEntity) {
        if (categoriaEntity == null) {
            return null;
        }

        CategoriaDTO categoriaDTO = new CategoriaDTO(categoriaEntity.getId(), categoriaEntity.getDenominacion());

        // Mapear subcategorías recursivamente si la entidad Categoria tiene una lista de subentidades Categoria
        if (categoriaEntity.getSubcategorias() != null) {
            categoriaEntity.getSubcategorias().forEach(subcategoriaEntity -> {
                categoriaDTO.addSubcategoria(mapearCategoriaADTO(subcategoriaEntity)); // Llamada recursiva
            });
        }

        return categoriaDTO;
    }


    // Método para calcular el precio final aplicando descuentos activos
    private Double calcularPrecioFinal(Producto producto) {
        Double precioActual = producto.getPrecioVenta();

        if (!producto.isTienePromocion() || producto.getDescuentos() == null || producto.getDescuentos().isEmpty()) {
            return precioActual;
        }

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        Double precioConDescuentoMasAlto = precioActual; // Inicializa con el precio base

        for (Descuento descuento : producto.getDescuentos()) {
            // Verifica si el descuento está activo hoy y ahora
            boolean fechaValida = (descuento.getFechaDesde() == null || !descuento.getFechaDesde().isAfter(hoy)) &&
                    (descuento.getFechaHasta() == null || !descuento.getFechaHasta().isBefore(hoy));
            boolean horaValida = (descuento.getHoraDesde() == null || !descuento.getHoraDesde().isAfter(ahora)) &&
                    (descuento.getHoraHasta() == null || !descuento.getHoraHasta().isBefore(ahora));


            if (fechaValida && horaValida) {
                // Calcula el precio aplicando este descuento
                double precioAplicandoEsteDescuento = precioActual * (1 - descuento.getPrecioPromocional()); // Asume precioPromocional es un factor (ej: 0.10 para 10%)

                // Mantiene el precio más bajo encontrado hasta ahora
                if (precioAplicandoEsteDescuento < precioConDescuentoMasAlto) {
                    precioConDescuentoMasAlto = precioAplicandoEsteDescuento;
                }
            }
        }

        // Retorna el precio más bajo si se encontró un descuento, de lo contrario el precio base
        return precioConDescuentoMasAlto;
    }
}
