package com.ecommerce.ecommerce.config; // Asegúrate de que el paquete sea correcto

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}") // Inyecta el valor del directorio de subidas
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Este manejador de recursos expone los archivos en el directorio 'uploads'
        // bajo la URL /uploads/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/"); // Mapea a la ruta del sistema de archivos
    }
}