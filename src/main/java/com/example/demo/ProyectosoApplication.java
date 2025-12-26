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
}
