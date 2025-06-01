package com.ecommerce.ecommerce.Entities.enums;

public enum EstadoOrdenCompra {
    CREADA,             // La orden se generó y se espera la interacción con el pago (antes de ir a MP)
    PENDIENTE_PAGO,     // El usuario fue redirigido a MP, esperando confirmación del pago
    PAGADA,             // Pago aprobado
    RECHAZADA,          // Pago rechazado
    CANCELADA,          // Orden cancelada (ej. por el usuario o administrador)
    EN_PROCESO,         // Pago aprobado, la orden está siendo preparada
    ENVIADA,            // La orden ha sido enviada
    ENTREGADA           // La orden ha sido entregada
}