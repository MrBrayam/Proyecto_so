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
                               @RequestParam String tipoDocumento,
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

        // Crear pedido PENDIENTE (sin confirmar)
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setLibro(libro);
        pedido.setTipo(tipo); // "PRESTAMO" o "COMPRA"
        pedido.setTipoDocumento(tipoDocumento); // "BOLETA" o "FACTURA"
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PENDIENTE"); // Estado pendiente hasta que el admin confirme
        
        // Calcular precio según tipo
        double precio;
        if (tipo.equals("PRESTAMO")) {
            precio = libro.getPrecioPrestamo();
        } else {
            precio = libro.getPrecio();
        }
        pedido.setPrecio(precio);

        // Guardar el pedido pendiente
        pedidoRepository.save(pedido);

        // Redirigir a mis pedidos
        return "redirect:/cliente/mis-pedidos";
    }

}