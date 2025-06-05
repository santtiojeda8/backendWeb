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

// Importaciones de Mercado Pago SDK
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.net.MPResponse;
import com.mercadopago.resources.preference.Preference;

// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                // ⭐ CORRECCIÓN AQUÍ: Llamamos directamente a buscarPorId y manejamos su posible excepción ⭐
                usuario = usuarioService.buscarPorId(requestDTO.getUserId());
            } catch (Exception e) {
                // Captura la excepción lanzada por UsuarioService.buscarPorId si el usuario no es encontrado
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
                // Si ocurre un error al crear la orden en tu sistema, devuelve un error 500
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

            // Mapea los ítems del DTO del frontend a los objetos de ítem que Mercado Pago espera
            List<PreferenceItemRequest> items = requestDTO.getItems().stream()
                    .map(mpItem -> PreferenceItemRequest.builder()
                            .id(mpItem.getId())
                            .title(mpItem.getTitle())
                            .description(mpItem.getDescription())
                            .pictureUrl(mpItem.getPictureUrl())
                            .quantity(mpItem.getQuantity())
                            .unitPrice(mpItem.getUnitPrice())
                            .categoryId(mpItem.getCategoryId())
                            .currencyId("ARS") // Moneda Argentina
                            .build())
                    .collect(Collectors.toList());

            // Excluye tipos de pago si es necesario (ej. "ticket" para boletas de pago)
            List<PreferencePaymentTypeRequest> excludedPaymentTypes = new ArrayList<>();
            excludedPaymentTypes.add(PreferencePaymentTypeRequest.builder().id("ticket").build());

            PreferencePaymentMethodsRequest paymentMethods = PreferencePaymentMethodsRequest.builder()
                    .excludedPaymentTypes(excludedPaymentTypes)
                    .installments(1) // Número de cuotas permitidas
                    .build();

            // Construye la solicitud de preferencia completa para Mercado Pago
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrls)
                    .paymentMethods(paymentMethods)
                    .autoReturn(requestDTO.getAuto_return())
                    .externalReference(externalReference)
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
            ordenCompra.setMercadopagoPreferenceId(preference.getId());
            try {
                ordenCompraService.actualizar(ordenCompra);
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
}