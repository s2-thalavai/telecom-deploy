#!/usr/bin/env bash
# Post-deployment verification: readiness, deployed version, environment, and a real API call.
# Usage: ./smoke-test.sh <base-url> <management-url> <expected-version> <expected-env> [bearer-token]
set -euo pipefail
BASE_URL=$1; MGMT_URL=$2; EXPECTED_VERSION=$3; EXPECTED_ENV=$4; TOKEN=${5:-}

echo "Waiting for readiness at $MGMT_URL ..."
for i in $(seq 1 30); do
  if curl -fs "$MGMT_URL/actuator/health/readiness" | grep -q '"UP"'; then echo "Ready"; break; fi
  [ "$i" -eq 30 ] && { echo "Service never became ready"; exit 1; }
  sleep 5
done

INFO=$(curl -fs "$MGMT_URL/actuator/info")
VERSION=$(echo "$INFO" | jq -r '.deployment.version')
ENVIRONMENT=$(echo "$INFO" | jq -r '.deployment.environment')
[ "$VERSION" = "$EXPECTED_VERSION" ] || { echo "Version mismatch: got $VERSION expected $EXPECTED_VERSION"; exit 1; }
[ "$ENVIRONMENT" = "$EXPECTED_ENV" ] || { echo "Env mismatch: got $ENVIRONMENT expected $EXPECTED_ENV"; exit 1; }

AUTH=()
[ -n "$TOKEN" ] && AUTH=(-H "Authorization: Bearer $TOKEN")
STATUS=$(curl -s -o /dev/null -w '%{http_code}' "${AUTH[@]}" "$BASE_URL/api/v1/plans")
[ "$STATUS" = "200" ] || { echo "API check failed: HTTP $STATUS"; exit 1; }

echo "Smoke test passed: $EXPECTED_ENV running $VERSION"
