# SmartHealth Web UI

This is the Angular-based frontend for the SmartHealth Platform.

## Features
- **Dashboard**: Overview of system status and metrics.
- **Patient Management**: Register and list patients.
- **Appointment Scheduling**: Book appointments with healthcare providers.
- **OIDC Security**: Integrated with Keycloak for secure access.

## Tech Stack
- **Framework**: Angular 19+ (Standalone Components).
- **Styling**: Bootstrap 5 + Bootstrap Icons.
- **Security**: angular-oauth2-oidc.
- **API**: Communication via API Gateway (Port 8080).

## Local Development

1. Install dependencies:
   ```bash
   npm install
   ```
2. Start development server:
   ```bash
   npm start
   ```
3. Access at `http://localhost:4200`.

## Docker Deployment
The UI is served using Nginx.
```bash
docker compose up --build -d web-ui
```