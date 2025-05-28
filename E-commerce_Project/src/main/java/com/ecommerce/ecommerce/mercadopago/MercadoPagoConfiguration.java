package com.ecommerce.ecommerce.mercadopago;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct; // Para que Spring ejecute este método después de la inyección de dependencias
import org.springframework.beans.factory.annotation.Value; // Para inyectar valores de application.properties
import org.springframework.context.annotation.Configuration; // Para que Spring reconozca esta clase como configuración

@Configuration // Indica a Spring que esta clase contiene definiciones de beans o configuraciones
public class MercadoPagoConfiguration {

    // Inyecta el valor del access token desde application.properties
    // El nombre de la propiedad debe coincidir con el de application.properties
    @Value("${mercadopago.access_token}") // Nota: Usar underscore si así lo definiste en properties
    private String accessToken;

    /**
     * Este método se ejecuta automáticamente después de que la instancia de MercadoPagoConfiguration
     * ha sido creada y sus dependencias (como 'accessToken') han sido inyectadas por Spring.
     * Es el lugar ideal para inicializar el SDK de Mercado Pago.
     */
    @PostConstruct
    public void init() {
        try {
            // Establece el Access Token globalmente para el SDK de Mercado Pago.
            // Este es el método estático correcto para la versión 2.1.24 del SDK.
            MercadoPagoConfig.setAccessToken(accessToken);
            System.out.println("Mercado Pago SDK inicializado correctamente con Access Token.");
        } catch (Exception e) {
            // Captura cualquier excepción durante la inicialización (ej. token inválido, problemas de red)
            System.err.println("ERROR: Falló la inicialización del SDK de Mercado Pago.");
            System.err.println("Mensaje de error: " + e.getMessage());
            System.err.println("Asegúrate de que 'mercadopago.access_token' en application.properties sea válido.");
            e.printStackTrace(); // Imprime la traza completa para depuración
        }
    }
}