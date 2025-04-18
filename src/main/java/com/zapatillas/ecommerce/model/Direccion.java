/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zapatillas.ecommerce.model;

/**
 *
 * @author astud
 */
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String localidad;
    private String pais;
    private String provincia;
    private String departamento;

    public String direccionCompleta() {
        return String.format("%s, %s, %s, %s", localidad, departamento, provincia, pais);
    }
}
