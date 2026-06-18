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
- Flyway (según microservicio)
- WebClient
- JWT
- Swagger / OpenAPI (springdoc)
- JUnit 5 + Mockito + JaCoCo
- Maven
- Docker / Docker Compose
- Render (despliegue remoto)

## Rutas del API Gateway (`http://localhost:8080`)

| Ruta gateway | Microservicio |
|--------------|---------------|
| `/api/usuarios/**` | ms-user |
| `/api/productos/**` | ms-producto |
| `/api/orders/**` | ms-order |
| `/api/cart/**` | ms-cart |
| `/api/payments/**` | ms-payment (rewrite → `/api/pagos`) |
| `/api/catalog/**` | ms-catalog (rewrite → `/api/v1/catalog`) |
| `/api/inventory/**` | ms-inventory (rewrite → `/api/v1/inventory`) |
| `/api/search/**` | ms-search |
| `/api/notifications/**` | ms-notification |
| `/auth/**` | ms-auth |

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

## Ejecución con Docker

```bash
cd microservicios
copy .env.example .env
docker compose up --build
```

- Gateway: http://localhost:8080
- Eureka: http://localhost:8761
- MySQL: puerto 3306

## Despliegue remoto (Render)

El archivo `render.yaml` en la raíz del proyecto define servicios Docker para despliegue en [Render](https://render.com):

1. Conectar el repositorio de GitHub.
2. Crear Web Services usando el `Dockerfile` en `microservicios/`.
3. Configurar variables: `JWT_SECRET`, `SPRING_DATASOURCE_URL`, `DB_PASSWORD`, `SERVICE_DIR`.

## Pruebas unitarias y cobertura

Cada microservicio de negocio incluye pruebas con **JUnit + Mockito** (estructura Given–When–Then) y validación **JaCoCo ≥ 80%** en clases `*Service`.

```bash
cd microservicios/ms-user
./mvnw test
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
