#!/bin/bash
echo "Initializing LocalStack with secrets..."

# Wait for SSM to be ready (naive wait)
sleep 5

# Create secrets for Patient Service
awslocal ssm put-parameter --name "/config/patient-service/spring.datasource.password" --value "password" --type "SecureString"
awslocal ssm put-parameter --name "/config/patient-service/spring.datasource.username" --value "user" --type "String"

echo "Secrets initialized."
