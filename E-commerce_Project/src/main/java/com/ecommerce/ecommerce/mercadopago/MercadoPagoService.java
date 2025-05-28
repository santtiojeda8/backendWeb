package com.ecommerce.ecommerce.mercadopago;

import com.ecommerce.ecommerce.dto.MercadoPagoPreferenceRequestDTO;

import com.mercadopago.client.common.PhoneRequest; // ¡NUEVA IMPORTACIÓN CLAVE!
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MercadoPagoService {

    public String createPaymentPreference(MercadoPagoPreferenceRequestDTO requestDTO) {
        try {
            // 1. Crear los ítems de la preferencia de Mercado Pago
            List<PreferenceItemRequest> items = requestDTO.getItems().stream()
                    .map(itemDto ->
                            PreferenceItemRequest.builder()
                                    .id(itemDto.getId())
                                    .title(itemDto.getTitle())
                                    .description(itemDto.getDescription())
                                    .pictureUrl(itemDto.getPictureUrl())
                                    .categoryId(itemDto.getCategoryId())
                                    .quantity(itemDto.getQuantity())
                                    .unitPrice(new BigDecimal(itemDto.getUnitPrice()))
                                    .build()
                    )
                    .collect(Collectors.toList());

            // 2. Añadir el costo de envío como un ítem si es "delivery"
            if ("delivery".equalsIgnoreCase(requestDTO.getShippingOption()) && requestDTO.getShippingCost() > 0) {
                items.add(
                        PreferenceItemRequest.builder()
                                .title("Costo de Envío")
                                .description("Gastos de envío a domicilio")
                                .quantity(1)
                                .unitPrice(new BigDecimal(requestDTO.getShippingCost()))
                                .build()
                );
            }

            // 3. Crear el pagador
            // Aquí está el cambio crucial: construir un objeto PhoneRequest
            PhoneRequest phoneRequest = PhoneRequest.builder()
                    .areaCode("11") // O el código de área que corresponda (puedes pasarlo desde el frontend)
                    .number(requestDTO.getBuyerPhoneNumber())
                    .build();

            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .name("Usuario")
                    .surname("de Prueba")
                    .email("test_user@example.com")
                    .phone(phoneRequest) // Se le pasa el objeto PhoneRequest
                    .build();

            // 4. Definir URLs de redirección
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:3000/checkout/success")
                    .failure("http://localhost:3000/checkout/failure")
                    .pending("http://localhost:3000/checkout/pending")
                    .build();

            // 5. Construir la preferencia de pago
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .autoReturn("approved_on_payment")
                    .backUrls(backUrls)
                    .externalReference(requestDTO.getUserId())
                    .notificationUrl("https://tudominio.com/api/mercadopago/webhook") // **¡IMPORTANTE!** Reemplaza con tu URL de webhook HTTPS real
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // Retornar la URL de redirección
            return preference.getSandboxInitPoint();

        } catch (Exception e) {
            System.err.println("Error al crear preferencia de Mercado Pago: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}