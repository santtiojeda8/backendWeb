package com.ecommerce.ecommerce.mercadopago;

import com.ecommerce.ecommerce.dto.MercadoPagoPreferenceRequestDTO;
import com.ecommerce.ecommerce.dto.OrdenCompraDTO;
import com.ecommerce.ecommerce.Entities.OrdenCompra;
import com.ecommerce.ecommerce.Services.OrdenCompraService;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// IMPORTACIONES ADICIONALES NECESARIAS
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/mercadopago")
@CrossOrigin("*")
public class MercadoPagoController {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoController.class);
    private final ObjectMapper objectMapper = new ObjectMapper(); // <--- AÑADE ESTA LÍNEA

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @Autowired
    private OrdenCompraService ordenCompraService;

    @PostMapping("/create-preference")
    public ResponseEntity<?> createPreference(@RequestBody MercadoPagoPreferenceRequestDTO requestDTO) {
        try {
            logger.info("MercadoPagoPreferenceRequestDTO recibido del frontend: {}", requestDTO.toString());
            String initPoint = mercadoPagoService.createPaymentPreference(requestDTO);

            if (initPoint != null && !initPoint.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("init_point", initPoint);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("El servicio de Mercado Pago retornó un initPoint nulo o vacío.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la preferencia de pago: No se pudo obtener la URL de Mercado Pago.");
            }
        } catch (MPApiException e) {
            logger.error("MPApiException en el controlador: {}", e.getMessage());
            String mpErrorDetails = (e.getApiResponse() != null && e.getApiResponse().getContent() != null)
                    ? e.getApiResponse().getContent() : "No hay detalles adicionales de la API.";
            logger.error("Detalles de la API de Mercado Pago en el controlador: {}", mpErrorDetails);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de la API de Mercado Pago: " + e.getMessage() + " Detalles: " + mpErrorDetails);
        } catch (MPException e) {
            logger.error("MPException (SDK) en el controlador: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error del SDK de Mercado Pago: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación de datos en el controlador: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de validación de datos: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado en el controlador al crear preferencia: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
        }
    }


    @PostMapping("/webhook")
    public ResponseEntity<?> handleMercadoPagoWebhook(
            @RequestBody(required = false) String payload
    ) {
        logger.info("---- WEBHOOK DE MERCADO PAGO RECIBIDO ----");
        logger.info("Payload: {}", payload != null && payload.length() > 500 ? payload.substring(0, 500) + "..." : payload);

        String id = null;
        String topic = null;

        if (payload != null && !payload.isEmpty()) {
            try {
                JsonNode rootNode = objectMapper.readTree(payload);

                // Intentar obtener 'id' y 'topic' del formato "payment.created"
                if (rootNode.has("data") && rootNode.get("data").has("id")) {
                    id = rootNode.get("data").get("id").asText(); // ID del pago
                    if (rootNode.has("type")) {
                        topic = rootNode.get("type").asText(); // Tipo de evento (e.g., "payment")
                    }
                } else if (rootNode.has("resource") && rootNode.has("topic")) {
                    // Intentar obtener 'id' y 'topic' del formato "merchant_order"
                    // Para merchant_order, el ID relevante está en la URL del recurso
                    String resourceUrl = rootNode.get("resource").asText();
                    // Extraer el ID del final de la URL, por ejemplo: /merchant_orders/31385300497 -> 31385300497
                    int lastSlashIndex = resourceUrl.lastIndexOf('/');
                    if (lastSlashIndex != -1 && lastSlashIndex < resourceUrl.length() - 1) {
                        id = resourceUrl.substring(lastSlashIndex + 1);
                    }
                    topic = rootNode.get("topic").asText(); // Tipo de evento (e.g., "merchant_order")
                } else if (rootNode.has("id") && rootNode.get("id").isTextual() && rootNode.has("topic") && rootNode.get("topic").isTextual()) {
                    // Esto es un fallback por si el formato es simplemente { "id": "...", "topic": "..." }
                    id = rootNode.get("id").asText();
                    topic = rootNode.get("topic").asText();
                }

                logger.info("Payload parseado - ID: {}, Topic: {}", id, topic);

            } catch (Exception e) {
                logger.error("Error al parsear el payload del webhook: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al parsear el payload del webhook.");
            }
        }

        // El resto de la lógica permanece igual
        if (id == null || topic == null) {
            logger.warn("Webhook recibido sin ID o Topic válido en el payload. Ignorando.");
            return ResponseEntity.badRequest().body("ID o Topic ausente en la notificación.");
        }

        if ("payment".equalsIgnoreCase(topic)) {
            try {
                ordenCompraService.procesarNotificacionPagoMP(id);
                logger.info("Webhook de pago procesado exitosamente para el ID de pago: {}", id);
                return ResponseEntity.ok("Webhook de pago procesado exitosamente.");
            } catch (Exception e) {
                logger.error("Error procesando webhook de pago para ID {}: {}", id, e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando webhook de pago.");
            }
        } else if ("merchant_order".equalsIgnoreCase(topic)) {
            logger.info("Webhook recibido con topic 'merchant_order'. No se procesa a nivel de orden de compra, pero se reconoce. ID de Merchant Order: {}", id);
            // Aquí podrías agregar lógica para merchant_order si la necesitas,
            // por ejemplo, para unificar información de una merchant_order con pagos asociados.
            // Por ahora, simplemente lo ignoramos pero lo logueamos como reconocido.
            return ResponseEntity.ok("Webhook de merchant_order recibido, pero no se procesa en este endpoint.");
        }
        else {
            logger.info("Webhook recibido con topic '{}'. No es un topic de pago ni merchant_order, ignorando.", topic);
            return ResponseEntity.ok("Webhook recibido, pero no es una notificación de pago o merchant_order relevante.");
        }
    }

    // El endpoint /feedback permanece igual
    @GetMapping("/feedback")
    public ResponseEntity<?> handlePaymentFeedback(
            @RequestParam(name = "collection_id", required = false) String collectionId,
            @RequestParam(name = "collection_status", required = false) String collectionStatus,
            @RequestParam(name = "external_reference", required = false) String externalReference,
            @RequestParam(name = "payment_id", required = false) String paymentId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "preference_id", required = false) String preferenceId
    ) {
        logger.info("Recibido feedback de pago de Mercado Pago (Redirección). Parámetros: collection_id={}, collection_status={}, external_reference={}, payment_id={}, status={}",
                collectionId, collectionStatus, externalReference, paymentId, status);

        if (externalReference != null && !externalReference.isEmpty()) {
            try {
                OrdenCompra orden = ordenCompraService.buscarPorId(Long.valueOf(externalReference));
                if (orden != null) {
                    OrdenCompraDTO ordenDTO = ordenCompraService.mapOrdenCompraToDTO(orden);
                    return ResponseEntity.ok().body(ordenDTO);
                }
            } catch (NumberFormatException e) {
                logger.warn("external_reference no es un ID numérico válido: {}", externalReference);
            } catch (Exception e) {
                logger.error("Error al buscar orden por external_reference {}: {}", externalReference, e.getMessage());
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "El estado de tu pago está siendo procesado. Revisa tus pedidos para la confirmación final.");
        response.put("collection_status", collectionStatus != null ? collectionStatus : status);
        response.put("external_reference", externalReference);
        return ResponseEntity.ok(response);
    }
}