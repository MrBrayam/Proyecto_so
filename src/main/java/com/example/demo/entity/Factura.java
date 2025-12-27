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
        if (numeroFactura == null) {
            numeroFactura = "FAC-" + System.currentTimeMillis();
        }
    }
}
