#!/usr/bin/env bash
# 启动外联进程 boot-open（第 1 期）
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
if [[ -x ./mvnw ]]; then
  MVN=./mvnw
elif command -v mvn >/dev/null 2>&1; then
  MVN=mvn
else
  echo "需要 Maven Wrapper (./mvnw) 或本机 mvn。" >&2
  exit 1
fi
echo "Starting boot-open on http://localhost:8081 — test page: /test.html"
exec "$MVN" -pl boot-open -am spring-boot:run "$@"
