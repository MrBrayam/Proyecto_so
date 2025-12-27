package com.example.demo.controller;

import com.example.demo.entity.Cliente;
import com.example.demo.entity.Pedido;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session) {
        // Si ya está logueado, redirigir a la tienda
        if (session.getAttribute("clienteLogueado") != null) {
            return "redirect:/tienda";
        }
        model.addAttribute("error", null);
        return "cliente/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, 
                       HttpSession session, Model model) {
        Cliente cliente = clienteRepository.findByEmail(email);
        
        if (cliente != null && cliente.getPassword() != null && cliente.getPassword().equals(password)) {
            session.setAttribute("clienteLogueado", cliente);
            return "redirect:/tienda";
        } else {
            model.addAttribute("error", "Email o contraseña incorrectos");
            return "cliente/login";
        }
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente/registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Cliente cliente, Model model) {
        // Verificar si el email ya existe
        if (clienteRepository.findByEmail(cliente.getEmail()) != null) {
            model.addAttribute("error", "El email ya está registrado");
            return "cliente/registro";
        }
        
        clienteRepository.save(cliente);
        model.addAttribute("mensaje", "Registro exitoso. Ya puedes iniciar sesión.");
        return "cliente/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("clienteLogueado");
        return "redirect:/tienda";
    }

    @GetMapping("/mis-pedidos")
    public String misPedidos(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            return "redirect:/cliente/login";
        }
        
        List<Pedido> pedidos = pedidoRepository.findByCliente(cliente);
        model.addAttribute("pedidos", pedidos);
        return "cliente/mis-pedidos";
    }

    @GetMapping("/perfil")
    public String verPerfil(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            return "redirect:/cliente/login";
        }
        
        model.addAttribute("cliente", cliente);
        return "cliente/perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute Cliente clienteActualizado, 
                                   HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            return "redirect:/cliente/login";
        }

        // Obtener cliente de la base de datos
        Cliente clienteDb = clienteRepository.findById(cliente.getId()).orElse(null);
        if (clienteDb == null) {
            return "redirect:/cliente/login";
        }

        // Actualizar solo los campos permitidos (NO el DNI)
        clienteDb.setNombre(clienteActualizado.getNombre());
        clienteDb.setApellido(clienteActualizado.getApellido());
        clienteDb.setTelefono(clienteActualizado.getTelefono());
        clienteDb.setDireccion(clienteActualizado.getDireccion());
        clienteDb.setRuc(clienteActualizado.getRuc());
        
        // Si quiere cambiar contraseña
        if (clienteActualizado.getPassword() != null && !clienteActualizado.getPassword().isEmpty()) {
            clienteDb.setPassword(clienteActualizado.getPassword());
        }

        clienteRepository.save(clienteDb);
        
        // Actualizar sesión
        session.setAttribute("clienteLogueado", clienteDb);
        
        model.addAttribute("cliente", clienteDb);
        model.addAttribute("mensaje", "Perfil actualizado correctamente");
        return "cliente/perfil";
    }
}
