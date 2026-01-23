#!/bin/bash
# Navigate to project root (one level up from scripts/)
cd "$(dirname "$0")/.."

set -e

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}=== Starting Full System Tests (Java + Angular) ===${NC}"

# 1. Java Backend Tests
echo -e "\n${GREEN}[1/2] Running Backend Tests (Maven)...${NC}"
# Using 'verify' as per documentation to ensure contract stubs are generated
./mvnw clean verify

# Check if Maven succeeded (set -e should handle it, but explicit check is nice for logging)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Backend tests passed.${NC}"
else
    echo -e "${RED}❌ Backend tests failed.${NC}"
    exit 1
fi

# 2. Angular Frontend Tests
echo -e "\n${GREEN}[2/2] Running Frontend Tests (Angular/Jest)...${NC}"
cd web-ui

# Ensure dependencies are installed (optional but good for safety)
if [ ! -d "node_modules" ]; then
    echo "Installing frontend dependencies..."
    npm install
fi

# Run Jest tests. 
# We pass --watchAll=false to ensure it runs once and exits (CI mode).
npm test -- --watchAll=false

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Frontend tests passed.${NC}"
else
    echo -e "${RED}❌ Frontend tests failed.${NC}"
    exit 1
fi

echo -e "\n${GREEN}=== 🎉 ALL TESTS PASSED ===${NC}"
