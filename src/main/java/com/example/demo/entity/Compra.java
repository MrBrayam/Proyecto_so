package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "compras")
public class Compra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Usuario que registró la compra
    
    @Column(name = "numero_factura", unique = true)
    private String numeroFactura;
    
    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;
    
    private Double subtotal;
    
    private Double impuesto;
    
    private Double total;
    
    private String observaciones;
    
    @PrePersist
    public void prePersist() {
        if (fechaCompra == null) {
            fechaCompra = LocalDateTime.now();
        }
        // El número de factura se genera en el controlador
    }
}
