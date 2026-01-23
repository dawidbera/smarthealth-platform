#!/bin/bash
# Navigate to project root (one level up from scripts/)
cd "$(dirname "$0")/.."

echo "Stopping SmartHealth Platform..."
docker compose down
echo "🛑 SmartHealth Platform stopped and resources released."
