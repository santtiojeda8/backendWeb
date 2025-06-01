package com.ecommerce.ecommerce.mercadopago;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoConfiguration.class);

    @Value("${mercadopago.access_token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        try {
            logger.info("Initializing Mercado Pago SDK with Access Token: (first 5 chars) {}", accessToken.substring(0, Math.min(accessToken.length(), 5)));
            MercadoPagoConfig.setAccessToken(accessToken);
            System.out.println("DEBUG MP: Access Token utilizado para crear preferencia: " + accessToken);// Esto mostrará el token completo. Si no quieres que aparezca completo, puedes usar substring.

            logger.info("Mercado Pago SDK initialized successfully.");
        } catch (Exception e) {
            logger.error("ERROR: Failed to initialize Mercado Pago SDK.");
            logger.error("Error message: {}", e.getMessage());
            logger.error("Ensure 'mercadopago.access_token' in application.properties is valid and correct.");
            e.printStackTrace();
        }
    }

    @Bean // <--- AÑADIDO: Define PaymentClient como un bean
    public PaymentClient paymentClient() {
        // No es necesario setAccessToken aquí de nuevo, ya se hizo en init()
        return new PaymentClient();
    }

    @Bean // <--- AÑADIDO: Define PreferenceClient como un bean
    public PreferenceClient preferenceClient() {
        // No es necesario setAccessToken aquí de nuevo, ya se hizo en init()
        return new PreferenceClient();
    }
}