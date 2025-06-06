package com.ecommerce.ecommerce.Entities;

import com.ecommerce.ecommerce.Entities.enums.Sexo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId; // Importación necesaria para ZoneId
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "productos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Producto extends Base {

    @Column(name = "denominacion")
    protected String denominacion;

    @Column(name = "precio_venta", columnDefinition = "DECIMAL(10,2)")
    protected BigDecimal precioVenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo_producto", length = 20)
    protected Sexo sexo;

    @Column(name = "tiene_promocion")
    private boolean tienePromocion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "descuento_id")
    @JsonIgnore
    private Descuento descuento;

    @ManyToMany
    @JoinTable(
            name = "producto_categoria",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @Builder.Default
    @JsonIgnore
    private Set<Categoria> categorias = new HashSet<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference("producto-imagenes")
    protected Set<Imagen> imagenes = new HashSet<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonManagedReference("producto-detalles")
    private Set<ProductoDetalle> productos_detalles = new HashSet<>();

    @Transient
    private BigDecimal precioFinal;

    public void calcularPrecioFinal() {
        // Inicializa precioFinal con precioVenta por si no hay descuento o promoción
        if (this.precioVenta == null) {
            this.precioFinal = BigDecimal.ZERO; // Valor por defecto seguro si precioVenta es null
        } else {
            this.precioFinal = this.precioVenta;
        }

        // Aplica el descuento si el producto tiene promoción y hay un descuento válido
        if (this.tienePromocion &&
                this.descuento != null &&
                this.descuento.isActivo() && // Asegúrate de que el descuento esté activo
                this.descuento.getPrecioPromocional() != null) {

            // Obtén la fecha y hora actuales en la zona horaria de Mendoza, Argentina
            ZoneId zoneIdMendoza = ZoneId.of("America/Argentina/Mendoza"); // Utiliza la zona horaria de Mendoza
            LocalDate today = LocalDate.now(zoneIdMendoza);
            LocalTime now = LocalTime.now(zoneIdMendoza);

            // Verifica si el descuento es válido por fecha
            boolean fechaValida = !today.isBefore(this.descuento.getFechaDesde()) && !today.isAfter(this.descuento.getFechaHasta());

            // Verifica si el descuento es válido por hora
            // Considera el caso donde la horaDesde es posterior a la horaHasta (ej. descuento nocturno)
            boolean horaValida;
            if (this.descuento.getHoraDesde().isBefore(this.descuento.getHoraHasta())) {
                horaValida = !now.isBefore(this.descuento.getHoraDesde()) && !now.isAfter(this.descuento.getHoraHasta());
            } else { // Descuento que cruza la medianoche (ej. de 22:00 a 02:00)
                horaValida = now.isAfter(this.descuento.getHoraDesde()) || now.isBefore(this.descuento.getHoraHasta());
            }

            if (fechaValida && horaValida) {
                BigDecimal porcentajeDescuento = this.descuento.getPrecioPromocional();

                // Calcula el monto del descuento: precioVenta * porcentajeDescuento
                BigDecimal montoDescuento = this.precioVenta.multiply(porcentajeDescuento);

                // El precio final es precioVenta - montoDescuento
                this.precioFinal = this.precioVenta.subtract(montoDescuento).setScale(2, RoundingMode.HALF_UP);
            }
        }

        // Asegúrate de que precioFinal siempre tenga un valor y no sea null al final del método
        if (this.precioFinal == null) {
            this.precioFinal = BigDecimal.ZERO;
        }
    }
}