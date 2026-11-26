# Cineva · Frontend

Cineva es el panel de administración de un catálogo de películas y actores. Este proyecto contiene el microservicio de presentación del trabajo inicial de **Frameworks Backend y Microservicios** de la Universidad de Alcalá.

## Funciones

- **Películas.** Alta, edición, baja con confirmación, listado y consulta de fichas con portada, sinopsis y reparto. Búsqueda por título, género y actor.
- **Actores.** Alta, edición, baja con confirmación y listado. Búsqueda por nombre y país de nacimiento. Ordenación por nombre, fecha o país de nacimiento.
- **Catálogo.** Paginación de 10 registros en ambos listados y formularios con validación. El reparto de una película se selecciona desde el directorio de actores.

## Requisitos y arranque

Necesitas un **JDK 17 o posterior** y el proyecto **Películas Backend** en ejecución. Para preparar MySQL y arrancar el backend, sigue su README. El script de creación de la base de datos está en `database/schema.sql` dentro de ese proyecto.

1. Arranca el backend. Su API usa el puerto `8080` de forma predeterminada. Puedes comprobarla en [localhost:8080/api/actores](http://localhost:8080/api/actores).
2. Desde la raíz de este repositorio, arranca el frontend con el Maven Wrapper.

   ```bash
   ./mvnw spring-boot:run
   ```

   En Windows, usa `mvnw.cmd spring-boot:run`.
3. Abre [Películas](http://localhost:8081/peliculas) o [Actores](http://localhost:8081/actores).

### Configuración

| Variable de entorno | Valor predeterminado | Uso |
| --- | --- | --- |
| `FRONTEND_PORT` | `8081` | Puerto de Cineva |
| `CATALOGO_API_URL` | `http://localhost:8080` | Dirección base del backend, sin el sufijo `/api` |

Para ejecutar la prueba de arranque del frontend, usa `./mvnw test` o `mvnw.cmd test` en Windows.

## Arquitectura

El frontend usa Java 17, Spring Boot, Spring MVC y Thymeleaf. Los controladores gestionan las rutas y los formularios. `ActorCliente` y `PeliculaCliente` consumen la API REST mediante `RestClient`. El backend gestiona la persistencia con JPA y MySQL.

- `src/main/java/com/example/peliculas_frontend/actores/` contiene los controladores, formularios y el cliente REST de actores.
- `src/main/java/com/example/peliculas_frontend/peliculas/` contiene los equivalentes para películas.
- `src/main/resources/templates/` contiene las vistas Thymeleaf y el menú compartido.
- `src/main/resources/static/css/catalogo.css` contiene los estilos de Cineva.

## Autoría

**Yoselin Andrango**

Máster Universitario en Desarrollo Ágil de Software para la Web · Universidad de Alcalá

Frameworks Backend y Microservicios · Curso 2026–2027
