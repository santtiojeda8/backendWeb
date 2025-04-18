package com.zapatillas.ecommerce.model;

import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orden_de_compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenDeCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Usuario
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Relación con Direccion
    @ManyToOne
    @JoinColumn(name = "id_direccion", nullable = false)
    private Direccion direccion;

    // Fecha de compra
    private String fechaCompra;

    // Total de la compra
    private BigDecimal total;

    // Relación con Descuento
    @ManyToOne
    @JoinColumn(name = "id_descuento")
    private Descuento descuento; // Ahora relacionado con la tabla Descuento

    // Relación con los detalles de la orden (productos dentro de la orden)
    @OneToMany(mappedBy = "ordenDeCompra", cascade = CascadeType.ALL)
    private List<OrdenCompraDetalle> ordenCompraDetalles;

    // Constructor sin argumentos (usado por JPA)
    public OrdenDeCompra() {}

    // Constructor con los atributos
    public OrdenDeCompra(Usuario usuario, Direccion direccion, String fechaCompra, BigDecimal total, Descuento descuento) {
        this.usuario = usuario;
        this.direccion = direccion;
        this.fechaCompra = fechaCompra;
        this.total = total;
        this.descuento = descuento; // Aplicación de descuento
    }
    // Calcular el total de la compra sumando los subtotales
public void calcularTotalSinDescuento() {
    if (ordenCompraDetalles != null && !ordenCompraDetalles.isEmpty()) {
        this.total = ordenCompraDetalles.stream()
                .peek(ordenCompraDetalle -> ordenCompraDetalle.calcularSubtotal())  // Aseguramos que se calcule el subtotal antes de acceder a él
                .map(OrdenCompraDetalle::getSubtotal)  // Ahora podemos acceder al subtotal calculado
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sumar los subtotales
    } else {
        this.total = BigDecimal.ZERO;
    }
}



public void calcularTotalConDescuento() {
    calcularTotalSinDescuento(); // Primero calculamos el total sin descuento

    if (descuento != null && descuento.getPorcentaje() > 0) {
        BigDecimal porcentaje = BigDecimal.valueOf(descuento.getPorcentaje()).divide(BigDecimal.valueOf(100));
        BigDecimal descuentoAplicado = total.multiply(porcentaje);
        total = total.subtract(descuentoAplicado);
    }
}
}
