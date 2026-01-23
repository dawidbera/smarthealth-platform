#!/bin/bash
# Navigate to project root (one level up from scripts/)
cd "$(dirname "$0")/.."

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}Stopping SmartHealth Demo and cleaning up...${NC}"

# Stop the containers
docker compose down

# Kill the simulator if it's running
SIM_PID=$(pgrep -f "infra/simulator.sh" || true)
if [ ! -z "$SIM_PID" ]; then
    echo "Stopping telemetry simulator (PID: $SIM_PID)..."
    kill $SIM_PID || true
fi

echo -e "${GREEN}✅ Demo stopped and resources released.${NC}"
