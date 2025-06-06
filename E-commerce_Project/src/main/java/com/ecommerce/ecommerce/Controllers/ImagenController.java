package com.ecommerce.ecommerce.Controllers;

import com.ecommerce.ecommerce.Entities.Imagen;
import com.ecommerce.ecommerce.Services.CloudinaryService; // ¡Importa el servicio de Cloudinary!
import com.ecommerce.ecommerce.Services.ImagenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Necesario para recibir archivos

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/imagen") // Mantén tu mapping base
@CrossOrigin(origins = "*") // Asegúrate de que el CORS esté configurado adecuadamente
public class ImagenController extends BaseController<Imagen, Long> {

    private final ImagenService imagenService;
    private final CloudinaryService cloudinaryService; // ¡Nueva inyección!

    @Autowired // Inyecta ambos servicios en el constructor
    public ImagenController(ImagenService imagenService, CloudinaryService cloudinaryService) {
        super(imagenService); // Llama al constructor de la clase base
        this.imagenService = imagenService;
        this.cloudinaryService = cloudinaryService; // Asigna el servicio de Cloudinary
    }


    // --- NUEVO ENDPOINT DEDICADO PARA SUBIR ARCHIVOS DE IMAGEN A CLOUDINARY ---
    @PostMapping("/upload") // Este será el endpoint que tu frontend llamará para subir archivos
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>("Por favor, selecciona un archivo para subir.", HttpStatus.BAD_REQUEST);
            }

            // Llama al servicio de Cloudinary para subir la imagen
            Map uploadResult = cloudinaryService.uploadImage(file);

            // Cloudinary devuelve un mapa con varios detalles, la URL está en la clave "url"
            String imageUrl = (String) uploadResult.get("url");
            // Opcional: También podrías querer guardar el public_id si planeas borrar imágenes de Cloudinary más tarde
            // String publicId = (String) uploadResult.get("public_id");

            // Prepara la respuesta para el frontend
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl); // El frontend espera la URL de la imagen

            // Puedes devolver más datos si los necesitas en el frontend, como el public_id
            // response.put("public_id", publicId);

            return new ResponseEntity<>(response, HttpStatus.OK); // Devuelve la URL con un 200 OK

        } catch (IOException e) {
            System.err.println("Error al subir la imagen a Cloudinary: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error al subir la imagen: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) { // Captura cualquier otra excepción inesperada
            System.err.println("Error inesperado en la subida de imagen: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error inesperado en la subida: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}