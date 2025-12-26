package com.example.demo;

import com.example.demo.entity.Libro;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.LibroRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProyectosoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectosoApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UsuarioRepository usuarioRepository, LibroRepository libroRepository) {
		return args -> {
			// Insertar 5 usuarios si no existen
			if (usuarioRepository.count() == 0) {
				for (int i = 1; i <= 5; i++) {
					Usuario usuario = new Usuario();
					usuario.setUsername("Usuario" + i);
					usuario.setEmail("usuario" + i + "@sistema.com");
					usuario.setPassword("password" + i);
					usuario.setRol("ADMIN");
					usuarioRepository.save(usuario);
				}
				System.out.println("5 usuarios insertados correctamente");
			}

			// Actualizar libros existentes con precioPrestamo si no lo tienen
			libroRepository.findAll().forEach(libro -> {
				if (libro.getPrecioPrestamo() == null || libro.getPrecioPrestamo() == 0) {
					libro.setPrecioPrestamo(libro.getPrecio() * 0.30);
					libroRepository.save(libro);
					System.out.println("Precio préstamo actualizado para: " + libro.getTitulo());
				}
			});

			// Insertar 10 libros si hay menos de 10
			if (libroRepository.count() < 10) {
				String[] titulos = {
					"Cien Años de Soledad", "Don Quijote de la Mancha", "1984",
					"El Principito", "Orgullo y Prejuicio", "Crimen y Castigo",
					"El Gran Gatsby", "Rayuela", "La Odisea", "El Alquimista"
				};
				String[] autores = {
					"Gabriel García Márquez", "Miguel de Cervantes", "George Orwell",
					"Antoine de Saint-Exupéry", "Jane Austen", "Fiódor Dostoyevski",
					"F. Scott Fitzgerald", "Julio Cortázar", "Homero", "Paulo Coelho"
				};
				Double[] precios = {25.99, 30.50, 18.75, 12.99, 22.00, 28.50, 20.00, 24.99, 35.00, 16.50};
				Integer[] stocks = {15, 10, 20, 25, 12, 8, 18, 14, 5, 30};

				for (int i = 0; i < 10; i++) {
					Libro libro = new Libro();
					libro.setTitulo(titulos[i]);
					libro.setAutor(autores[i]);
					libro.setIsbn("ISBN-" + (1000 + i));
					libro.setDescripcion("Una obra maestra de la literatura universal.");
					libro.setPrecio(precios[i]);
					libro.setPrecioPrestamo(precios[i] * 0.30); // 30% del precio
					libro.setStock(stocks[i]);
					libro.setUrlImg("/uploads/libro" + (i+1) + ".jpg");
					libroRepository.save(libro);
				}
				System.out.println("10 libros insertados correctamente");
			}
		};
	}
}
