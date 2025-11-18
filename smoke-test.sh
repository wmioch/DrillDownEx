#!/bin/bash
# Smoke test script for DrillDown desktop application
# This script launches the application and verifies it stays running for 30 seconds

set -euo pipefail

# Cleanup function
cleanup() {
    echo ""
    echo "Cleaning up..."
    set +e  # Disable exit on error for cleanup
    if [ -n "${APP_PID:-}" ]; then
        kill $APP_PID 2>/dev/null || true
        kill -9 $APP_PID 2>/dev/null || true
    fi
    if [ -n "${XVFB_PID:-}" ]; then
        kill $XVFB_PID 2>/dev/null || true
        kill -9 $XVFB_PID 2>/dev/null || true
    fi
}

# Set up trap to ensure cleanup on exit
trap cleanup EXIT INT TERM

echo "========================================="
echo "  DrillDown Desktop Smoke Test"
echo "========================================="
echo ""

# Configuration
JAR_PATH="desktop/build/libs/desktop-1.0.jar"
TEST_DURATION=30
DISPLAY=":99"

# Verify JAR exists
if [ ! -f "$JAR_PATH" ]; then
    echo "ERROR: JAR file not found at $JAR_PATH"
    exit 1
fi

echo "JAR file found: $JAR_PATH"
echo "Test duration: ${TEST_DURATION} seconds"
echo ""

# Start Xvfb (virtual display)
echo "Starting virtual display (Xvfb)..."
Xvfb $DISPLAY -screen 0 1280x720x24 &
XVFB_PID=$!
sleep 2

# Verify Xvfb is running
if ! ps -p $XVFB_PID > /dev/null; then
    echo "ERROR: Failed to start Xvfb"
    exit 1
fi

echo "Virtual display started (PID: $XVFB_PID)"
echo ""

# Launch the application in windowed mode
echo "Launching DrillDown desktop application..."
export DISPLAY=$DISPLAY
java -jar "$JAR_PATH" windowed &
APP_PID=$!

echo "Application launched (PID: $APP_PID)"
echo ""

# Monitor the application
echo "Monitoring application for ${TEST_DURATION} seconds..."
ELAPSED=0
INTERVAL=1

while [ $ELAPSED -lt $TEST_DURATION ]; do
    # Check if the application is still running
    if ! ps -p $APP_PID > /dev/null; then
        echo ""
        echo "ERROR: Application crashed or exited prematurely after ${ELAPSED} seconds"
        exit 1
    fi
    
    # Progress indicator
    if [ $((ELAPSED % 5)) -eq 0 ]; then
        echo "  ${ELAPSED}s / ${TEST_DURATION}s - Application still running ✓"
    fi
    
    sleep $INTERVAL
    ELAPSED=$((ELAPSED + INTERVAL))
done

echo ""
echo "SUCCESS: Application ran successfully for ${TEST_DURATION} seconds!"
echo ""

echo "Smoke test completed successfully!"
exit 0
