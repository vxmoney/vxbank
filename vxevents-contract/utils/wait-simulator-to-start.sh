#!/bin/bash

CHAIN_SIMULATOR_URL=http://localhost:8085

# This will stop the script execution if a command returns an error
set -e

wait_simulator_to_start() {
    local endpoint="${CHAIN_SIMULATOR_URL}/network/config"
    local max_attempts="10"
    local wait_interval_in_seconds="6"

    echo "Waiting for simulator to start at $endpoint"

    for ((i = 1; i <= max_attempts; i++)); do
        if [ "$(curl -s -o /dev/null -w "%{http_code}" "$endpoint")" -eq 200 ]; then
            echo "Endpoint '$endpoint' is reachable."
            return 0
        fi
        sleep "$wait_interval_in_seconds"
    done

    echo "Error: Timed out waiting for endpoint '$endpoint' to become reachable."
    return 1
}

wait_simulator_to_start


