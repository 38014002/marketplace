# Microservicios — guía rápida

Carpeta con los 12 microservicios del marketplace + Eureka + Gateway.

## Inicio rápido con Docker

```bash
copy .env.example .env
docker compose up --build
```

## Tests por servicio

Pruebas en **service**, **controller** y **repository** (Mockito en las dos primeras; H2 real en repository).

```bash
cd ms-user
./mvnw verify -Dtest="*ServiceTest,*ControllerTest,*RepositoryTest"
```

Ver cobertura JaCoCo (≥ 80% en `*Service`, `*Controller`, `*Repository`): `target/site/jacoco/index.html`

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
