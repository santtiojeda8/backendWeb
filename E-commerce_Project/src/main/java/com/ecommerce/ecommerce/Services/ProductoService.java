package com.ecommerce.ecommerce.Services;

import com.ecommerce.ecommerce.Entities.*;
import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.ecommerce.ecommerce.Repositories.*;
import com.ecommerce.ecommerce.dto.CategoriaDTO;
import com.ecommerce.ecommerce.dto.ColorDTO;
import com.ecommerce.ecommerce.dto.DescuentoDTO;
import com.ecommerce.ecommerce.dto.ImagenDTO;
import com.ecommerce.ecommerce.dto.ProductoDTO;
import com.ecommerce.ecommerce.dto.ProductoDetalleDTO;
import com.ecommerce.ecommerce.dto.ProductoRequestDTO;
import com.ecommerce.ecommerce.dto.ProductoDetalleRequestDTO;
import com.ecommerce.ecommerce.dto.TalleDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductoService extends BaseService<Producto, Long> {

    private ProductoRepository productoRepository;
    private CategoriaRepository categoriaRepository;
    private ImagenRepository imagenRepository; // This can often be removed if using full cascading
    private ProductoDetalleRepository productoDetalleRepository;
    private ColorRepository colorRepository;
    private TalleRepository talleRepository;
    private DescuentosRepository descuentoRepository;
    private CloudinaryService cloudinaryService;

    @Autowired
    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           ImagenRepository imagenRepository,
                           ProductoDetalleRepository productoDetalleRepository,
                           ColorRepository colorRepository,
                           TalleRepository talleRepository,
                           DescuentosRepository descuentoRepository,
                           CloudinaryService cloudinaryService) {
        super(productoRepository);
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.imagenRepository = imagenRepository;
        this.productoDetalleRepository = productoDetalleRepository;
        this.colorRepository = colorRepository;
        this.talleRepository = talleRepository;
        this.descuentoRepository = descuentoRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listar() throws Exception {
        try {
            List<Producto> productos = productoRepository.findAllByActivoTrueWithDescuento();
            productos.forEach(Producto::calcularPrecioFinal);
            return productos;
        } catch (Exception e) {
            throw new Exception("Error al listar productos con descuento: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) throws Exception {
        try {
            Optional<Producto> productoOptional = productoRepository.findByIdAndActivoTrueWithDescuento(id);
            if (productoOptional.isEmpty()) {
                throw new EntityNotFoundException("Producto con ID " + id + " no encontrado o inactivo.");
            }
            Producto producto = productoOptional.get();
            producto.calcularPrecioFinal();
            return producto;
        } catch (Exception e) {
            throw new Exception("Error al buscar producto por ID con descuento: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerTodosLosProductosDTO() throws Exception {
        List<Producto> productos = listar();
        return productos.stream()
                .map(this::mapearProductoADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoDTO obtenerProductoDTOPorId(Long id) throws Exception {
        Producto producto = buscarPorId(id);
        return mapearProductoADTO(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarProductosPorDenominacion(String denominacion) throws Exception {
        try {
            List<Producto> productos = productoRepository.findByDenominacionContainingAndActivoTrueWithDescuento(denominacion);
            productos.forEach(Producto::calcularPrecioFinal);
            return productos.stream()
                    .map(this::mapearProductoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al buscar productos por denominación con descuento: " + e.getMessage());
        }
    }

    @Transactional
    public ProductoDTO crearProducto(ProductoRequestDTO productoRequestDTO, List<MultipartFile> newImageFiles) throws Exception {
        Producto producto = new Producto();
        actualizarProductoDesdeDTO(producto, productoRequestDTO);

        if (productoRequestDTO.isTienePromocion() && productoRequestDTO.getDescuento() != null && productoRequestDTO.getDescuento().getId() != null) {
            Descuento descuento = descuentoRepository.findById(productoRequestDTO.getDescuento().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Descuento con ID " + productoRequestDTO.getDescuento().getId() + " no encontrado."));
            producto.setDescuento(descuento);
            producto.setTienePromocion(true);
        } else {
            producto.setDescuento(null);
            producto.setTienePromocion(false);
        }

        producto = productoRepository.save(producto);

        // Lógica para cargar nuevas imágenes (creación)
        // **Este bloque se mantiene similar, pero sin guardar cada imagen con imagenRepository.save()**
        // **La cascada se encargará al guardar el producto al final.**
        if (newImageFiles != null && !newImageFiles.isEmpty()) {
            for (MultipartFile file : newImageFiles) {
                Map<?, ?> uploadResult = cloudinaryService.uploadImage(file);
                String imageUrl = uploadResult.get("url").toString();

                Imagen nuevaImagen = new Imagen();
                nuevaImagen.setUrl(imageUrl);
                nuevaImagen.setProducto(producto); // Asigna el producto padre
                nuevaImagen.setActivo(true); // Las nuevas imágenes están activas por defecto
                producto.getImagenes().add(nuevaImagen); // Añade a la colección del producto
                // No necesitas imagenRepository.save(nuevaImagen) aquí si tienes CascadeType.ALL
            }
        }

        // Lógica para añadir detalles de producto (para la creación)
        if (productoRequestDTO.getProductos_detalles() != null) {
            for (ProductoDetalleRequestDTO detalleRequest : productoRequestDTO.getProductos_detalles()) {
                ProductoDetalle detalle = new ProductoDetalle();
                detalle.setProducto(producto);
                detalle.setPrecioCompra(detalleRequest.getPrecioCompra());
                detalle.setStockActual(detalleRequest.getStockActual());
                detalle.setStockMaximo(detalleRequest.getStockMaximo());
                detalle.setActivo(detalleRequest.isActivo());

                detalle.setColor(colorRepository.findById(detalleRequest.getColorId())
                        .orElseThrow(() -> new EntityNotFoundException("Color con ID " + detalleRequest.getColorId() + " no encontrado.")));
                detalle.setTalle(talleRepository.findById(detalleRequest.getTalleId())
                        .orElseThrow(() -> new EntityNotFoundException("Talle con ID " + detalleRequest.getTalleId() + " no encontrado.")));

                productoDetalleRepository.save(detalle); // Se mantiene el save explícito para detalles si no usas cascade en detalles
                producto.getProductos_detalles().add(detalle);
            }
        }

        producto.calcularPrecioFinal();
        // Guardar el producto, lo cual cascadeará las operaciones a las imágenes recién añadidas
        return mapearProductoADTO(productoRepository.save(producto));
    }


    @Transactional
    public ProductoDTO actualizarProducto(Long id, ProductoRequestDTO productoRequestDTO, List<MultipartFile> newImageFiles) throws Exception {
        Producto productoExistente = buscarPorId(id);
        actualizarProductoDesdeDTO(productoExistente, productoRequestDTO);

        // Lógica para actualizar el descuento
        if (productoRequestDTO.isTienePromocion() && productoRequestDTO.getDescuento() != null && productoRequestDTO.getDescuento().getId() != null) {
            Descuento descuento = descuentoRepository.findById(productoRequestDTO.getDescuento().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Descuento con ID " + productoRequestDTO.getDescuento().getId() + " no encontrado."));
            productoExistente.setDescuento(descuento);
            productoExistente.setTienePromocion(true);
        } else {
            productoExistente.setDescuento(null);
            productoExistente.setTienePromocion(false);
        }

        // --- INICIO: Lógica OPTIMIZADA para la GESTIÓN de IMÁGENES ---
        Set<Imagen> imagesToPersist = new HashSet<>();

        // 1. Procesar imágenes que vienen en el DTO (existentes y potencialmente actualizadas)
        if (productoRequestDTO.getImagenes() != null) {
            for (ImagenDTO imagenDTO : productoRequestDTO.getImagenes()) {
                if (imagenDTO.getId() != null) {
                    // Es una imagen existente: búscala y actualiza sus propiedades
                    Imagen existingImage = productoExistente.getImagenes().stream()
                            .filter(img -> img.getId() != null && img.getId().equals(imagenDTO.getId()))
                            .findFirst()
                            .orElse(null);

                    if (existingImage != null) {
                        // Actualiza el campo 'url' y 'activo'.
                        existingImage.setUrl(imagenDTO.getUrl());
                        existingImage.setActivo(imagenDTO.isActivo());
                        imagesToPersist.add(existingImage); // Se añade la imagen existente y actualizada
                    }
                } else {
                    // Es una imagen nueva (ID es null), créala
                    Imagen newImage = Imagen.builder()
                            .url(imagenDTO.getUrl())
                            .activo(imagenDTO.isActivo())
                            .producto(productoExistente) // Establecer la relación bidireccional
                            .build();
                    imagesToPersist.add(newImage); // Se añade la nueva imagen
                }
            }
        }

        // 2. Cargar y añadir nuevas imágenes desde MultipartFile (imágenes recién subidas)
        if (newImageFiles != null && !newImageFiles.isEmpty()) {
            // Opcional: Para evitar subir la misma imagen de Cloudinary varias veces (aunque Cloudinary ya da URL única)
            Set<String> currentAndNewImageUrls = imagesToPersist.stream().map(Imagen::getUrl).collect(Collectors.toSet());

            for (MultipartFile file : newImageFiles) {
                Map<?, ?> uploadResult = cloudinaryService.uploadImage(file);
                String imageUrl = uploadResult.get("url").toString();

                if (!currentAndNewImageUrls.contains(imageUrl)) {
                    Imagen newImage = Imagen.builder()
                            .url(imageUrl)
                            .activo(true) // Las nuevas imágenes están activas por defecto
                            .producto(productoExistente) // Establecer la relación
                            .build();
                    imagesToPersist.add(newImage);
                    currentAndNewImageUrls.add(imageUrl); // Añadir para futuras comprobaciones en el mismo bucle
                }
            }
        }

        // 3. Reemplazar la colección de imágenes del producto existente.
        // Esto activará el orphanRemoval para las imágenes que no estén en 'imagesToPersist'
        // y la cascada para las nuevas/modificadas.
        productoExistente.getImagenes().clear(); // Limpia la colección existente
        productoExistente.getImagenes().addAll(imagesToPersist); // Añade todas las imágenes procesadas

        // Asegurarse de que la referencia bidireccional se mantenga si por alguna razón se perdió
        productoExistente.getImagenes().forEach(img -> img.setProducto(productoExistente));
        // --- FIN: Lógica OPTIMIZADA para la GESTIÓN de IMÁGENES ---


        // --- Lógica para la GESTIÓN de PRODUCTOS_DETALLES (AJUSTADA para Soft Delete) ---
        if (productoRequestDTO.getProductos_detalles() != null) {
            Map<Long, ProductoDetalle> existingDetailsMap = productoExistente.getProductos_detalles().stream()
                    .filter(d -> d.getId() != null)
                    .collect(Collectors.toMap(ProductoDetalle::getId, d -> d));

            Set<Long> requestDetailIds = productoRequestDTO.getProductos_detalles().stream()
                    .map(ProductoDetalleRequestDTO::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            for (ProductoDetalleRequestDTO detalleRequest : productoRequestDTO.getProductos_detalles()) {
                ProductoDetalle detalle;
                if (detalleRequest.getId() != null && existingDetailsMap.containsKey(detalleRequest.getId())) {
                    detalle = existingDetailsMap.get(detalleRequest.getId());
                    detalle.setPrecioCompra(detalleRequest.getPrecioCompra());
                    detalle.setStockActual(detalleRequest.getStockActual());
                    detalle.setStockMaximo(detalleRequest.getStockMaximo());
                    detalle.setActivo(detalleRequest.isActivo());

                    Color color = colorRepository.findById(detalleRequest.getColorId())
                            .orElseThrow(() -> new EntityNotFoundException("Color con ID " + detalleRequest.getColorId() + " no encontrado."));
                    Talle talle = talleRepository.findById(detalleRequest.getTalleId())
                            .orElseThrow(() -> new EntityNotFoundException("Talle con ID " + detalleRequest.getTalleId() + " no encontrado."));
                    detalle.setColor(color);
                    detalle.setTalle(talle);

                } else {
                    detalle = new ProductoDetalle();
                    detalle.setProducto(productoExistente);
                    detalle.setPrecioCompra(detalleRequest.getPrecioCompra());
                    detalle.setStockActual(detalleRequest.getStockActual());
                    detalle.setStockMaximo(detalleRequest.getStockMaximo());
                    detalle.setActivo(detalleRequest.isActivo());

                    Color color = colorRepository.findById(detalleRequest.getColorId())
                            .orElseThrow(() -> new EntityNotFoundException("Color con ID " + detalleRequest.getColorId() + " no encontrado."));
                    Talle talle = talleRepository.findById(detalleRequest.getTalleId())
                            .orElseThrow(() -> new EntityNotFoundException("Talle con ID " + detalleRequest.getTalleId() + " no encontrado."));
                    detalle.setColor(color);
                    detalle.setTalle(talle);

                    productoExistente.getProductos_detalles().add(detalle);
                }
                productoDetalleRepository.save(detalle);
            }

            new ArrayList<>(productoExistente.getProductos_detalles()).forEach(existingDetail -> {
                if (existingDetail.getId() != null && !requestDetailIds.contains(existingDetail.getId())) {
                    if (existingDetail.isActivo()) {
                        existingDetail.setActivo(false);
                        productoDetalleRepository.save(existingDetail);
                    }
                }
            });

        } else {
            productoExistente.getProductos_detalles().forEach(d -> {
                if (d.isActivo()) {
                    d.setActivo(false);
                    productoDetalleRepository.save(d);
                }
            });
        }

        productoExistente.calcularPrecioFinal();
        return mapearProductoADTO(productoRepository.save(productoExistente));
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerProductosPromocionalesDTO() throws Exception {
        try {
            List<Producto> productos = productoRepository.findByTienePromocionTrueAndActivoTrueWithDescuento();
            productos.forEach(Producto::calcularPrecioFinal);
            return productos.stream()
                    .map(this::mapearProductoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al obtener productos promocionales DTO: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombre(String keyword) throws Exception {
        return buscarProductosPorDenominacion(keyword);
    }

    @Transactional(readOnly = true)
    public List<String> getAllAvailableCategories() throws Exception {
        try {
            return categoriaRepository.findAll().stream()
                    .map(Categoria::getDenominacion)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al obtener categorías disponibles: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<String> getAllAvailableColors() throws Exception {
        try {
            return colorRepository.findAll().stream()
                    .map(c -> c.getNombreColor())
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al obtener colores disponibles: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<String> getAllAvailableTalles() throws Exception {
        try {
            return talleRepository.findAll().stream()
                    .map(t -> t.getNombreTalle())
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al obtener talles disponibles: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> filtrarYOrdenarProductos(
            String denominacion, List<String> categorias, Sexo sexo, Boolean tienePromocion,
            BigDecimal minPrice, BigDecimal maxPrice, List<String> colores, List<String> talles,
            Integer stockMinimo, String orderBy, String orderDirection) throws Exception {
        try {
            Specification<Producto> finalSpec = ProductoSpecification.withFilters(
                    denominacion, categorias, sexo, tienePromocion, minPrice, maxPrice, colores, talles, stockMinimo
            ).and(ProductoSpecification.byActivo(true));

            Sort sort = Sort.unsorted();
            if (orderBy != null && !orderBy.isEmpty()) {
                Sort.Direction direction = "desc".equalsIgnoreCase(orderDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
                if ("precioVenta".equalsIgnoreCase(orderBy)) {
                    sort = Sort.by(direction, "precioVenta");
                } else if ("denominacion".equalsIgnoreCase(orderBy)) {
                    sort = Sort.by(direction, "denominacion");
                }
            }

            List<Producto> productos = productoRepository.findAll(finalSpec, sort);
            productos.forEach(Producto::calcularPrecioFinal);
            return productos.stream()
                    .map(this::mapearProductoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al filtrar y ordenar productos: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al filtrar y ordenar productos: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ProductoDTO activarProducto(Long id) throws Exception {
        try {
            Optional<Producto> productoOptional = productoRepository.findById(id);
            if (productoOptional.isEmpty()) {
                throw new EntityNotFoundException("Producto con ID " + id + " no encontrado para activar.");
            }
            Producto producto = productoOptional.get();
            producto.setActivo(true);
            return mapearProductoADTO(productoRepository.save(producto));
        } catch (Exception e) {
            throw new Exception("Error al activar producto por ID: " + e.getMessage());
        }
    }

    @Transactional
    public void eliminarProductoPorId(Long id) throws Exception {
        try {
            Optional<Producto> productoOptional = productoRepository.findById(id);
            if (productoOptional.isEmpty()) {
                throw new EntityNotFoundException("Producto con ID " + id + " no encontrado para desactivar.");
            }
            Producto producto = productoOptional.get();
            producto.setActivo(false);
            productoRepository.save(producto);
        } catch (Exception e) {
            throw new Exception("Error al desactivar producto por ID: " + e.getMessage());
        }
    }

    private void actualizarProductoDesdeDTO(Producto producto, ProductoRequestDTO dto) {
        producto.setDenominacion(dto.getDenominacion());
        producto.setPrecioVenta(dto.getPrecioOriginal());
        producto.setSexo(dto.getSexo());
        producto.setActivo(dto.isActivo());

        if (dto.getCategoriaIds() != null && !dto.getCategoriaIds().isEmpty()) {
            List<Categoria> categoriasList = categoriaRepository.findAllById(dto.getCategoriaIds());
            if (categoriasList.size() != dto.getCategoriaIds().size()) {
                throw new EntityNotFoundException("Una o más categorías no fueron encontradas.");
            }
            producto.setCategorias(new HashSet<>(categoriasList));
        } else {
            producto.setCategorias(Collections.emptySet());
        }
    }

    public ProductoDTO mapearProductoADTO(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId(producto.getId());
        productoDTO.setDenominacion(producto.getDenominacion());
        productoDTO.setPrecioOriginal(producto.getPrecioVenta());
        productoDTO.setPrecioFinal(producto.getPrecioFinal());
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
                        .filter(Imagen::isActivo)
                        .map(imagenEntity -> new ImagenDTO(imagenEntity.getId(), imagenEntity.getUrl(), imagenEntity.isActivo()))
                        .collect(Collectors.toList()) :
                Collections.emptyList());

        productoDTO.setProductos_detalles(producto.getProductos_detalles() != null ?
                producto.getProductos_detalles().stream()
                        .filter(ProductoDetalle::isActivo)
                        .map(this::mapearProductoDetalleADTO)
                        .collect(Collectors.toList()) :
                Collections.emptyList());

        if (producto.getDescuento() != null) {
            productoDTO.setDescuento(mapearDescuentoADTO(producto.getDescuento()));
        } else {
            productoDTO.setDescuento(null);
        }

        return productoDTO;
    }

    private CategoriaDTO mapearCategoriaADTO(Categoria categoriaEntity) {
        if (categoriaEntity == null) return null;
        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(categoriaEntity.getId());
        categoriaDTO.setDenominacion(categoriaEntity.getDenominacion());
        return categoriaDTO;
    }

    private ImagenDTO mapearImagenADTO(Imagen imagenEntity) {
        if (imagenEntity == null) return null;
        return new ImagenDTO(imagenEntity.getId(), imagenEntity.getUrl(), imagenEntity.isActivo());
    }

    private ProductoDetalleDTO mapearProductoDetalleADTO(ProductoDetalle detalleEntity) {
        if (detalleEntity == null) return null;
        ProductoDetalleDTO detalleDTO = new ProductoDetalleDTO();
        detalleDTO.setId(detalleEntity.getId());
        detalleDTO.setPrecioCompra(detalleEntity.getPrecioCompra());
        detalleDTO.setStockActual(detalleEntity.getStockActual());
        detalleDTO.setStockMaximo(detalleEntity.getStockMaximo());

        if (detalleEntity.getColor() != null) {
            detalleDTO.setColor(new ColorDTO(detalleEntity.getColor().getId(), detalleEntity.getColor().getNombreColor(), detalleEntity.getColor().isActivo()));
        } else {
            detalleDTO.setColor(null);
        }

        if (detalleEntity.getTalle() != null) {
            detalleDTO.setTalle(new TalleDTO(detalleEntity.getTalle().getId(), detalleEntity.getTalle().getNombreTalle(), detalleEntity.getTalle().isActivo()));
        } else {
            detalleDTO.setTalle(null);
        }
        detalleDTO.setCantidad(detalleEntity.getStockActual());
        detalleDTO.setActivo(detalleEntity.isActivo());

        if (detalleEntity.getProducto() != null) {
            detalleDTO.setProductoId(detalleEntity.getProducto().getId());
            detalleDTO.setProductoDenominacion(detalleEntity.getProducto().getDenominacion());
        }

        return detalleDTO;
    }

    private DescuentoDTO mapearDescuentoADTO(Descuento descuentoEntity) {
        if (descuentoEntity == null) return null;
        return DescuentoDTO.builder()
                .id(descuentoEntity.getId())
                .denominacion(descuentoEntity.getDenominacion())
                .fechaDesde(descuentoEntity.getFechaDesde())
                .fechaHasta(descuentoEntity.getFechaHasta())
                .horaDesde(descuentoEntity.getHoraDesde())
                .horaHasta(descuentoEntity.getHoraHasta())
                .descripcionDescuento(descuentoEntity.getDescripcionDescuento())
                .precioPromocional(descuentoEntity.getPrecioPromocional())
                .activo(descuentoEntity.isActivo())
                .build();
    }
}