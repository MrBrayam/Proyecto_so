package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Data
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Bibliotecario que generó la factura
    
    @Column(name = "numero_factura", unique = true)
    private String numeroFactura;
    
    @Column(name = "tipo_documento")
    private String tipoDocumento; // BOLETA o FACTURA
    
    private LocalDateTime fechaEmision;
    
    private Double subtotal;
    
    private Double impuesto; // IVA u otro impuesto
    
    private Double total;
    
    @PrePersist
    public void prePersist() {
        if (fechaEmision == null) {
            fechaEmision = LocalDateTime.now();
        }
        // El número de factura se genera en el controlador
    }
}
