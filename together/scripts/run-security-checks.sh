#!/bin/bash

# Script to run security checks on the Together application

echo "Running security checks for Together application..."

# Make sure gradlew is executable
chmod +x ./gradlew

echo "1. Running OWASP Dependency Check..."
./gradlew dependencyCheckAnalyze
if [ $? -ne 0 ]; then
    echo "❌ Dependency check failed!"
    exit 1
fi

echo "2. Running SpotBugs security scan..."
./gradlew spotbugsMain spotbugsTest
if [ $? -ne 0 ]; then
    echo "❌ SpotBugs scan failed!"
    exit 1
fi

echo "3. Running focused security scan..."
./gradlew spotbugsSecurity
if [ $? -ne 0 ]; then
    echo "❌ Security-focused scan failed!"
    exit 1
fi

echo "✅ All security checks completed successfully!"

echo ""
echo "Security reports generated:"
echo "- SpotBugs: build/reports/spotbugs/"
echo "- Dependency Check: build/reports/dependency-check-report.html"
echo "- Test Coverage: build/reports/jacoco/"