# Marketplace Microservices

## Descripción

Marketplace desarrollado con arquitectura de microservicios utilizando Spring Boot.

La plataforma permite gestionar usuarios, autenticación, productos, inventario, carrito de compras, órdenes, pagos y búsqueda mediante comunicación REST entre microservicios (WebClient) y un API Gateway centralizado.

## Integrantes

- Daniel Palma
- Juan Mendoza

## Microservicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| ms-eureka | 8761 | Service discovery |
| ms-gateway | 8080 | API Gateway (punto de entrada) |
| ms-user | 8081 | Usuarios y login JWT |
| ms-producto | 8082 | Fachada de productos (catalog + inventory) |
| ms-order | 8083 | Órdenes y checkout |
| ms-inventory | 8084 | Stock |
| ms-catalog | 8085 | Catálogo de productos |
| ms-cart | 8086 | Carrito de compras |
| ms-auth | 8087 | Autenticación alternativa (register/login/refresh) |
| ms-payment | 8088 | Pagos |
| ms-search | 8089 | Búsqueda de productos |
| ms-notification | 8090 | Notificaciones |

## Arquitectura

Cada microservicio implementa el patrón **CSR**:

- **Controller** → endpoints REST
- **Service** → lógica de negocio
- **Repository** → acceso a datos

Comunicación entre microservicios: **WebClient**. Registro de servicios: **Eureka**. Enrutamiento unificado: **Spring Cloud Gateway**.

## Tecnologías

- Java 21
- Spring Boot 3.2
- Spring Cloud Gateway + Eureka
- Spring Data JPA + Hibernate
- MySQL
- Flyway (migraciones versionadas en todos los MS con BD)
- WebClient
- JWT
- Swagger / OpenAPI (springdoc)
- JUnit 5 + Mockito + JaCoCo
- Maven
- Docker / Docker Compose
- Render (despliegue remoto)

## Rutas del API Gateway (`http://localhost:8080`)

| Ruta gateway | Microservicio | Controller |
|--------------|---------------|------------|
| `/api/usuarios/**` | ms-user | `/api/usuarios` |
| `/api/productos/**` | ms-producto | `/api/productos` |
| `/api/orders/**` | ms-order | `/api/orders` |
| `/api/cart/**` | ms-cart | `/api/cart` |
| `/api/pagos/**` | ms-payment | `/api/pagos` |
| `/api/v1/catalog/**` | ms-catalog | `/api/v1/catalog` |
| `/api/v1/inventory/**` | ms-inventory | `/api/v1/inventory` |
| `/api/search/**` | ms-search | `/api/search` |
| `/api/notifications/**` | ms-notification | `/api/notifications` |
| `/auth/**` | ms-auth | `/auth` |

## Documentación Swagger (local)

| Servicio | URL |
|----------|-----|
| ms-user | http://localhost:8081/swagger-ui/index.html |
| ms-producto | http://localhost:8082/swagger-ui/index.html |
| ms-order | http://localhost:8083/swagger-ui/index.html |
| ms-inventory | http://localhost:8084/swagger-ui/index.html |
| ms-catalog | http://localhost:8085/swagger-ui/index.html |
| ms-cart | http://localhost:8086/swagger-ui/index.html |
| ms-auth | http://localhost:8087/swagger-ui/index.html |
| ms-payment | http://localhost:8088/swagger-ui/index.html |
| ms-search | http://localhost:8089/swagger-ui/index.html |
| ms-notification | http://localhost:8090/swagger-ui/index.html |

## Flujo principal

1. Login → `POST /api/usuarios/login` (o vía gateway `:8080`)
2. Listar productos → `GET /api/productos`
3. Buscar → `GET /api/search?query=mouse`
4. Agregar al carrito → `POST /api/cart`
5. Checkout → `POST /api/orders/checkout/{userId}`
6. Pago procesado por ms-payment → orden en estado `PAID`

## Ejecución local (IDE)

1. Clonar el repositorio e iniciar **MySQL** (XAMPP o Laragon).
2. Configurar `DB_PASSWORD` si aplica (por defecto vacío en XAMPP).
3. Levantar en este orden:
   - `ms-eureka` (8761)
   - microservicios de negocio
   - `ms-gateway` (8080)
4. Variables opcionales: `JWT_SECRET`, `DB_PASSWORD`.

```bash
cd microservicios/ms-eureka
./mvnw spring-boot:run
# Repetir en cada microservicio necesario
```

## Perfiles Spring (dev / prod)

Cada microservicio usa perfiles YAML `application-dev.yml` y `application-prod.yml` junto con `application.properties` (configuración base).

