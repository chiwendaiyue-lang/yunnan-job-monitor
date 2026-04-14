#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
echo "Building backend..."
(cd "$ROOT/backend" && mvn -q -DskipTests package)
echo "Building frontend..."
(cd "$ROOT/frontend" && npm install && npm run build)
echo "Artifacts:"
echo "  - $ROOT/backend/target/*.jar"
echo "  - $ROOT/frontend/dist/"
echo "Configure systemd / Docker / Nginx according to your environment."
