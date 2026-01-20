#!/bin/bash

DEVICE_SN="HR-MON-001"
PATIENT_ID=1
ENDPOINT="http://localhost:8080/device/telemetry"

echo "Starting Telemetry Simulator for device $DEVICE_SN (Patient: $PATIENT_ID)..."
echo "Sending data to $ENDPOINT"

while true; do
  # 10% chance of high heart rate (anomaly)
  if [ $((RANDOM % 10)) -eq 0 ]; then
    VALUE=$(shuf -i 130-150 -n 1)
    echo "!!! GENERATING ANOMALY !!!"
  else
    VALUE=$(shuf -i 60-100 -n 1)
  fi
  
  TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
  
  JSON_DATA="{\"serialNumber\":\"$DEVICE_SN\",\"patientId\":$PATIENT_ID,\"value\":$VALUE,\"unit\":\"BPM\",\"timestamp\":\"$TIMESTAMP\"}"
  
  curl -s -X POST "$ENDPOINT" \
    -H "Content-Type: application/json" \
    -d "$JSON_DATA"
    
  echo "Sent: $VALUE BPM at $TIMESTAMP"
  sleep 2
done
