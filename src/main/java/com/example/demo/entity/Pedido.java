package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pedidos")
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;
    
    @Column(nullable = false)
    private String tipo; // "PRESTAMO" o "COMPRA"
    
    @Column(nullable = false)
    private LocalDateTime fechaPedido;
    
    private LocalDateTime fechaDevolucion;
    
    @Column(nullable = false)
    private String estado; // "ACTIVO", "DEVUELTO", "COMPLETADO"
    
    private Double precio;
}
