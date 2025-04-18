package com.zapatillas.ecommerce.model;

import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orden_compra_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompraDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con OrdenDeCompra
    @ManyToOne
    @JoinColumn(name = "id_orden_compra", nullable = false)
    private OrdenDeCompra ordenDeCompra;

    // Relación con DetalleProducto
    @ManyToOne
    @JoinColumn(name = "id_detalle_producto", nullable = false)
    private DetalleProducto detalleProducto;

    // Cantidad de productos de este detalle
    private Integer cantidad;

    // Subtotal (cantidad * precio)
    private BigDecimal subtotal;

    // Constructor sin argumentos (usado por JPA)
    public OrdenCompraDetalle() {}

    // Constructor con los atributos
    public OrdenCompraDetalle(OrdenDeCompra ordenDeCompra, DetalleProducto detalleProducto, Integer cantidad, BigDecimal subtotal) {
        this.ordenDeCompra = ordenDeCompra;
        this.detalleProducto = detalleProducto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }
    public BigDecimal getSubtotal() {
    return this.subtotal == null ? BigDecimal.ZERO : this.subtotal;
}

    // Método para calcular el subtotal, si es necesario
  public void calcularSubtotal() {
    if (detalleProducto != null && detalleProducto.getPrecios() != null && !detalleProducto.getPrecios().isEmpty()) {
        Precio precio = detalleProducto.getPrecios().get(0); // Obtener el precio actual
        this.subtotal = BigDecimal.valueOf(precio.getPrecio()).multiply(BigDecimal.valueOf(cantidad)); // Actualizar subtotal
    } else {
        this.subtotal = BigDecimal.ZERO; // En caso de que no haya precio
    }
}

}
