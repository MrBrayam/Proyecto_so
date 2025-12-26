package com.example.demo.controller;

import com.example.demo.entity.Libro;
import com.example.demo.entity.Cliente;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.Pedido;
import com.example.demo.repository.LibroRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalLibros", libroRepository.count());
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        return "admin/dashboard";
    }

    // Libros
    @GetMapping("/libros")
    public String listarLibros(Model model) {
        model.addAttribute("libros", libroRepository.findAll());
        return "admin/libros";
    }

    @GetMapping("/libros/nuevo")
    public String nuevoLibro(Model model) {
        model.addAttribute("libro", new Libro());
        return "admin/libro-form";
    }

    @PostMapping("/libros/guardar")
    public String guardarLibro(@ModelAttribute Libro libro, @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        // Si se subió una imagen, guardarla localmente
        if (imagen != null && !imagen.isEmpty()) {
            try {
                // Crear directorio si no existe
                String uploadDir = "src/main/resources/static/uploads/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Generar nombre único para la imagen
                String fileName = UUID.randomUUID().toString() + "_" + imagen.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                
                // Guardar archivo
                Files.copy(imagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                // Establecer la URL de la imagen
                libro.setUrlImg("/uploads/" + fileName);
                
                System.out.println("Imagen guardada: /uploads/" + fileName);
            } catch (IOException e) {
                System.err.println("Error al guardar imagen: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No se seleccionó imagen o urlImg ya tiene valor: " + libro.getUrlImg());
        }
        
        libroRepository.save(libro);
        return "redirect:/admin/libros";
    }

    @GetMapping("/libros/editar/{id}")
    public String editarLibro(@PathVariable Long id, Model model) {
        Libro libro = libroRepository.findById(id).orElse(null);
        model.addAttribute("libro", libro);
        return "admin/libro-form";
    }

    @GetMapping("/libros/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id) {
        libroRepository.deleteById(id);
        return "redirect:/admin/libros";
    }

    // Clientes
    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "admin/clientes";
    }

    @GetMapping("/clientes/nuevo")
    public String nuevoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "admin/cliente-form";
    }

    @PostMapping("/clientes/guardar")
    public String guardarCliente(@ModelAttribute Cliente cliente) {
        clienteRepository.save(cliente);
        return "redirect:/admin/clientes";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        model.addAttribute("cliente", cliente);
        return "admin/cliente-form";
    }

    @GetMapping("/clientes/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        clienteRepository.deleteById(id);
        return "redirect:/admin/clientes";
    }

    // Usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin/usuario-form";
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuarioRepository.save(usuario);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        model.addAttribute("usuario", usuario);
        return "admin/usuario-form";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/admin/usuarios";
    }

    // Préstamos
    @GetMapping("/prestamos")
    public String listarPrestamos(Model model) {
        model.addAttribute("prestamos", pedidoRepository.findByTipoAndEstado("PRESTAMO", "ACTIVO"));
        return "admin/prestamos";
    }

    @PostMapping("/prestamos/devolver/{id}")
    public String devolverLibro(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null && pedido.getTipo().equals("PRESTAMO") && pedido.getEstado().equals("ACTIVO")) {
            pedido.setEstado("DEVUELTO");
            pedido.setFechaDevolucion(LocalDateTime.now());
            
            // Incrementar stock
            Libro libro = pedido.getLibro();
            libro.setStock(libro.getStock() + 1);
            libroRepository.save(libro);
            
            pedidoRepository.save(pedido);
        }
        return "redirect:/admin/prestamos";
    }
}
