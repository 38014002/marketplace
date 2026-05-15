# Marketplace Microservices

## Descripción

Marketplace desarrollado con arquitectura de microservicios utilizando Spring Boot.

La plataforma permite gestionar usuarios, autenticación, productos, inventario, carrito de compras, órdenes y pagos mediante comunicación entre microservicios usando WebClient.

## Integrantes
- Daniel Palma
- Juan Mendoza

## Microservicios

- ms-auth
- ms-user
- ms-product
- ms-catalog
- ms-inventory
- ms-search
- ms-cart
- ms-order
- ms-payment
- ms-notification

## Arquitectura

Cada microservicio implementa el patrón CSR:

- Controller → manejo de endpoints REST
- Service → lógica de negocio
- Repository → acceso a datos

La comunicación entre microservicios se realiza mediante WebClient.

## Tecnologías

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- WebClient
- JWT
- Maven
- Lombok

## Funcionalidades

- CRUD de productos
- Gestión de inventario
- Autenticación JWT
- Gestión de usuarios
- Carrito de compras
- Checkout de órdenes
- Procesamiento de pagos
- Comunicación entre microservicios
- Validaciones
- Manejo de excepciones
- Logs estructurados

## Flujo principal

1. Usuario inicia sesión
2. Busca productos
3. Agrega productos al carrito
4. Order Service obtiene productos desde Cart Service
5. Order Service procesa pago mediante Payment Service
6. Se confirma la orden

## Ejecución

1. Clonar el repositorio
2. Abrir Laragon e iniciar MySQL
3. Crear las bases de datos necesarias para cada microservicio
4. Configurar las credenciales de conexión en application.properties
5. Ejecutar las migraciones automáticas
6. Levantar los microservicios Spring Boot
7. Probar los endpoints utilizando Postman

## Endpoints principales

### Cart
GET /api/cart/user/{userId}

### Order
POST /api/orders/checkout/{userId}

### Payment
POST /api/pagos/process/{orderId}

## Manejo de errores

Se implementó manejo centralizado de excepciones utilizando:

- ResponseEntity
- códigos HTTP
- ControllerAdvice
- validaciones con Bean Validation

## Logs

Se implementaron logs estructurados utilizando SLF4J para trazabilidad entre capas y microservicios.