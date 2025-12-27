package com.example.demo.controller;

import com.example.demo.entity.Cliente;
import com.example.demo.entity.Libro;
import com.example.demo.entity.Pedido;
import com.example.demo.entity.Ingreso;
import com.example.demo.entity.Factura;
import com.example.demo.repository.LibroRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.IngresoRepository;
import com.example.demo.repository.FacturaRepository;
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

    @Autowired
    private IngresoRepository ingresoRepository;

    @Autowired
    private FacturaRepository facturaRepository;

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

        // Validar que si quiere factura, tenga RUC
        if (tipoDocumento.equals("FACTURA") && (cliente.getRuc() == null || cliente.getRuc().isEmpty())) {
            model.addAttribute("error", "Debes agregar tu RUC en tu perfil para solicitar una factura");
            Libro libro = libroRepository.findById(id).orElse(null);
            model.addAttribute("libro", libro);
            return "tienda/detalle";
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
        
        // Guardar el tipo de documento en sesión para este pedido
        session.setAttribute("tipoDocumentoPedido_" + pedido.getId(), tipoDocumento);

        model.addAttribute("mensaje", "Solicitud enviada. Diríjase al bibliotecario para confirmar su " + 
                                      (tipo.equals("PRESTAMO") ? "préstamo" : "compra"));
        model.addAttribute("libro", libro);
        return "tienda/detalle";
    }

    // Eliminar el endpoint de factura de tienda (se moverá a admin)
}