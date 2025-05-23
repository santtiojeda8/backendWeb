package com.ecommerce.ecommerce.exception; // Asegúrate de que este es el paquete correcto

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import org.springframework.security.authentication.BadCredentialsException; // <-- ¡NUEVA IMPORTACIÓN!

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejador específico para BadCredentialsException
    // Esta excepción es comúnmente lanzada por Spring Security cuando la autenticación falla
    // (ej. contraseña incorrecta).
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.UNAUTHORIZED.value()); // Un 401 Unauthorized es más apropiado para credenciales incorrectas
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage()); // Mensaje: "Contraseña actual incorrecta."
        body.put("path", request.getDescription(false).substring(4));

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED); // Retorna 401
    }

    // Si aún quieres usar InvalidCurrentPasswordException para otros casos, déjalo.
    // Si BadCredentialsException cubrirá todo el caso de contraseña incorrecta,
    // podrías considerar eliminar InvalidCurrentPasswordException si no la usas en otro lado.
    // Por ahora, déjamos ambos para no romper nada.
    @ExceptionHandler(com.ecommerce.ecommerce.exception.InvalidCurrentPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCurrentPasswordException(com.ecommerce.ecommerce.exception.InvalidCurrentPasswordException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value()); // Código 400
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage()); // Mensaje de tu excepción personalizada
        body.put("path", request.getDescription(false).substring(4));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value()); // Código 400
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage()); // Mensaje de la excepción
        body.put("path", request.getDescription(false).substring(4));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Captura RuntimeException para los mensajes de "Usuario no encontrado"
    @ExceptionHandler(RuntimeException.class) // Puede ser más específico si quieres, como UsernameNotFoundException
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        // Puedes poner 404 Not Found si el mensaje es de "Usuario no encontrado" o 500 para otros RuntimeExceptions
        HttpStatus status = (ex.getMessage() != null && ex.getMessage().contains("no encontrado")) ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage()); // Muestra el mensaje de la excepción de RuntimeException
        body.put("path", request.getDescription(false).substring(4));
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "Ha ocurrido un error inesperado en el servidor.");
        body.put("details", ex.getMessage()); // Solo para depuración en desarrollo
        body.put("path", request.getDescription(false).substring(4));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}