| Perfil | Cuándo | Características |
|--------|--------|-----------------|
| `dev` (default) | IDE local, XAMPP | SQL visible, logging DEBUG, esquema vía **Flyway** |
| `prod` | Docker Compose, Render | Eureka `prefer-ip-address`, SQL oculto, esquema vía **Flyway** |

Activar perfil:

```bash
# Local (default dev, no requiere variable)
./mvnw spring-boot:run

# Producción explícita
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

Variables clave por entorno:

| Variable | Local | Docker | Render |
|----------|-------|--------|--------|
| `SPRING_PROFILES_ACTIVE` | `dev` | `dev` | `prod` |
| `DB_HOST` | `localhost` | `mysql-<servicio>` (ej. `mysql-user`) | host MySQL externo |
| `EUREKA_HOST` | `localhost` | `ms-eureka` | vía `fromService` |
| `JWT_SECRET` | `.env` | `.env` | auto-generado |

## Ejecución con Docker

```bash
cd microservicios
copy .env.example .env
docker compose up --build
```

- Gateway: http://localhost:8080
- Eureka: http://localhost:8761
- Base de datos: una instancia MySQL dedicada por microservicio (`mysql-user`, `mysql-order`, etc.)
- Sin BD propia: `ms-producto` (fachada REST), `ms-gateway`, `ms-eureka`
- Perfil activo: `dev` (definido en `docker-compose.yml`)

## Despliegue remoto (Render)

**URL pública (tras deploy):** `https://marketplace-gateway.onrender.com`

El archivo [`render.yaml`](render.yaml) define el blueprint completo:

1. **MySQL externo** — Render no ofrece MySQL nativo. Crear instancia en [Railway](https://railway.app), [Aiven](https://aiven.io) u otro proveedor.
2. **Conectar GitHub** en [Render Dashboard](https://dashboard.render.com) → **New** → **Blueprint** → seleccionar el repo.
3. **Configurar variables** en el grupo `marketplace-shared`:
   - `DB_HOST` — hostname del MySQL externo
   - `DB_PASSWORD` — contraseña de la BD
4. Render despliega automáticamente:
   - `marketplace-gateway` (público, health check `/actuator/health`)
   - `marketplace-eureka` + 10 microservicios (red privada)
5. Verificar: `GET https://marketplace-gateway.onrender.com/actuator/health`

```bash
# Ejemplo de prueba tras deploy
curl https://marketplace-gateway.onrender.com/api/productos
curl -X POST https://marketplace-gateway.onrender.com/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"secret"}'
```

> **Nota:** El plan free de Render suspende servicios tras inactividad (~50 s de cold start). Los private services (`pserv`) requieren plan Starter o superior.

## Pruebas unitarias y cobertura

Cada microservicio de negocio incluye pruebas en **tres capas** con **JUnit 5 + Mockito** (estructura Given–When–Then) y validación **JaCoCo ≥ 80%** en clases `*Service`, `*Controller` y `*Repository`.

| Capa | Técnica | Mockito |
|------|---------|---------|
| **Service** | `@ExtendWith(MockitoExtension.class)` + `@Mock` / `@InjectMocks` | Sí — dependencias simuladas con `when()` / `verify()` |
| **Controller** | `@WebMvcTest` + `MockMvc` + `@MockBean` | Sí — `@MockBean` inyecta mocks Mockito del service (y `JwtUtil` si hay seguridad) |
| **Repository** | `@DataJpaTest` + H2 en memoria | No — prueba real de persistencia y queries Spring Data |

Los tests de repositorio usan `src/test/resources/application.properties` con H2 en memoria (no conectan a MySQL local).

```bash
cd microservicios/ms-user
./mvnw verify -Dtest="*ServiceTest,*ControllerTest,*RepositoryTest"
```

Reporte de cobertura: `target/site/jacoco/index.html`

## Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/usuarios/login` | Login JWT |
| GET | `/api/productos` | Listar productos |
| GET | `/api/search?query=` | Buscar productos |
| POST | `/api/cart` | Agregar al carrito |
| POST | `/api/orders/checkout/{userId}` | Checkout |
| POST | `/api/pagos/process/{orderId}` | Procesar pago |

## Manejo de errores

- `GlobalExceptionHandler` / `@ControllerAdvice`
- Códigos HTTP coherentes (400, 401, 403, 404, 500)
- Validaciones con Bean Validation (`@Valid`)

## Logs

Logs estructurados con SLF4J / Logback en cada microservicio (`logs/`).
