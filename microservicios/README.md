# Microservicios — guía rápida

Carpeta con los 12 microservicios del marketplace + Eureka + Gateway.

## Inicio rápido con Docker

```bash
copy .env.example .env
docker compose up --build
```

## Tests por servicio

```bash
cd ms-user
./mvnw test
```

Ver cobertura: `target/site/jacoco/index.html`

## Estructura

```
microservicios/
├── ms-eureka/       # 8761
├── ms-gateway/      # 8080
├── ms-user/         # 8081
├── ms-producto/     # 8082
├── ms-order/        # 8083
├── ms-inventory/    # 8084
├── ms-catalog/      # 8085
├── ms-cart/         # 8086
├── ms-auth/         # 8087
├── ms-payment/      # 8088
├── ms-search/       # 8089
├── ms-notification/ # 8090
├── Dockerfile
└── docker-compose.yml
```

Documentación completa en el [README principal](../README.md).
