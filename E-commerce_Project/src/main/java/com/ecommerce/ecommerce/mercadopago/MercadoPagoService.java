package com.ecommerce.ecommerce.mercadopago;

import com.ecommerce.ecommerce.dto.MercadoPagoPreferenceRequestDTO;
import com.ecommerce.ecommerce.dto.OrdenCompraDetalleDTO;
import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Entities.enums.EstadoOrdenCompra;
import com.ecommerce.ecommerce.Repositories.OrdenCompraRepository;
import com.ecommerce.ecommerce.Services.OrdenCompraService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList; // Importar ArrayList
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MercadoPagoService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${mercadopago.redirect.success}")
    private String redirectSuccessBaseUrl;
    @Value("${mercadopago.redirect.failure}")
    private String redirectFailureBaseUrl;
    @Value("${mercadopago.redirect.pending}")
    private String redirectPendingBaseUrl;
    @Value("${mercadopago.access_token}")
    private String mercadoPagoAccessToken;
    @Value("${mercadopago.notification_url_base}")
    private String notificationUrlBase;

    @Value("${mercadopago.production.enabled:false}")
    private boolean productionEnabled;

    @Autowired
    private OrdenCompraService ordenCompraService;
    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @PostConstruct
    public void init() {
        logger.info("Inicializando MercadoPagoConfig con Access Token.");
        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
    }

    @Transactional
    public String createPaymentPreference(MercadoPagoPreferenceRequestDTO requestDTO) throws MPException, MPApiException {
        try {
            // Log del DTO recibido para depuración
            try {
                String receivedDtoJson = objectMapper.writeValueAsString(requestDTO);
                logger.info("MercadoPagoPreferenceRequestDTO recibido del frontend: {}", receivedDtoJson);
            } catch (JsonProcessingException e) {
                logger.error("Error al serializar el DTO recibido a JSON para log: {}", e.getMessage());
            }

            // Validaciones iniciales
            if (requestDTO.getItems() == null || requestDTO.getItems().isEmpty()) {
                throw new IllegalArgumentException("La lista de ítems no puede estar vacía.");
            }
            if (requestDTO.getUserId() == null) {
                throw new IllegalArgumentException("El ID de usuario es obligatorio para crear la orden de compra.");
            }

            // Adaptar los ítems del carrito a DTOs de detalles de orden
            List<OrdenCompraDetalleDTO> detallesOrden = requestDTO.getItems().stream()
                    .map(itemDto -> {
                        OrdenCompraDetalleDTO ocddto = new OrdenCompraDetalleDTO();
                        ocddto.setCantidad(itemDto.getQuantity());
                        // Asegurarse de que el precio unitario sea un BigDecimal y se asigne directamente.
                        ocddto.setPrecioUnitario(itemDto.getUnitPrice());
                        ocddto.setProductoDetalleId(Long.valueOf(itemDto.getId())); // Asegúrate de que el id del item sea el id de productoDetalle
                        return ocddto;
                    })
                    .collect(Collectors.toList());

            // Crear los ítems de la preferencia de Mercado Pago y calcular el total definitivo
            // Usamos un ArrayList temporal para los items de MP y calculamos el montoTotal fuera del stream.map()
            List<PreferenceItemRequest> mpItems = new ArrayList<>();
            BigDecimal montoTotalCalculadoParaMP = BigDecimal.ZERO;

            for (com.ecommerce.ecommerce.dto.MercadoPagoItemDTO itemDto : requestDTO.getItems()) {
                if (itemDto.getUnitPrice() == null) {
                    logger.error("Ítem con ID {} tiene un unitPrice nulo. Se requiere un precio válido.", itemDto.getId());
                    throw new IllegalArgumentException("El precio unitario no puede ser nulo para el ítem: " + itemDto.getTitle());
                }
                if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                    logger.error("Ítem con ID {} tiene una cantidad inválida ({}). La cantidad debe ser mayor a 0.", itemDto.getId(), itemDto.getQuantity());
                    throw new IllegalArgumentException("La cantidad debe ser mayor a 0 para el ítem: " + itemDto.getTitle());
                }

                // Redondear a 2 decimales para MP
                BigDecimal unitPriceRounded = itemDto.getUnitPrice().setScale(2, RoundingMode.HALF_UP);
                BigDecimal itemSubtotal = unitPriceRounded.multiply(new BigDecimal(itemDto.getQuantity()));
                montoTotalCalculadoParaMP = montoTotalCalculadoParaMP.add(itemSubtotal); // Sumar al total definitivo

                mpItems.add(
                        PreferenceItemRequest.builder()
                                .id(itemDto.getId())
                                .title(itemDto.getTitle())
                                .description(itemDto.getDescription())
                                .pictureUrl(itemDto.getPictureUrl())
                                .categoryId(itemDto.getCategoryId())
                                .quantity(itemDto.getQuantity())
                                .unitPrice(unitPriceRounded)
                                .build()
                );
            }

            // Añadir el costo de envío como un ítem separado (si aplica)
            if ("delivery".equalsIgnoreCase(requestDTO.getShippingOption()) &&
                    requestDTO.getShippingCost() != null && requestDTO.getShippingCost().compareTo(BigDecimal.ZERO) > 0) {
                // Redondear a 2 decimales para MP
                BigDecimal shippingCostRounded = requestDTO.getShippingCost().setScale(2, RoundingMode.HALF_UP);
                mpItems.add(
                        PreferenceItemRequest.builder()
                                .title("Costo de Envío")
                                .description("Gastos de envío a domicilio")
                                .quantity(1)
                                .unitPrice(shippingCostRounded)
                                .build()
                );
                montoTotalCalculadoParaMP = montoTotalCalculadoParaMP.add(shippingCostRounded); // Sumar al total definitivo
            }

            // Validar la suma de ítems calculada contra el montoTotal del requestDTO (si quieres mantener la validación)
            if (requestDTO.getMontoTotal() != null) {
                BigDecimal requestDTORoundedTotal = requestDTO.getMontoTotal().setScale(2, RoundingMode.HALF_UP);
                if (montoTotalCalculadoParaMP.compareTo(requestDTORoundedTotal) != 0) {
                    logger.warn("Discrepancia entre la suma de ítems calculada para MP ({}) y el montoTotal del DTO ({}) recibido del frontend. Mercado Pago usará el total calculado.",
                            montoTotalCalculadoParaMP, requestDTORoundedTotal);
                    // Aquí podrías lanzar una excepción si la discrepancia es crítica
                } else {
                    logger.info("La suma de ítems para Mercado Pago ({}) coincide con el monto total del DTO ({}).",
                            montoTotalCalculadoParaMP, requestDTORoundedTotal);
                }
            }


            // **PASO CLAVE 1: CREAR LA ORDEN DE COMPRA EN ESTADO PENDIENTE**
            // Se pasa el montoTotal calculado definitivamente para Mercado Pago
            OrdenCompra ordenCompra = ordenCompraService.crearOrdenInicial(
                    requestDTO.getUserId(),
                    requestDTO.getShippingAddress(),
                    requestDTO.getBuyerPhoneNumber(),
                    requestDTO.getNuevaDireccion(),
                    requestDTO.getDireccionId(),
                    requestDTO.getShippingOption(),
                    requestDTO.getShippingCost(), // Esto es el costo de envío original, no el total
                    montoTotalCalculadoParaMP,    // <--- ¡¡¡Pasamos el total calculado por el backend!!!
                    detallesOrden
            );
            logger.info("OrdenCompra {} creada en estado {} antes de Mercado Pago con monto total: {}.", ordenCompra.getId(), ordenCompra.getEstadoOrden(), montoTotalCalculadoParaMP);


            // Crear el pagador
            String payerEmailForMp = (requestDTO.getPayerEmail() != null && !requestDTO.getPayerEmail().isEmpty())
                    ? requestDTO.getPayerEmail()
                    : "test_user_169965231@testuser.com"; // Email de prueba si no hay uno real

            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email(payerEmailForMp)
                    .name(requestDTO.getPayerName())
                    .surname(requestDTO.getPayerLastName())
                    .build();

            // Definir URLs de redirección incluyendo el ID de la orden de compra
            String successUrlWithOrderId = redirectSuccessBaseUrl + "?orderId=" + ordenCompra.getId();
            String failureUrlWithOrderId = redirectFailureBaseUrl + "?orderId=" + ordenCompra.getId();
            String pendingUrlWithOrderId = redirectPendingBaseUrl + "?orderId=" + ordenCompra.getId();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(successUrlWithOrderId)
                    .failure(failureUrlWithOrderId)
                    .pending(pendingUrlWithOrderId)
                    .build();

            // **IMPORTANTE: Configurar la notification_url para el webhook**
            String notificationUrl = notificationUrlBase + "/api/mercadopago/webhook";
            logger.info("URL de notificación de Mercado Pago configurada: {}", notificationUrl);


            // Construir la preferencia de pago
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(mpItems) // <--- Usamos la lista de items de MP ya construida
                    .payer(payer)
                    .autoReturn("approved")
                    .backUrls(backUrls)
                    .externalReference(ordenCompra.getId().toString())
                    .notificationUrl(notificationUrl)
                    .statementDescriptor("ECOMMERCEGONZALEZ")
                    // No se necesita .totalAmount() aquí, MP lo calcula de los items.
                    .build();

            try {
                String preferenceRequestJson = objectMapper.writeValueAsString(preferenceRequest);
                logger.info("JSON de PreferenceRequest enviado a Mercado Pago: {}", preferenceRequestJson);
            } catch (JsonProcessingException e) {
                logger.error("Error al serializar PreferenceRequest a JSON para log: {}", e.getMessage());
            }

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // **PASO CLAVE 2: GUARDAR EL preferenceId DE MP EN LA ORDEN DE COMPRA**
            ordenCompra.setMercadopagoPreferenceId(preference.getId());
            ordenCompra.setEstadoOrden(EstadoOrdenCompra.PENDIENTE_PAGO);
            ordenCompraRepository.save(ordenCompra);

            if (productionEnabled) {
                logger.info("Devolviendo init_point para producción: {}", preference.getInitPoint());
                return preference.getInitPoint();
            } else {
                logger.info("Devolviendo sandbox_init_point para desarrollo: {}", preference.getSandboxInitPoint());
                return preference.getSandboxInitPoint();
            }

        } catch (MPApiException e) {
            logger.error("Error de la API de Mercado Pago al crear preferencia: {}", e.getMessage());
            if (e.getApiResponse() != null && e.getApiResponse().getContent() != null) {
                logger.error("Detalles de la respuesta de la API de Mercado Pago: {}", e.getApiResponse().getContent());
            } else {
                logger.error("No se pudieron obtener detalles adicionales de la respuesta de la API de Mercado Pago.");
            }
            throw new MPException("Error en la API de Mercado Pago: " + e.getMessage(), e);
        } catch (MPException e) {
            logger.error("Error del SDK de Mercado Pago: {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación de datos para Mercado Pago: {}", e.getMessage());
            throw new IllegalArgumentException("Error de datos para crear preferencia: " + e.getMessage(), e);
        }
        catch (Exception e) {
            logger.error("Error inesperado al crear preferencia de Mercado Pago: {}", e.getMessage(), e);
            throw new RuntimeException("Error inesperado al crear preferencia: " + e.getMessage(), e);
        }
    }
}