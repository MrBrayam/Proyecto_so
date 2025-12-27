package com.example.demo.repository;

import com.example.demo.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    Optional<Compra> findByNumeroFactura(String numeroFactura);
    Optional<Compra> findFirstByOrderByIdDesc();
    
    List<Compra> findByFechaCompraBetween(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT SUM(c.total) FROM Compra c")
    Double calcularGastoTotal();
    
    @Query("SELECT SUM(c.total) FROM Compra c WHERE c.fechaCompra BETWEEN :inicio AND :fin")
    Double calcularGastoTotalPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
