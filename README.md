# SmartHealth Platform — Event‑Driven Microservices for Healthcare

## Project Structure
The project consists of several microservices, each in its own directory:
*   `api-gateway` (Port: 8080)
*   `patient` (Port: 8081)
*   `appointment` (Port: 8082)
*   `billing` (Port: 8083)
*   `notification` (Port: 8084)
*   `device` (Port: 8085)
*   `analytics` (Port: 8086)

## Development & Operations

### Working with Individual Services
Commands should be executed from the specific service directory (e.g., `cd patient/`).

#### 1. Build (JAR)
```bash
mvn -q -DskipTests clean package
```

#### 2. Run Locally
```bash
java -jar target/<service-name>-0.0.1-SNAPSHOT.jar
# Example check:
curl http://localhost:<port>/<service-name>/health
```

#### 3. Build Docker Image
```bash
docker build -t <service-name>:local .
```

#### 4. Deploy to Kubernetes (K3s)
Deploy using Helm charts located in the `helm/` directory of each service.
```bash
helm upgrade --install <service-name> helm/ \
  --namespace smarthealth --create-namespace \
  --set image.repository=<service-name> --set image.tag=local
```

### API Gateway
The API Gateway runs on port **8080**.
*   **Local Dev:** You can adjust `application.yml` to point to `localhost:<port>` instead of K8s DNS names if running services outside K8s.
*   **OIDC:** To enable security, uncomment the issuer line in `application.yml` and configure Keycloak.

### Common Features
*   **Health Checks:** `GET /<service>/health`
*   **Metrics:** `GET /actuator/prometheus`
