package com.ecommerce.ecommerce.mercadopago;

// Importaciones de DTOs
import com.ecommerce.ecommerce.dto.MercadoPagoPreferenceRequestDTO;

// Importaciones de Entidades
import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Entities.Usuario;
import com.ecommerce.ecommerce.Entities.enums.EstadoOrdenCompra;

// Importaciones de Servicios
import com.ecommerce.ecommerce.Services.OrdenCompraService;
import com.ecommerce.ecommerce.Services.UsuarioService;
import com.ecommerce.ecommerce.exception.ResourceNotFoundException; // Importa tu excepción

// Importaciones de Mercado Pago SDK
import com.mercadopago.client.preference.*;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.net.MPResponse;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.payment.Payment;

// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal; // Aunque no se usa directamente aquí, se mantiene por si acaso
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Aunque no se usa directamente, se mantiene por si acaso
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/payments")
public class MercadoPagoController {

    @Value("${MERCADOPAGO_ACCESS_TOKEN}")
    private String accesToken;

    private final OrdenCompraService ordenCompraService;
    private final UsuarioService usuarioService;

    // Constructor que inyecta los servicios necesarios
    public MercadoPagoController(OrdenCompraService ordenCompraService,
                                 UsuarioService usuarioService) {
        this.ordenCompraService = ordenCompraService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/create-preference")
    public ResponseEntity<String> createCheckout(@RequestBody MercadoPagoPreferenceRequestDTO requestDTO) {
        try {
            // 1. Configura el Access Token de Mercado Pago
            com.mercadopago.MercadoPagoConfig.setAccessToken(accesToken);
            System.out.println("Token Mercado Pago: " + accesToken);

            // 2. Valida y obtiene el usuario del sistema
            Usuario usuario;
            try {
                usuario = usuarioService.buscarPorId(requestDTO.getUserId());
            } catch (Exception e) {
                System.err.println("Error al encontrar el usuario con ID " + requestDTO.getUserId() + ": " + e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Usuario no encontrado o inactivo con ID: " + requestDTO.getUserId() + "\"}");
            }

            // 3. Crea la Orden de Compra en tu base de datos.
            OrdenCompra ordenCompra = null;
            try {
                ordenCompra = ordenCompraService.crearOrdenInicial(
                        requestDTO.getUserId(),
                        requestDTO.getBuyerPhoneNumber(),
                        requestDTO.getNuevaDireccion(),
                        requestDTO.getDireccionId(),
                        requestDTO.getShippingOption(),
                        requestDTO.getShippingCost(),
                        requestDTO.getMontoTotal(),
                        requestDTO.getDetalles()
                );
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al crear la orden de compra en el sistema: " + e.getMessage());
            }

            // 4. Usa el ID de la orden de compra recién creada como `external_reference` para Mercado Pago.
            String externalReference = String.valueOf(ordenCompra.getId());

            // 5. Configura los detalles de la preferencia de Mercado Pago
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(requestDTO.getBack_urls().get("success"))
                    .pending(requestDTO.getBack_urls().get("pending"))
                    .failure(requestDTO.getBack_urls().get("failure"))
                    .build();

            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .name(requestDTO.getPayerName())
                    .surname(requestDTO.getPayerLastName())
                    .email(requestDTO.getPayerEmail())
                    .build();

            List<PreferenceItemRequest> items = requestDTO.getItems().stream()
                    .map(mpItem -> PreferenceItemRequest.builder()
                            .id(mpItem.getId())
                            .title(mpItem.getTitle())
                            .description(mpItem.getDescription())
                            .pictureUrl(mpItem.getPictureUrl())
                            .quantity(mpItem.getQuantity())
                            .unitPrice(mpItem.getUnitPrice())
                            .categoryId(mpItem.getCategoryId())
                            .currencyId("ARS")
                            .build())
                    .collect(Collectors.toList());

            List<PreferencePaymentTypeRequest> excludedPaymentTypes = new ArrayList<>();
            excludedPaymentTypes.add(PreferencePaymentTypeRequest.builder().id("ticket").build());

            PreferencePaymentMethodsRequest paymentMethods = PreferencePaymentMethodsRequest.builder()
                    .excludedPaymentTypes(excludedPaymentTypes)
                    .installments(1)
                    .build();

            // Construye la solicitud de preferencia completa para Mercado Pago
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrls)
                    .paymentMethods(paymentMethods)
                    .autoReturn(requestDTO.getAuto_return())
                    .externalReference(externalReference) // Clave para vincular
                    .shipments(PreferenceShipmentsRequest.builder()
                            .cost(requestDTO.getShippingCost())
                            .mode("not_specified")
                            .build())
                    .metadata(Map.of("orden_compra_id", ordenCompra.getId()))
                    .build();

            // 6. Crea la preferencia en Mercado Pago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // 7. Actualiza la Orden de Compra en tu base de datos con el ID de la preferencia de Mercado Pago
            // Aquí usamos el método 'actualizar' del BaseService, que es general.
            // Para MercadopagoPreferenceId es un simple set, no un cambio de estado con lógica de stock.
            ordenCompra.setMercadopagoPreferenceId(preference.getId());
            try {
                ordenCompraService.actualizar(ordenCompra); // Asumiendo que 'actualizar' es del BaseService
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al actualizar la orden con el ID de preferencia de Mercado Pago: " + e.getMessage());
            }

            // 8. Prepara la respuesta para el frontend
            String prefId = preference.getId();
            String initPoint = preference.getInitPoint();
            System.out.println("URL de pago de Mercado Pago: " + initPoint);

            return ResponseEntity.status(HttpStatus.OK).body("{\"preferenceId\":\"" + prefId + "\", \"initPoint\":\"" + initPoint + "\"}");

        } catch (MPApiException mpEx) {
            MPResponse response = mpEx.getApiResponse();
            System.err.println("Status code de Mercado Pago: " + response.getStatusCode());
            System.err.println("Response body de Mercado Pago: " + response.getContent());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la preferencia en Mercado Pago: " + response.getContent());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al procesar el pago: " + e.getMessage());
        }
    }
    @GetMapping("/process-payment-result")
    public ResponseEntity<String> processPaymentResult(
            @RequestParam("collection_id") Long paymentId, // ID del pago generado por Mercado Pago
            @RequestParam("collection_status") String collectionStatus, // Estado inicial del pago (ej: approved, pending, rejected)
            @RequestParam("external_reference") Long externalReference, // Tu ID de orden de compra
            @RequestParam("preference_id") String preferenceId) { // El ID de la preferencia de MP
        try {
            com.mercadopago.MercadoPagoConfig.setAccessToken(accesToken);

            System.out.println("Procesando resultado de pago desde back_url:");
            System.out.println("  Payment ID: " + paymentId);
            System.out.println("  Collection Status (inicial): " + collectionStatus);
            System.out.println("  External Reference (ID Orden Compra): " + externalReference);
            System.out.println("  Preference ID: " + preferenceId);

            // Se consulta a la API de Mercado Pago para obtener el estado final del pago.
            // Esto es crucial porque el `collection_status` inicial puede ser "pending"
            // y luego el pago podría confirmarse o rechazarse definitivamente.
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(paymentId);

            String finalStatus = payment.getStatus();
            String finalStatusDetail = payment.getStatusDetail();

            System.out.println("Estado final del pago según MP: " + finalStatus + " (" + finalStatusDetail + ")");

            OrdenCompra ordenCompra;
            try {
                // Ahora usamos buscarPorId del service, que ya maneja ResourceNotFoundException
                ordenCompra = ordenCompraService.buscarPorId(externalReference);
            } catch (ResourceNotFoundException e) {
                System.err.println("Orden de compra no encontrada para external_reference: " + externalReference);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Orden de compra no encontrada con ID: " + externalReference + "\"}");
            }

            EstadoOrdenCompra nuevoEstado;

            // Mapea el estado de Mercado Pago a tu enum EstadoOrdenCompra
            switch (finalStatus) {
                case "approved":
                    nuevoEstado = EstadoOrdenCompra.PAGADA;
                    break;
                case "pending":
                    nuevoEstado = EstadoOrdenCompra.PENDIENTE_PAGO;
                    break;
                case "rejected":
                case "cancelled": // Considera si "cancelled" también es un rechazo para tu lógica de negocio
                    nuevoEstado = EstadoOrdenCompra.RECHAZADA;
                    break;
                default:
                    // Si el estado no es reconocido, o es un estado transitorio no manejado,
                    // lo dejamos como pendiente para una posible revisión manual o futura actualización.
                    nuevoEstado = EstadoOrdenCompra.PENDIENTE_PAGO;
                    break;
            }

            // ⭐ Uso del nuevo método `actualizarEstadoOrdenYStock` ⭐
            // Este método ya se encarga de verificar el estado, actualizar y manejar el stock
            ordenCompraService.actualizarEstadoOrdenYStock(
                    ordenCompra.getId(),
                    nuevoEstado,
                    String.valueOf(paymentId) // Pasamos el paymentId de MP
            );

            return ResponseEntity.ok("{\"message\":\"Estado de la orden de compra actualizado correctamente.\", \"newStatus\":\"" + nuevoEstado.name() + "\"}");

        } catch (MPApiException mpEx) {
            MPResponse response = mpEx.getApiResponse();
            System.err.println("Error de Mercado Pago al consultar el pago - Status code: " + response.getStatusCode() + ", Body: " + response.getContent());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al consultar el pago en Mercado Pago: " + response.getContent() + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error inesperado al procesar el resultado del pago: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error interno al procesar el resultado del pago: " + e.getMessage() + "\"}");
        }
    }
}