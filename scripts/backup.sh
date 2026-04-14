#!/usr/bin/env bash
set -euo pipefail
if [[ -z "${MYSQL_DATABASE:-}" || -z "${MYSQL_USER:-}" ]]; then
  echo "Set MYSQL_DATABASE, MYSQL_USER, MYSQL_PWD (or MYSQL_PASSWORD) before running." >&2
  exit 1
fi
TS="$(date +%Y%m%d_%H%M%S)"
OUT="${BACKUP_DIR:-./backups}/dump_${MYSQL_DATABASE}_${TS}.sql.gz"
mkdir -p "$(dirname "$OUT")"
mysqldump --single-transaction --routines --triggers -u"$MYSQL_USER" -p"${MYSQL_PWD:-${MYSQL_PASSWORD:-}}" "$MYSQL_DATABASE" | gzip > "$OUT"
echo "Backup written to $OUT"
