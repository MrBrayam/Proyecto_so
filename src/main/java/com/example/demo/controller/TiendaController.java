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

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setLibro(libro);
        pedido.setTipo(tipo); // "PRESTAMO" o "COMPRA"
        pedido.setFechaPedido(LocalDateTime.now());
        
        // Calcular precio según tipo
        double precio;
        if (tipo.equals("PRESTAMO")) {
            precio = libro.getPrecioPrestamo(); // Precio de préstamo del libro
            pedido.setEstado("ACTIVO");
        } else {
            precio = libro.getPrecio(); // Precio completo para compra
            pedido.setEstado("COMPLETADO");
        }
        pedido.setPrecio(precio);

        // Reducir stock
        libro.setStock(libro.getStock() - 1);
        libroRepository.save(libro);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Registrar ingreso
        Ingreso ingreso = new Ingreso();
        ingreso.setPedido(pedidoGuardado);
        ingreso.setMonto(precio);
        ingreso.setTipo(tipo);
        ingreso.setDescripcion(tipo.equals("PRESTAMO") ? 
            "Préstamo de libro: " + libro.getTitulo() : 
            "Compra de libro: " + libro.getTitulo());
        ingreso.setFecha(LocalDateTime.now());
        ingresoRepository.save(ingreso);

        // Generar factura
        Factura factura = new Factura();
        factura.setPedido(pedidoGuardado);
        factura.setTipoDocumento(tipoDocumento); // BOLETA o FACTURA
        factura.setSubtotal(precio);
        factura.setImpuesto(precio * 0.19); // 19% de IVA
        factura.setTotal(precio + (precio * 0.19));
        Factura facturaGuardada = facturaRepository.save(factura);

        // Redirigir a la factura
        return "redirect:/tienda/factura/" + facturaGuardada.getId();
    }

    @GetMapping("/factura/{id}")
    public String verFactura(@PathVariable Long id, Model model, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            return "redirect:/cliente/login";
        }

        Factura factura = facturaRepository.findById(id).orElse(null);
        if (factura == null) {
            return "redirect:/cliente/mis-pedidos";
        }

        // Verificar que la factura pertenece al cliente logueado
        if (!factura.getPedido().getCliente().getId().equals(cliente.getId())) {
            return "redirect:/cliente/mis-pedidos";
        }

        model.addAttribute("factura", factura);
        return "tienda/factura";
    }
}