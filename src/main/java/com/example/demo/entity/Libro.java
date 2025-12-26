package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "libros")
public class Libro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titulo;
    
    private String autor;
    
    private String isbn;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    private Double precio;
    
    private Integer stock;
    
    @Column(name = "urlimg")
    private String urlImg;
}
