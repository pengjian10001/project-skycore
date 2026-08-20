#!/usr/bin/env bash
# 启动外联进程 boot-open（第 1 期）。实现父 POM 与启动类后生效。
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
if [[ ! -f mvnw ]]; then
  echo "Maven Wrapper 尚未生成。请先完成第 1 期工程脚手架（见 docs/todo.md）。" >&2
  exit 1
fi
exec ./mvnw -pl boot-open spring-boot:run "$@"
