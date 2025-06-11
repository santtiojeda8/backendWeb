package com.ecommerce.ecommerce.Services;

import com.cloudinary.Cloudinary; // Importa la clase principal de Cloudinary
import com.cloudinary.utils.ObjectUtils; // Para opciones de subida
import org.springframework.beans.factory.annotation.Value; // Para inyectar valores de application.properties
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // Para manejar los archivos subidos

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service // Marca esta clase como un bean de servicio de Spring
public class CloudinaryService {

    private final Cloudinary cloudinary; // Instancia de Cloudinary

    // Constructor que inyecta las credenciales de Cloudinary desde application.properties
    public CloudinaryService(@Value("${cloudinary.cloud_name}") String cloudName,
                             @Value("${cloudinary.api_key}") String apiKey,
                             @Value("${cloudinary.api_secret}") String apiSecret) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        this.cloudinary = new Cloudinary(config); // Inicializa Cloudinary con las credenciales
    }

    /**
     * Sube un archivo MultipartFile a Cloudinary.
     * @param multipartFile El archivo de imagen a subir.
     * @return Un mapa con los detalles de la subida, incluyendo la URL de la imagen.
     * @throws IOException Si ocurre un error al procesar el archivo.
     */
    public Map uploadImage(MultipartFile multipartFile) throws IOException {
        // Cloudinary trabaja con objetos File, así que convertimos el MultipartFile temporalmente
        File file = convertMultiPartToFile(multipartFile);

        // Realiza la subida a Cloudinary
        // ObjectUtils.emptyMap() se usa para pasar opciones vacías. Podrías pasar opciones como la carpeta de destino:
        // cloudinary.uploader().upload(file, ObjectUtils.asMap("folder", "ecommerce_products"));
        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());

        file.delete(); // Es crucial borrar el archivo temporal después de subirlo
        return uploadResult;
    }

    /**
     * Convierte un MultipartFile a un objeto File temporal.
     * @param file El MultipartFile a convertir.
     * @return El objeto File temporal.
     * @throws IOException Si ocurre un error de E/S.
     */
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        // Guarda el contenido del MultipartFile en el archivo temporal
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    /**
     * Opcional: Elimina una imagen de Cloudinary usando su public_id.
     * Necesitarías guardar el public_id de la imagen cuando la subes si quieres usar esto.
     * @param publicId El public_id de la imagen en Cloudinary.
     * @return Un mapa con el resultado de la eliminación.
     * @throws IOException Si ocurre un error.
     */
    public Map deleteImage(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}