package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "proveedores")
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(unique = true, nullable = false)
    private String ruc;
    
    @Column(nullable = false)
    private String email;
    
    private String telefono;
    
    private String direccion;
    
    @Column(name = "nombre_contacto")
    private String nombreContacto;
}
