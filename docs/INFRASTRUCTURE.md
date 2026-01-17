# Infrastructure Documentation

This document describes the technical setup of the SmartHealth Platform.

## Containers (Docker Compose)

| Service | Image | Internal Port | External Port | Memory Limit |
|---------|-------|---------------|---------------|--------------|
| `sh-postgres` | `postgres:16-alpine` | 5432 | 5432 | 300MB |
| `sh-rabbitmq` | `rabbitmq:3.13-management` | 5672, 15672 | 5672, 15672 | 400MB |
| `sh-redis` | `redis:7.2-alpine` | 6379 | 6379 | 100MB |
| `sh-keycloak` | `keycloak:24.0.0` | 8080 | 8180 | 700MB |
| `sh-gateway` | `smarthealth-gateway:latest` | 8080 | 8080 | 256MB |
| `sh-patient` | `smarthealth-patient:latest` | 8081 | 8081 | 256MB |
| `sh-appointment`| `smarthealth-appointment:latest`| 8082 | 8082 | 256MB |
| `sh-billing` | `smarthealth-billing:latest` | 8083 | 8083 | 256MB |
| `sh-notification`| `smarthealth-notification:latest`| 8084 | 8084 | 256MB |
| `sh-device` | `smarthealth-device:latest` | 8085 | 8085 | 256MB |
| `sh-analytics` | `smarthealth-analytics:latest` | 8086 | 8086 | 256MB |

## Databases
All databases are hosted on the `sh-postgres` instance:
- `patient_db`
- `appointment_db`
- `billing_db`
- `device_db`
- `analytics_db`

## Security (OIDC)
Keycloak realm `smarthealth` manages users and clients.
- **Client**: `api-gateway`
- **Secret**: Managed via `application.yml`
- **Flow**: Authorization Code Flow for UI, Bearer JWT for API.

## JVM Tuning
All Spring Boot applications are configured with:
`-Xmx192m -Xms128m`
This ensures they operate reliably within the 256MB Docker memory limit.
