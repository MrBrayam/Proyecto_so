package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        assertNotNull(dataSource, "DataSource should be configured");
        
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(5), "Connection should be valid");
            
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            String databaseProductVersion = connection.getMetaData().getDatabaseProductVersion();
            String url = connection.getMetaData().getURL();
            
            System.out.println("✓ Conexión exitosa a la base de datos");
            System.out.println("  - Tipo: " + databaseProductName);
            System.out.println("  - Versión: " + databaseProductVersion);
            System.out.println("  - URL: " + url);
            
        } catch (SQLException e) {
            fail("No se pudo conectar a la base de datos: " + e.getMessage());
        }
    }

    @Test
    public void testQueryExecution() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            assertEquals(1, result, "Query should return 1");
            System.out.println("✓ Consulta de prueba ejecutada exitosamente");
        } catch (Exception e) {
            fail("No se pudo ejecutar la consulta de prueba: " + e.getMessage());
        }
    }

    @Test
    public void testDatabaseSchema() {
        try {
            // Verificar que podemos acceder al esquema de la base de datos
            String query = "SELECT DATABASE() as db";
            String database = jdbcTemplate.queryForObject(query, String.class);
            assertNotNull(database, "Database name should not be null");
            System.out.println("✓ Base de datos actual: " + database);
        } catch (Exception e) {
            fail("No se pudo obtener el nombre de la base de datos: " + e.getMessage());
        }
    }
}
