# Sistema de Gestión de Biblioteca

Sistema integral de gestión de biblioteca desarrollado con Spring Boot que permite administrar libros, clientes, préstamos, ventas y finanzas.

## 📋 Descripción

Sistema web completo para la gestión de una biblioteca que incluye:
- **Tienda virtual** para que los clientes puedan comprar o solicitar libros en préstamo
- **Panel de administración** para gestionar inventario, usuarios, finanzas y operaciones
- **Sistema de autenticación** con roles diferenciados (ADMIN, BIBLIOTECARIO, USER)
- **Gestión financiera** con control de ingresos, gastos y reportes
- **Control de inventario** con alertas de stock

## ✨ Características Principales

### Para Clientes
- Navegación de catálogo de libros
- Compra de libros
- Solicitud de préstamos
- Visualización de historial de pedidos
- Gestión de perfil personal
- Generación de facturas

### Para Administradores
- Dashboard con métricas en tiempo real
- Gestión de libros (CRUD completo)
- Administración de clientes
- Control de usuarios del sistema
- Gestión de pedidos pendientes
- Control de préstamos activos
- Módulo de finanzas con reportes
- Gestión de proveedores
- Registro de compras a proveedores
- Indicadores de stock con alertas

## 🛠️ Tecnologías Utilizadas

- **Backend:** Spring Boot 4.0.1
- **Java:** JDK 21
- **Base de Datos:** MySQL 8.0
- **ORM:** Hibernate/JPA
- **Template Engine:** Thymeleaf
- **Build Tool:** Maven
- **Frontend:** HTML5, CSS3, JavaScript
- **Arquitectura:** MVC (Model-View-Controller)

## 📦 Requisitos Previos

- JDK 21 o superior
- Maven 3.6+
- MySQL 8.0+
- Git

## 🚀 Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/tu-usuario/proyecto-biblioteca.git
cd proyecto-biblioteca
```

2. **Configurar la base de datos**

Crear una base de datos en MySQL:
```sql
CREATE DATABASE Libreria;
```

3. **Configurar credenciales**

Copiar el archivo de ejemplo y configurar tus credenciales:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Editar `application.properties` con tus datos:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/Libreria?useSSL=false&serverTimezone=UTC
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

4. **Compilar el proyecto**
```bash
mvn clean install
```

5. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📊 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/          # Configuraciones
│   │   ├── controller/      # Controladores MVC
│   │   ├── entity/          # Entidades JPA
│   │   ├── repository/      # Repositorios
│   │   └── DemoApplication.java
│   └── resources/
│       ├── static/
│       │   ├── css/         # Hojas de estilo
│       │   └── uploads/     # Imágenes subidas
│       ├── templates/       # Plantillas Thymeleaf
│       │   ├── admin/       # Vistas de administración
│       │   ├── cliente/     # Vistas de cliente
│       │   └── tienda/      # Vistas de tienda
│       └── application.properties
```

## 💾 Base de Datos

### Esquema Principal

- **libros:** Catálogo de libros con precios y stock
- **clientes:** Información de clientes registrados
- **usuarios:** Usuarios del sistema (admin/bibliotecario)
- **pedidos:** Registro de compras y préstamos
- **proveedores:** Proveedores de libros
- **compras:** Compras realizadas a proveedores
- **ingresos:** Registro de ingresos por ventas/préstamos

### Scripts de Datos de Prueba

Ejecutar los scripts SQL incluidos para poblar la base de datos:
```bash
mysql -u root -p Libreria < insert_clientes.sql
```

## 🎯 Uso

### Acceso al Sistema

**Panel de Administración:**
- URL: `http://localhost:8080/admin/login`
- Usuario por defecto: Crear usuario ADMIN mediante SQL

**Tienda (Clientes):**
- URL: `http://localhost:8080/`
- Los clientes pueden registrarse desde `/cliente/registro`

### Roles y Permisos

- **ADMIN:** Acceso completo a todas las funcionalidades
- **BIBLIOTECARIO:** Gestión de préstamos y pedidos
- **USER/CLIENTE:** Acceso a la tienda y sus pedidos

## 📈 Características Destacadas

### Sistema de Stock Inteligente
- Indicadores visuales de stock (alto/medio/bajo/agotado)
- Alertas automáticas cuando el stock es bajo

### Panel Financiero
- Dashboard con balance en tiempo real
- Filtros por períodos (hoy, semana, mes, personalizado)
- Visualización de ingresos por tipo (compras/préstamos)
- Control de gastos a proveedores

### Diseño Responsive
- Todas las vistas optimizadas para móviles
- Tablas que se convierten en cards en dispositivos pequeños
- Interfaz moderna con gradientes y animaciones

## 🔒 Seguridad

- Autenticación por sesión
- Validación de roles en cada endpoint
- Protección de rutas administrativas
- Credenciales no expuestas en el repositorio

## 📝 Licencia

Este proyecto está bajo la Licencia GPL 3.0 - ver el archivo [LICENSE](LICENSE) para más detalles.

## 👥 Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/NuevaCaracteristica`)
3. Commit tus cambios (`git commit -m 'Añadir nueva característica'`)
4. Push a la rama (`git push origin feature/NuevaCaracteristica`)
5. Abre un Pull Request

## 📧 Contacto

Para preguntas o sugerencias, puedes contactar al equipo de desarrollo.

## 🙏 Agradecimientos

- Spring Boot community
- Thymeleaf
- Bootstrap icons
- Todos los contribuidores del proyecto

---

**Nota:** Este es un proyecto educativo para la gestión de bibliotecas.
