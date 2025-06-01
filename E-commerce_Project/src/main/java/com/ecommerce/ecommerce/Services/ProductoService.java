package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.*;
import com.ecommerce.ecommerce.Entities.enums.Color;
import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.ecommerce.ecommerce.Entities.enums.Talle;
import com.ecommerce.ecommerce.Repositories.BaseRepository;
import com.ecommerce.ecommerce.Repositories.CategoriaRepository;
import com.ecommerce.ecommerce.Repositories.ImagenRepository;
import com.ecommerce.ecommerce.Repositories.ProductoDetalleRepository;
import com.ecommerce.ecommerce.Repositories.ProductoRepository;
import com.ecommerce.ecommerce.dto.CategoriaDTO;
import com.ecommerce.ecommerce.dto.ImagenDTO;
import com.ecommerce.ecommerce.dto.ProductoDTO;
import com.ecommerce.ecommerce.dto.ProductoDetalleDTO;
import com.ecommerce.ecommerce.dto.ProductoRequestDTO;
import com.ecommerce.ecommerce.dto.ImagenRequestDTO;
import com.ecommerce.ecommerce.dto.ProductoDetalleRequestDTO;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductoService extends BaseService<Producto, Long> {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ImagenRepository imagenRepository;
    private final ProductoDetalleRepository productoDetalleRepository;

    public ProductoService(ProductoRepository productoRepository, BaseRepository<Producto, Long> baseRepository,
                           CategoriaRepository categoriaRepository, ImagenRepository imagenRepository,
                           ProductoDetalleRepository productoDetalleRepository) {
        super(baseRepository);
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.imagenRepository = imagenRepository;
        this.productoDetalleRepository = productoDetalleRepository;
    }

    // --- Métodos de búsqueda y obtención de listas de DTOs ---

    @Transactional(readOnly = true)
    public List<Producto> findProductosConPromocion() throws Exception  {
        try{
            Specification<Producto> spec = ProductoSpecification.byTienePromocion(true).and(ProductoSpecification.byActivo(true));
            return productoRepository.findAll(spec);
        }catch (Exception e){
            System.err.println("Error en findProductosConPromocion: " + e.getMessage()); e.printStackTrace();
            throw new Exception("Error al buscar productos con promoción: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String keyword)  throws Exception {
        try{
            Specification<Producto> spec = ProductoSpecification.byDenominacionLike(keyword).and(ProductoSpecification.byActivo(true));
            return productoRepository.findAll(spec);
        }catch (Exception e){
            System.err.println("Error en buscarPorNombre: " + e.getMessage()); e.printStackTrace();
            throw new Exception("Error al buscar productos por nombre: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerTodosLosProductosDTO() throws Exception {
        try {
            List<Producto> todosLosProductos = super.listar();
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
            Specification<Producto> spec = ProductoSpecification.byTienePromocion(true)
                    .and(ProductoSpecification.byActivo(true));
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
            Producto producto = super.buscarPorId(id);
            return mapearProductoADTO(producto);
        } catch (Exception e) {
            System.err.println("Error en obtenerProductoDTOPorId: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener Producto DTO por ID: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<String> getAllAvailableCategories() throws Exception {
        try {
            List<Producto> allProducts = super.listar();
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
            return uniqueCategories.stream().sorted().collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al obtener categorías disponibles: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener categorías disponibles: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<String> getAllAvailableColors() throws Exception {
        try {
            List<Producto> todosLosProductos = super.listar();
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
            return uniqueColors.stream().sorted().collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al obtener colores disponibles: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener colores disponibles: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<String> getAllAvailableTalles() throws Exception {
        try {
            List<Producto> todosLosProductos = super.listar();
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
            return uniqueTalles.stream().sorted().collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al obtener talles disponibles: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al obtener talles disponibles: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> filtrarYOrdenarProductos(
            String denominacion, List<String> categorias, Sexo sexo, Boolean tienePromocion,
            Double precioMin, Double precioMax, List<String> colores, List<String> talles,
            Integer stockMinimo, String sortBy, String sortDir
    ) throws Exception {
        try {
            Specification<Producto> combinedSpec = ProductoSpecification.withFilters(
                    denominacion, categorias, sexo, tienePromocion, precioMin, precioMax, colores, talles, stockMinimo
            ).and(ProductoSpecification.byActivo(true));

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
            return productosFiltradosYOrdenados.stream().map(this::mapearProductoADTO).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al filtrar y ordenar productos: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al filtrar y ordenar productos: " + e.getMessage());
        }
    }

    // --- Métodos CRUD con DTOs de Solicitud (ProductoRequestDTO) ---

    @Transactional
    public ProductoDTO crearProductoDesdeRequestDTO(ProductoRequestDTO requestDTO) throws Exception {
        try {
            Producto producto = mapearRequestDTOaEntidad(requestDTO);
            Producto savedProducto = productoRepository.save(producto);
            return mapearProductoADTO(savedProducto); // Mapear Entidad guardada a DTO de RESPUESTA
        } catch (Exception e) {
            System.err.println("Error en crearProductoDesdeRequestDTO: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al crear producto desde DTO de solicitud: " + e.getMessage());
        }
    }

    @Transactional
    public ProductoDTO actualizarProductoDesdeRequestDTO(Long id, ProductoRequestDTO requestDTO) throws Exception {
        try {
            Producto productoExistente = productoRepository.findById(id)
                    .orElseThrow(() -> new Exception("Producto no encontrado con ID: " + id));

            // Actualizar propiedades simples
            productoExistente.setDenominacion(requestDTO.getDenominacion());
            productoExistente.setPrecioVenta(requestDTO.getPrecioOriginal());
            productoExistente.setTienePromocion(requestDTO.isTienePromocion());
            productoExistente.setSexo(requestDTO.getSexo());
            productoExistente.setActivo(requestDTO.isActivo());

            // === 1. Manejo de Categorías (ManyToMany) ===
            Set<Categoria> nuevasCategorias = new HashSet<>();
            if (requestDTO.getCategoriaIds() != null) {
                for (Long catId : requestDTO.getCategoriaIds()) {
                    Categoria categoriaEntity = categoriaRepository.findById(catId)
                            .orElseThrow(() -> new Exception("Categoría no encontrada con ID: " + catId));
                    nuevasCategorias.add(categoriaEntity);
                }
            }
            // JPA gestionará las adiciones/eliminaciones automáticamente al asignar el nuevo Set.
            productoExistente.setCategorias(nuevasCategorias);


            // === 2. Manejo de Imágenes (OneToMany) ===
            if (requestDTO.getImagenes() == null || requestDTO.getImagenes().isEmpty()) {
                productoExistente.getImagenes().clear(); // Si no se envían imágenes, se eliminan todas.
            } else {
                Map<Long, Imagen> imagenesExistentesMap = productoExistente.getImagenes().stream()
                        .filter(img -> img.getId() != null)
                        .collect(Collectors.toMap(Imagen::getId, img -> img));

                Set<Imagen> imagenesAProcesar = new HashSet<>();

                for (ImagenRequestDTO imgDTO : requestDTO.getImagenes()) {
                    if (imgDTO.getId() != null && imagenesExistentesMap.containsKey(imgDTO.getId())) {
                        // Es una imagen existente: actualizarla
                        Imagen imagenAActualizar = imagenesExistentesMap.get(imgDTO.getId());
                        imagenAActualizar.setDenominacion(imgDTO.getUrl());
                        imagenAActualizar.setActivo(imgDTO.isActivo());
                        imagenesAProcesar.add(imagenAActualizar);
                    } else {
                        // Es una nueva imagen: crearla
                        Imagen nuevaImagen = new Imagen();
                        nuevaImagen.setDenominacion(imgDTO.getUrl());
                        nuevaImagen.setActivo(imgDTO.isActivo());
                        nuevaImagen.setProducto(productoExistente); // Establecer la relación bidireccional
                        imagenesAProcesar.add(nuevaImagen);
                    }
                }
                // Limpiar la colección existente y añadir las procesadas.
                // Con orphanRemoval=true, esto eliminará las que ya no están en imagenesAProcesar.
                productoExistente.getImagenes().clear();
                productoExistente.getImagenes().addAll(imagenesAProcesar);
            }


            // === 3. Manejo de ProductoDetalles (OneToMany) ===
            if (requestDTO.getProductos_detalles() == null || requestDTO.getProductos_detalles().isEmpty()) {
                productoExistente.getProductos_detalles().clear(); // Si no se envían detalles, se eliminan todos.
            } else {
                Map<Long, ProductoDetalle> detallesExistentesMap = productoExistente.getProductos_detalles().stream()
                        .filter(det -> det.getId() != null)
                        .collect(Collectors.toMap(ProductoDetalle::getId, det -> det));

                Set<ProductoDetalle> detallesAProcesar = new HashSet<>();

                for (ProductoDetalleRequestDTO detDTO : requestDTO.getProductos_detalles()) {
                    if (detDTO.getId() != null && detallesExistentesMap.containsKey(detDTO.getId())) {
                        // Es un detalle existente: actualizarlo
                        ProductoDetalle detalleAActualizar = detallesExistentesMap.get(detDTO.getId());
                        detalleAActualizar.setPrecioCompra(detDTO.getPrecioCompra());
                        detalleAActualizar.setStockActual(detDTO.getStockActual());
                        detalleAActualizar.setStockMaximo(detDTO.getStockMaximo());
                        detalleAActualizar.setActivo(detDTO.isActivo());
                        if (detDTO.getColor() != null && !detDTO.getColor().isEmpty()) {
                            detalleAActualizar.setColor(Color.valueOf(detDTO.getColor().toUpperCase()));
                        }
                        if (detDTO.getTalle() != null && !detDTO.getTalle().isEmpty()) {
                            detalleAActualizar.setTalle(Talle.valueOf(detDTO.getTalle().toUpperCase()));
                        }
                        detallesAProcesar.add(detalleAActualizar);
                    } else {
                        // Es un nuevo detalle: crearlo
                        ProductoDetalle nuevoDetalle = new ProductoDetalle();
                        nuevoDetalle.setPrecioCompra(detDTO.getPrecioCompra());
                        nuevoDetalle.setStockActual(detDTO.getStockActual());
                        nuevoDetalle.setStockMaximo(detDTO.getStockMaximo());
                        nuevoDetalle.setActivo(detDTO.isActivo());
                        if (detDTO.getColor() != null && !detDTO.getColor().isEmpty()) {
                            nuevoDetalle.setColor(Color.valueOf(detDTO.getColor().toUpperCase()));
                        }
                        if (detDTO.getTalle() != null && !detDTO.getTalle().isEmpty()) {
                            nuevoDetalle.setTalle(Talle.valueOf(detDTO.getTalle().toUpperCase()));
                        }
                        nuevoDetalle.setProducto(productoExistente); // Establecer la relación bidireccional
                        detallesAProcesar.add(nuevoDetalle);
                    }
                }
                // Limpiar la colección existente y añadir las procesadas.
                productoExistente.getProductos_detalles().clear();
                productoExistente.getProductos_detalles().addAll(detallesAProcesar);
            }

            // Guardar el producto, lo que propagará los cambios a las colecciones
            Producto updatedProducto = productoRepository.save(productoExistente);
            return mapearProductoADTO(updatedProducto);
        } catch (Exception e) {
            System.err.println("Error en actualizarProductoDesdeRequestDTO (ID: " + id + "): " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al actualizar producto desde DTO de solicitud: " + e.getMessage());
        }
    }

    @Transactional
    public void eliminarProductoPorId(Long id) throws Exception {
        try {
            Optional<Producto> productoOptional = productoRepository.findById(id);
            if (productoOptional.isEmpty()) {
                throw new Exception("Producto no encontrado con ID: " + id);
            }
            Producto producto = productoOptional.get();
            producto.setActivo(false); // Marcar como inactivo (soft delete)
            productoRepository.save(producto); // Persistir el cambio
        } catch (Exception e) {
            System.err.println("Error en eliminarProductoPorId: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al eliminar producto por ID: " + e.getMessage());
        }
    }

    // ====================================================================================
    // --- Métodos de mapeo (entidad a DTO de RESPUESTA) ---
    // ====================================================================================

    private ProductoDTO mapearProductoADTO(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId(producto.getId());
        productoDTO.setDenominacion(producto.getDenominacion());
        productoDTO.setPrecioOriginal(producto.getPrecioVenta());
        productoDTO.setPrecioFinal(calcularPrecioFinal(producto));
        productoDTO.setTienePromocion(producto.isTienePromocion());
        productoDTO.setSexo(producto.getSexo());
        productoDTO.setActivo(producto.isActivo());

        productoDTO.setCategorias(producto.getCategorias() != null ?
                producto.getCategorias().stream()
                        .map(this::mapearCategoriaADTO)
                        .collect(Collectors.toList()) :
                Collections.emptyList());

        productoDTO.setImagenes(producto.getImagenes() != null ?
                producto.getImagenes().stream()
                        .map(this::mapearImagenADTO)
                        .collect(Collectors.toList()) :
                Collections.emptyList());

        productoDTO.setProductos_detalles(producto.getProductos_detalles() != null ?
                producto.getProductos_detalles().stream()
                        .map(this::mapearProductoDetalleADTO)
                        .collect(Collectors.toList()) :
                Collections.emptyList());

        return productoDTO;
    }

    private CategoriaDTO mapearCategoriaADTO(Categoria categoriaEntity) {
        if (categoriaEntity == null) {
            return null;
        }
        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(categoriaEntity.getId());
        categoriaDTO.setDenominacion(categoriaEntity.getDenominacion());
        return categoriaDTO;
    }

    private ImagenDTO mapearImagenADTO(Imagen imagenEntity) {
        if (imagenEntity == null) {
            return null;
        }
        return new ImagenDTO(imagenEntity.getId(), imagenEntity.getDenominacion());
    }

    private ProductoDetalleDTO mapearProductoDetalleADTO(ProductoDetalle detalleEntity) {
        if (detalleEntity == null) {
            return null;
        }
        ProductoDetalleDTO detalleDTO = new ProductoDetalleDTO();
        detalleDTO.setId(detalleEntity.getId());
        detalleDTO.setPrecioCompra(detalleEntity.getPrecioCompra());
        detalleDTO.setStockActual(detalleEntity.getStockActual());
        detalleDTO.setStockMaximo(detalleEntity.getStockMaximo());
        detalleDTO.setColor(detalleEntity.getColor() != null ? detalleEntity.getColor().toString() : null);
        detalleDTO.setTalle(detalleEntity.getTalle() != null ? detalleEntity.getTalle().toString() : null);
        detalleDTO.setCantidad(detalleEntity.getStockActual());
        detalleDTO.setActivo(detalleEntity.isActivo());

        if (detalleEntity.getProducto() != null && detalleEntity.getProducto().getId() != null) {
            ProductoDTO productoRefDTO = new ProductoDTO();
            productoRefDTO.setId(detalleEntity.getProducto().getId());
            detalleDTO.setProducto(productoRefDTO);
        }
        return detalleDTO;
    }

    // ====================================================================================
    // --- MÉTODO DE MAPEO Request DTO A ENTIDAD ---
    // ====================================================================================
    private Producto mapearRequestDTOaEntidad(ProductoRequestDTO requestDTO) throws Exception {
        if (requestDTO == null) {
            return null;
        }

        Producto producto = new Producto();
        producto.setDenominacion(requestDTO.getDenominacion());
        producto.setPrecioVenta(requestDTO.getPrecioOriginal());
        producto.setTienePromocion(requestDTO.isTienePromocion());
        producto.setSexo(requestDTO.getSexo());
        producto.setActivo(requestDTO.isActivo());

        // Mapear Categorias usando IDs
        if (requestDTO.getCategoriaIds() != null && !requestDTO.getCategoriaIds().isEmpty()) {
            Set<Categoria> categorias = new HashSet<>();
            for (Long categoriaId : requestDTO.getCategoriaIds()) {
                Categoria categoriaExistente = categoriaRepository.findById(categoriaId)
                        .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + categoriaId));
                categorias.add(categoriaExistente);
            }
            producto.setCategorias(categorias);
        } else {
            producto.setCategorias(new HashSet<>());
        }

        // Mapear Imagenes (creación)
        if (requestDTO.getImagenes() != null && !requestDTO.getImagenes().isEmpty()) {
            Set<Imagen> imagenes = new HashSet<>();
            for (ImagenRequestDTO imagenDTO : requestDTO.getImagenes()) {
                Imagen imagen = new Imagen();
                imagen.setDenominacion(imagenDTO.getUrl());
                imagen.setActivo(imagenDTO.isActivo());
                imagen.setProducto(producto); // Vincular la imagen al producto
                imagenes.add(imagen);
            }
            producto.setImagenes(imagenes);
        } else {
            producto.setImagenes(new HashSet<>());
        }

        // Mapear ProductoDetalles (creación)
        if (requestDTO.getProductos_detalles() != null && !requestDTO.getProductos_detalles().isEmpty()) {
            Set<ProductoDetalle> detalles = new HashSet<>();
            for (ProductoDetalleRequestDTO detalleDTO : requestDTO.getProductos_detalles()) {
                ProductoDetalle detalle = new ProductoDetalle();
                detalle.setPrecioCompra(detalleDTO.getPrecioCompra());
                detalle.setStockActual(detalleDTO.getStockActual());
                detalle.setStockMaximo(detalleDTO.getStockMaximo());

                if (detalleDTO.getColor() != null && !detalleDTO.getColor().isEmpty()) {
                    try {
                        detalle.setColor(Color.valueOf(detalleDTO.getColor().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Color inválido en DTO: " + detalleDTO.getColor());
                        throw new IllegalArgumentException("Color inválido: " + detalleDTO.getColor());
                    }
                }

                if (detalleDTO.getTalle() != null && !detalleDTO.getTalle().isEmpty()) {
                    try {
                        detalle.setTalle(Talle.valueOf(detalleDTO.getTalle().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Talle inválido en DTO: " + detalleDTO.getTalle());
                        throw new IllegalArgumentException("Talle inválido: " + detalleDTO.getTalle());
                    }
                }
                detalle.setActivo(detalleDTO.isActivo());
                detalle.setProducto(producto); // Establecer la relación bidireccional
                detalles.add(detalle);
            }
            producto.setProductos_detalles(detalles);
        } else {
            producto.setProductos_detalles(new HashSet<>());
        }

        return producto;
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