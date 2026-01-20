# Infrastructure Documentation

This document describes the technical setup of the SmartHealth Platform.

## Containers (Docker Compose)

| Service | Image | Int Port | Ext Port | Memory Limit | JVM Xmx |
|---------|-------|----------|----------|--------------|---------|
| `sh-postgres` | `postgres:16-alpine` | 5432 | 5432 | 300MB | - |
| `sh-rabbitmq` | `rabbitmq:3.13-management` | 5672 | 5672 | 400MB | - |
| `sh-redis` | `redis:7.2-alpine` | 6379 | 6379 | 100MB | - |
| `sh-keycloak` | `keycloak:24.0.0` | 8080 | 8180 | 700MB | - |
| `sh-gateway` | `sh-api-gateway` | 8080 | 8080 | 512MB | 384m |
| `sh-patient` | `sh-patient-service` | 8081 | 8081 | 256MB | 192m |
| `sh-appointment`| `sh-appointment-service`| 8082 | 8082 | 256MB | 192m |
| `sh-billing` | `sh-billing-service` | 8083 | 8083 | 256MB | 192m |
| `sh-notification`| `sh-notification-service`| 8084 | 8084 | 300MB | 192m |
| `sh-device` | `sh-device-service` | 8085 | 8085 | 256MB | 192m |
| `sh-analytics` | `sh-analytics-service` | 8086 | 8086 | 256MB | 192m |
| `sh-web-ui` | `sh-web-ui` (Nginx) | 80 | 4200 | 128MB | - |
| `sh-prometheus`| `prom/prometheus` | 9090 | 9090 | 256MB | - |
| `sh-zipkin` | `openzipkin/zipkin` | 9411 | 9411 | 256MB | - |
| `sh-grafana` | `grafana/grafana` | 3000 | 3000 | 256MB | - |

## Databases
All databases are hosted on the `sh-postgres` instance:
- `patient_db`, `appointment_db`, `billing_db`, `device_db`, `analytics_db`

## Messaging (RabbitMQ)
Key Queues:
- `q.patient.created`: Consumed by internal services.
- `q.appointment.booked.notification`: Consumer for SMS/Email simulation.
- `q.medical.alerts.notification`: Real-time anomaly alerts for medical staff.
- `q.device.telemetry.analytics`: Data ingestion for historical records.

## Observability
- **Prometheus**: Scrapes `/actuator/prometheus` from all services every 15s.
- **Grafana**: Visualizes JVM metrics (Memory, CPU, GC, Threads). Pre-configured via provisioning.
- **Zipkin**: Collects and visualizes distributed traces (latency, error analysis).

## Security (OIDC)
Keycloak automatically imports the `smarthealth` realm from `infra/keycloak/realm-export.json` on startup.

## Testing & Simulation
- **Telemetry Simulator**: Use `./infra/simulator.sh` to send mock data to the `device-service` via the API Gateway. This is required to populate Redis and see data on the Dashboard.
- **Test Infrastructure**:
    - **H2 Database**: Used during Contract Tests to ensure CI independence.
    - **Testcontainers**: Used for full Integration Tests (PostgreSQL, RabbitMQ, Redis).
    - **Messaging Bridge**: Contract tests use a Spring Integration bridge to stub RabbitMQ interactions.
    - **Stub Runner**: Configured in `CLASSPATH` mode to allow multi-module verification without local Maven installation.