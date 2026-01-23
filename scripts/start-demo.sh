#!/bin/bash
# Navigate to project root (one level up from scripts/)
cd "$(dirname "$0")/.."

set -e

# Colors for logs
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Starting SmartHealth Platform Demo Script ===${NC}"

# Function to check if a command exists
check_command() {
    if ! command -v "$1" &> /dev/null; then
        return 1
    fi
    return 0
}

# 1. Checking and installing Docker
echo -e "${YELLOW}[1/6] Checking Docker environment...${NC}"
if ! check_command "docker"; then
    echo -e "${RED}Docker is not installed.${NC}"
    read -p "Do you want to try installing docker.io now? (y/n): " choice
    if [[ "$choice" == "y" || "$choice" == "Y" ]]; then
        echo "Installing Docker..."
        sudo apt update && sudo apt install -y docker.io docker-compose-v2
        sudo usermod -aG docker $USER
        echo -e "${GREEN}Docker installed. NOTE: You may need to re-login to run without sudo. Attempting to continue...${NC}"
    else
        echo "Cancelled. Script requires Docker."
        exit 1
    fi
else
    echo -e "${GREEN}Docker is installed.${NC}"
fi

# 2. Checking if Docker service is running
if ! sudo systemctl is-active --quiet docker; then
    echo -e "${YELLOW}Docker service is disabled/stopped.${NC}"
    echo "Attempting to start Docker service..."
    sudo systemctl start docker
    sudo systemctl enable docker
    echo -e "${GREEN}Docker has been started.${NC}"
else
    echo -e "${GREEN}Docker service is running.${NC}"
fi

# Checking curl (needed for healthcheck)
if ! check_command "curl"; then
    echo "Installing curl..."
    sudo apt install -y curl
fi

# 3. Building Application (Maven)
# Check if .jar files exist, if not - build.
# Could force build with --rebuild flag, but keeping it simple here.
echo -e "${YELLOW}[2/6] Verifying application artifacts...${NC}"
if [ ! -f "patient/target/patient-0.0.1-SNAPSHOT.jar" ]; then
    echo "No built .jar files found. Running Maven build (this may take a while)..."
    ./mvnw clean install -DskipTests
    echo -e "${GREEN}Build completed.${NC}"
else
    echo -e "${GREEN}Artifacts exist. Skipping full build (for speed).${NC}"
fi

# 4. Starting Containers
echo -e "${YELLOW}[3/6] Starting Docker Compose...${NC}"
docker compose up -d --build

# 5. Waiting for start (Healthcheck)
echo -e "${YELLOW}[4/6] Waiting for services to start (Gateway :8080)...${NC}"
echo "This may take 60-90 seconds for all Java services to start."

MAX_RETRIES=60
COUNT=0
URL="http://localhost:8080/actuator/health" # Gateway endpoint

while [ $COUNT -lt $MAX_RETRIES ]; do
    if curl -s "$URL" > /dev/null; then
        echo -e "${GREEN}System responding! Gateway is active.${NC}"
        break
    fi
    echo -n "."
    sleep 2
    COUNT=$((COUNT+1))
done

if [ $COUNT -eq $MAX_RETRIES ]; then
    echo -e "${RED}Timeout reached. Check logs (docker compose logs -f).${NC}"
    # Not exiting, trying to open what is available.
fi

# 6. Starting data simulator
echo -e "\n${YELLOW}[5/6] Starting telemetry simulator...${NC}"
if [ -f "infra/simulator.sh" ]; then
    chmod +x infra/simulator.sh
    ./infra/simulator.sh &
    SIM_PID=$!
    echo -e "${GREEN}Simulator running in background (PID: $SIM_PID).${NC}"
else
    echo "No simulator.sh file found, skipping."
fi

# 7. Opening browser
echo -e "${YELLOW}[6/6] Opening browser windows...${NC}"

open_url() {
    if check_command "xdg-open"; then
        xdg-open "$1" > /dev/null 2>&1
    elif check_command "google-chrome"; then
        google-chrome "$1" > /dev/null 2>&1
    elif check_command "firefox"; then
        firefox "$1" > /dev/null 2>&1
    else
        echo "Cannot open browser automatically. URL: $1"
    fi
}

# Open Dashboard (UI)
open_url "http://localhost:4200"
sleep 1
# Open Grafana
open_url "http://localhost:3000"
sleep 1
# Open Zipkin
open_url "http://localhost:9411"

echo -e "\n${GREEN}=== 🎉 Demo Started Successfully! ===${NC}"
echo -e "${YELLOW}Access Information:${NC}"
echo -e " 🖥️  Frontend UI:    http://localhost:4200"
echo -e " 📊 Grafana:        http://localhost:3000  (Login: ${GREEN}admin${NC} / Pass: ${GREEN}admin${NC})"
echo -e " 🔍 Zipkin:         http://localhost:9411"
echo -e " 🚪 API Gateway:    http://localhost:8080"
echo -e "\nℹ️  To stop the demo and release resources, run:"
echo -e "    ${YELLOW}./smarthealth-platform/scripts/stop-demo.sh${NC}"