package com.ecommerce.ecommerce.mercadopago;


import com.ecommerce.ecommerce.dto.MercadoPagoPreferenceRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mercadopago")
public class MercadoPagoController {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @PostMapping("/create-preference")
    public ResponseEntity<?> createPreference(@RequestBody MercadoPagoPreferenceRequestDTO requestDTO) {
        try {
            String initPoint = mercadoPagoService.createPaymentPreference(requestDTO);

            if (initPoint != null) {
                Map<String, String> response = new HashMap<>();
                response.put("init_point", initPoint); // Envía la URL de MP
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(500).body("Error al crear la preferencia de pago en Mercado Pago.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Opcional: Endpoint para recibir notificaciones de webhook de Mercado Pago
    // Este es CRÍTICO para actualizar el estado de tu orden en tu DB
    // Mercado Pago hará una llamada POST a esta URL cuando el estado del pago cambie.
    // Necesitas que esta URL sea accesible desde internet (puedes usar ngrok para desarrollo)
    @PostMapping("/webhook")
    public ResponseEntity<?> handleMercadoPagoWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Webhook de Mercado Pago recibido: " + payload);
        // TODO: Implementar la lógica para verificar la autenticidad del webhook,
        // obtener el ID del pago (del external_reference o del payload),
        // y actualizar el estado de la orden en tu base de datos.
        // Es fundamental responder con 200 OK a Mercado Pago para que no reintente.
        return ResponseEntity.ok().build();
    }
}