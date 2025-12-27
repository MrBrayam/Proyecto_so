package com.example.demo.repository;

import com.example.demo.entity.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    List<Ingreso> findByTipo(String tipo);
    
    List<Ingreso> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT SUM(i.monto) FROM Ingreso i")
    Double calcularIngresoTotal();
    
    @Query("SELECT SUM(i.monto) FROM Ingreso i WHERE i.tipo = ?1")
    Double calcularIngresoPorTipo(String tipo);
    
    @Query("SELECT SUM(i.monto) FROM Ingreso i WHERE i.fecha BETWEEN :inicio AND :fin")
    Double calcularIngresoTotalPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT SUM(i.monto) FROM Ingreso i WHERE i.tipo = :tipo AND i.fecha BETWEEN :inicio AND :fin")
    Double calcularIngresoPorTipoYFecha(@Param("tipo") String tipo, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
