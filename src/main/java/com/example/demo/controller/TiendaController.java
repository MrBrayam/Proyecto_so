package com.example.demo.controller;

import com.example.demo.entity.Cliente;
import com.example.demo.entity.Libro;
import com.example.demo.entity.Pedido;
import com.example.demo.repository.LibroRepository;
import com.example.demo.repository.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/tienda")
public class TiendaController {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("libros", libroRepository.findAll());
        return "tienda/index";
    }

    @GetMapping("/libro/{id}")
    public String verLibro(@PathVariable Long id, Model model) {
        Libro libro = libroRepository.findById(id).orElse(null);
        model.addAttribute("libro", libro);
        return "tienda/detalle";
    }

    @PostMapping("/libro/{id}/comprar")
    public String comprarLibro(@PathVariable Long id, @RequestParam String tipo, 
                               HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            return "redirect:/cliente/login";
        }

        Libro libro = libroRepository.findById(id).orElse(null);
        if (libro == null || libro.getStock() <= 0) {
            model.addAttribute("error", "Libro no disponible");
            model.addAttribute("libro", libro);
            return "tienda/detalle";
        }

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setLibro(libro);
        pedido.setTipo(tipo); // "PRESTAMO" o "COMPRA"
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setPrecio(libro.getPrecio());
        
        if (tipo.equals("PRESTAMO")) {
            pedido.setEstado("ACTIVO");
        } else {
            pedido.setEstado("COMPLETADO");
        }

        // Reducir stock
        libro.setStock(libro.getStock() - 1);
        libroRepository.save(libro);
        pedidoRepository.save(pedido);

        return "redirect:/cliente/mis-pedidos";
    }
}
