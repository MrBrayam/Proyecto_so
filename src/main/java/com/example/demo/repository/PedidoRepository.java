package com.example.demo.repository;

import com.example.demo.entity.Pedido;
import com.example.demo.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCliente(Cliente cliente);
    List<Pedido> findByTipoAndEstado(String tipo, String estado);
    List<Pedido> findByEstado(String estado);
}
