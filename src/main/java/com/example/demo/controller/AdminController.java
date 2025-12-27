package com.example.demo.controller;

import com.example.demo.entity.Libro;
import com.example.demo.entity.Cliente;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.Pedido;
import com.example.demo.entity.Ingreso;
import com.example.demo.entity.Factura;
import com.example.demo.entity.Proveedor;
import com.example.demo.entity.Compra;
import com.example.demo.entity.DetalleCompra;
import com.example.demo.repository.LibroRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.IngresoRepository;
import com.example.demo.repository.FacturaRepository;
import com.example.demo.repository.ProveedorRepository;
import com.example.demo.repository.CompraRepository;
import com.example.demo.repository.DetalleCompraRepository;
import jakarta.servlet.http.HttpSession;
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
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private IngresoRepository ingresoRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private DetalleCompraRepository detalleCompraRepository;

    // Login de administrador
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/admin";
        }
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                       HttpSession session, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        
        if (usuario != null && usuario.getPassword() != null && usuario.getPassword().equals(password)) {
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/admin";
        } else {
            model.addAttribute("error", "Email o contraseña incorrectos");
            return "admin/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("usuarioLogueado");
        return "redirect:/admin/login";
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/admin/login";
        }
        
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

    // Finanzas
    @GetMapping("/finanzas")
    public String verFinanzas(@RequestParam(required = false) String filtro,
                              @RequestParam(required = false) String fechaInicio,
                              @RequestParam(required = false) String fechaFin,
                              Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/admin/login";
        }
        
        LocalDateTime inicio = null;
        LocalDateTime fin = LocalDateTime.now();
        
        // Determinar el rango de fechas según el filtro
        if (filtro != null) {
            switch (filtro) {
                case "hoy":
                    inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
                    break;
                case "semana":
                    inicio = LocalDateTime.now().minusDays(7);
                    break;
                case "mes":
                    inicio = LocalDateTime.now().minusMonths(1);
                    break;
                case "personalizado":
                    if (fechaInicio != null && !fechaInicio.isEmpty()) {
                        inicio = LocalDateTime.parse(fechaInicio + "T00:00:00");
                    }
                    if (fechaFin != null && !fechaFin.isEmpty()) {
                        fin = LocalDateTime.parse(fechaFin + "T23:59:59");
                    }
                    break;
            }
        }
        
        // Calcular ingresos
        Double totalIngresos;
        Double ingresosCompras;
        Double ingresosPrestamos;
        List<Ingreso> ingresos;
        
        if (inicio != null) {
            totalIngresos = ingresoRepository.calcularIngresoTotalPorFecha(inicio, fin);
            ingresosCompras = ingresoRepository.calcularIngresoPorTipoYFecha("COMPRA", inicio, fin);
            ingresosPrestamos = ingresoRepository.calcularIngresoPorTipoYFecha("PRESTAMO", inicio, fin);
            ingresos = ingresoRepository.findByFechaBetween(inicio, fin);
        } else {
            totalIngresos = ingresoRepository.calcularIngresoTotal();
            ingresosCompras = ingresoRepository.calcularIngresoPorTipo("COMPRA");
            ingresosPrestamos = ingresoRepository.calcularIngresoPorTipo("PRESTAMO");
            ingresos = ingresoRepository.findAll();
        }
        
        // Calcular gastos (compras a proveedores)
        Double totalGastos;
        List<Compra> compras;
        
        if (inicio != null) {
            totalGastos = compraRepository.calcularGastoTotalPorFecha(inicio, fin);
            compras = compraRepository.findByFechaCompraBetween(inicio, fin);
        } else {
            totalGastos = compraRepository.calcularGastoTotal();
            compras = compraRepository.findAll();
        }
        
        // Calcular balance
        Double balance = (totalIngresos != null ? totalIngresos : 0.0) - (totalGastos != null ? totalGastos : 0.0);
        
        model.addAttribute("totalIngresos", totalIngresos != null ? totalIngresos : 0.0);
        model.addAttribute("ingresosCompras", ingresosCompras != null ? ingresosCompras : 0.0);
        model.addAttribute("ingresosPrestamos", ingresosPrestamos != null ? ingresosPrestamos : 0.0);
        model.addAttribute("totalGastos", totalGastos != null ? totalGastos : 0.0);
        model.addAttribute("balance", balance);
        model.addAttribute("ingresos", ingresos);
        model.addAttribute("compras", compras);
        model.addAttribute("filtroActual", filtro != null ? filtro : "todo");
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        
        return "admin/finanzas";
    }
    
    // Mantener compatibilidad con la ruta anterior
    @GetMapping("/ingresos")
    public String listarIngresos(HttpSession session) {
        return "redirect:/admin/finanzas";
    }

    // Pedidos Pendientes
    @GetMapping("/pedidos-pendientes")
    public String listarPedidosPendientes(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("pedidosPendientes", pedidoRepository.findByEstado("PENDIENTE"));
        return "admin/pedidos-pendientes";
    }

    @PostMapping("/pedidos/{id}/confirmar")
    public String confirmarPedido(@PathVariable Long id, @RequestParam String tipoDocumento,
                                  HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/admin/login";
        }

        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null || !pedido.getEstado().equals("PENDIENTE")) {
            return "redirect:/admin/pedidos-pendientes";
        }

        // Verificar que si es factura, el cliente tenga RUC
        if (tipoDocumento.equals("FACTURA") && 
            (pedido.getCliente().getRuc() == null || pedido.getCliente().getRuc().isEmpty())) {
            tipoDocumento = "BOLETA"; // Forzar boleta si no tiene RUC
        }

        try {
            // Asignar usuario que atendió
            pedido.setUsuario(usuario);
            pedido.setTipoDocumento(tipoDocumento); // Actualizar tipo de documento
            
            // Cambiar estado según tipo
            if (pedido.getTipo().equals("PRESTAMO")) {
                pedido.setEstado("ACTIVO");
            } else {
                pedido.setEstado("COMPLETADO");
            }

            // Reducir stock
            Libro libro = pedido.getLibro();
            if (libro != null && libro.getStock() > 0) {
                libro.setStock(libro.getStock() - 1);
                libroRepository.save(libro);
            }
            pedidoRepository.save(pedido);

            // Verificar que el precio no sea null
            Double precio = pedido.getPrecio();
            if (precio == null) {
                precio = 0.0;
            }

            // Registrar ingreso
            Ingreso ingreso = new Ingreso();
            ingreso.setPedido(pedido);
            ingreso.setMonto(precio);
            ingreso.setTipo(pedido.getTipo());
            ingreso.setDescripcion(pedido.getTipo().equals("PRESTAMO") ? 
                "Préstamo de libro: " + (libro != null ? libro.getTitulo() : "N/A") : 
                "Compra de libro: " + (libro != null ? libro.getTitulo() : "N/A"));
            ingreso.setFecha(LocalDateTime.now());
            ingresoRepository.save(ingreso);

            // Generar número de factura correlativo
            String numeroFactura = generarNumeroFactura();

            // Generar factura/boleta
            Factura factura = new Factura();
            factura.setPedido(pedido);
            factura.setUsuario(usuario);
            factura.setTipoDocumento(tipoDocumento);
            factura.setNumeroFactura(numeroFactura);
            factura.setSubtotal(precio);
            factura.setImpuesto(precio * 0.19);
            factura.setTotal(precio + (precio * 0.19));
            Factura facturaGuardada = facturaRepository.save(factura);

            // Redirigir a la factura
            return "redirect:/admin/factura/" + facturaGuardada.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/pedidos-pendientes?error=" + e.getMessage();
        }
    }

    @GetMapping("/factura/{id}")
    public String verFactura(@PathVariable Long id, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/admin/login";
        }

        Factura factura = facturaRepository.findById(id).orElse(null);
        if (factura == null) {
            return "redirect:/admin/pedidos-pendientes";
        }

        // Cargar explícitamente las relaciones para evitar LazyInitializationException
        if (factura.getPedido() != null) {
            factura.getPedido().getCliente().getNombre(); // Forzar carga
            factura.getPedido().getLibro().getTitulo(); // Forzar carga
        }
        
        // Cargar usuario (bibliotecario)
        if (factura.getUsuario() != null) {
            factura.getUsuario().getUsername(); // Forzar carga
        }

        model.addAttribute("factura", factura);
        return "admin/factura";
    }
    
    private String generarNumeroFactura() {
        Optional<Factura> ultimaFactura = facturaRepository.findFirstByOrderByIdDesc();
        long numeroCorrelativo = 1;
        
        if (ultimaFactura.isPresent() && ultimaFactura.get().getNumeroFactura() != null) {
            String ultimoNumero = ultimaFactura.get().getNumeroFactura();
            // Extraer el número después de "FAC-" (asumiendo formato FAC-000000001)
            try {
                if (ultimoNumero.startsWith("FAC-")) {
                    numeroCorrelativo = Long.parseLong(ultimoNumero.substring(4)) + 1;
                } else {
                    numeroCorrelativo = Long.parseLong(ultimoNumero) + 1;
                }
            } catch (NumberFormatException e) {
                // Si hay error al parsear, empezar desde 1
                numeroCorrelativo = 1;
            }
        }
        
        // Formatear con prefijo FAC- y 9 dígitos: FAC-000000001
        return "FAC-" + String.format("%09d", numeroCorrelativo);
    }
    
    // Proveedores
    @GetMapping("/proveedores")
    public String listarProveedores(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "admin/proveedores";
    }

    @GetMapping("/proveedores/nuevo")
    public String nuevoProveedor(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("proveedor", new Proveedor());
        return "admin/proveedor-form";
    }

    @PostMapping("/proveedores/guardar")
    public String guardarProveedor(@ModelAttribute Proveedor proveedor, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/admin/login";
        }
        
        proveedorRepository.save(proveedor);
        return "redirect:/admin/proveedores";
    }

    @GetMapping("/proveedores/editar/{id}")
    public String editarProveedor(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/admin/login";
        }
        
        Proveedor proveedor = proveedorRepository.findById(id).orElse(null);
        model.addAttribute("proveedor", proveedor);
        return "admin/proveedor-form";
    }

    @GetMapping("/proveedores/eliminar/{id}")
    public String eliminarProveedor(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/admin/login";
        }
        
        proveedorRepository.deleteById(id);
        return "redirect:/admin/proveedores";
    }
    
    // Compras
    @GetMapping("/compras")
    public String listarCompras(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("compras", compraRepository.findAll());
        return "admin/compras";
    }

    @GetMapping("/compras/nueva")
    public String nuevaCompra(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("compra", new Compra());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        model.addAttribute("libros", libroRepository.findAll());
        return "admin/compra-form";
    }

    @GetMapping("/compras/ver/{id}")
    public String verCompra(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/admin/login";
        }
        
        Compra compra = compraRepository.findById(id).orElse(null);
        if (compra == null) {
            return "redirect:/admin/compras";
        }
        
        // Cargar relaciones
        if (compra.getProveedor() != null) {
            compra.getProveedor().getNombre();
        }
        if (compra.getUsuario() != null) {
            compra.getUsuario().getUsername();
        }
        
        model.addAttribute("compra", compra);
        model.addAttribute("detalles", detalleCompraRepository.findByCompra(compra));
        return "admin/compra-detalle";
    }

    @PostMapping("/compras/guardar")
    @ResponseBody
    public String guardarCompra(@RequestBody CompraRequest request, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "Error: No autorizado";
        }

        try {
            // Crear compra
            Compra compra = new Compra();
            compra.setProveedor(proveedorRepository.findById(request.getProveedorId()).orElse(null));
            compra.setUsuario(usuario);
            compra.setObservaciones(request.getObservaciones());
            compra.setSubtotal(request.getSubtotal());
            compra.setImpuesto(request.getImpuesto());
            compra.setTotal(request.getTotal());
            compra.setNumeroFactura(generarNumeroCompra());
            
            Compra compraGuardada = compraRepository.save(compra);

            // Crear detalles y actualizar stock
            for (CompraDetalleRequest detalle : request.getDetalles()) {
                DetalleCompra detalleCompra = new DetalleCompra();
                detalleCompra.setCompra(compraGuardada);
                
                Libro libro = libroRepository.findById(detalle.getLibroId()).orElse(null);
                if (libro != null) {
                    detalleCompra.setLibro(libro);
                    detalleCompra.setCantidad(detalle.getCantidad());
                    detalleCompra.setPrecioUnitario(detalle.getPrecioUnitario());
                    
                    detalleCompraRepository.save(detalleCompra);
                    
                    // Actualizar stock del libro
                    libro.setStock(libro.getStock() + detalle.getCantidad());
                    libroRepository.save(libro);
                }
            }

            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private String generarNumeroCompra() {
        Optional<Compra> ultimaCompra = compraRepository.findFirstByOrderByIdDesc();
        long numeroCorrelativo = 1;
        
        if (ultimaCompra.isPresent() && ultimaCompra.get().getNumeroFactura() != null) {
            String ultimoNumero = ultimaCompra.get().getNumeroFactura();
            try {
                if (ultimoNumero.startsWith("COMP-")) {
                    numeroCorrelativo = Long.parseLong(ultimoNumero.substring(5)) + 1;
                } else {
                    numeroCorrelativo = Long.parseLong(ultimoNumero) + 1;
                }
            } catch (NumberFormatException e) {
                numeroCorrelativo = 1;
            }
        }
        
        return "COMP-" + String.format("%09d", numeroCorrelativo);
    }

    // Clases auxiliares para recibir datos JSON
    static class CompraRequest {
        private Long proveedorId;
        private String observaciones;
        private Double subtotal;
        private Double impuesto;
        private Double total;
        private java.util.List<CompraDetalleRequest> detalles;

        public Long getProveedorId() { return proveedorId; }
        public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }
        
        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
        
        public Double getSubtotal() { return subtotal; }
        public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
        
        public Double getImpuesto() { return impuesto; }
        public void setImpuesto(Double impuesto) { this.impuesto = impuesto; }
        
        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
        
        public java.util.List<CompraDetalleRequest> getDetalles() { return detalles; }
        public void setDetalles(java.util.List<CompraDetalleRequest> detalles) { this.detalles = detalles; }
    }

    static class CompraDetalleRequest {
        private Long libroId;
        private Integer cantidad;
        private Double precioUnitario;

        public Long getLibroId() { return libroId; }
        public void setLibroId(Long libroId) { this.libroId = libroId; }
        
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        
        public Double getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
    }
}

