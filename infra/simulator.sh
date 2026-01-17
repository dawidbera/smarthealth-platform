#!/bin/bash

DEVICE_SN="HR-MON-001"
ENDPOINT="http://localhost:8080/device/telemetry"

echo "Starting Telemetry Simulator for device $DEVICE_SN..."
echo "Sending data to $ENDPOINT"

while true; do
  VALUE=$(shuf -i 60-100 -n 1)
  TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
  
  JSON_DATA="{\"serialNumber\":\"$DEVICE_SN\",\"value\":$VALUE,\"unit\":\"BPM\",\"timestamp\":\"$TIMESTAMP\"}"
  
  curl -s -X POST "$ENDPOINT" \
    -H "Content-Type: application/json" \
    -d "$JSON_DATA"
    
  echo "Sent: $VALUE BPM at $TIMESTAMP"
  sleep 5
done
