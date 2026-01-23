#!/bin/bash
# Navigate to project root (one level up from scripts/)
cd "$(dirname "$0")/.."

echo "Starting SmartHealth Platform..."
docker compose up -d
echo "✅ SmartHealth Platform started!"
