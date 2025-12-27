package com.example.demo.config;

import com.example.demo.entity.Proveedor;
import com.example.demo.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Override
    public void run(String... args) throws Exception {
        // Insertar proveedores solo si no existen
        if (proveedorRepository.count() == 0) {
            Proveedor p1 = new Proveedor();
            p1.setNombre("Distribuidora de Libros S.A.");
            p1.setRuc("20123456789");
            p1.setEmail("ventas@distrilibros.com");
            p1.setTelefono("(01) 234-5678");
            p1.setDireccion("Av. Los Libertadores 456, Lima");
            p1.setNombreContacto("Carlos Mendoza");
            proveedorRepository.save(p1);

            Proveedor p2 = new Proveedor();
            p2.setNombre("Editorial Horizonte");
            p2.setRuc("20987654321");
            p2.setEmail("contacto@horizonte.com.pe");
            p2.setTelefono("(01) 876-5432");
            p2.setDireccion("Jr. Universitaria 234, San Miguel");
            p2.setNombreContacto("María Fernández");
            proveedorRepository.save(p2);

            Proveedor p3 = new Proveedor();
            p3.setNombre("Importadora Books International");
            p3.setRuc("20456789123");
            p3.setEmail("info@booksintl.com");
            p3.setTelefono("(01) 555-1234");
            p3.setDireccion("Av. Arequipa 1890, Lince");
            p3.setNombreContacto("Roberto Silva");
            proveedorRepository.save(p3);

            Proveedor p4 = new Proveedor();
            p4.setNombre("Librería Mayorista del Perú");
            p4.setRuc("20789123456");
            p4.setEmail("ventas@libmayorista.pe");
            p4.setTelefono("(01) 333-7890");
            p4.setDireccion("Av. Colonial 2345, Callao");
            p4.setNombreContacto("Ana Torres");
            proveedorRepository.save(p4);

            Proveedor p5 = new Proveedor();
            p5.setNombre("Grupo Editorial Andino");
            p5.setRuc("20654321987");
            p5.setEmail("compras@grupoandino.com");
            p5.setTelefono("(01) 444-2468");
            p5.setDireccion("Jr. Lampa 567, Cercado de Lima");
            p5.setNombreContacto("Luis Ramírez");
            proveedorRepository.save(p5);

            System.out.println("✅ Se insertaron 5 proveedores correctamente");
        }
    }
}
