#!/usr/bin/env bash
# Roll back the Deployment to the previous (or a specific) revision and wait for it.
# Usage: ./rollback.sh <namespace> [revision]
set -euo pipefail
NS=$1; REVISION=${2:-}
kubectl -n "$NS" rollout history deployment/telecom-plans
if [ -n "$REVISION" ]; then
  kubectl -n "$NS" rollout undo deployment/telecom-plans --to-revision="$REVISION"
else
  kubectl -n "$NS" rollout undo deployment/telecom-plans
fi
kubectl -n "$NS" rollout status deployment/telecom-plans --timeout=300s
kubectl -n "$NS" get deployment telecom-plans -o jsonpath='{.spec.template.spec.containers[0].image}'; echo
