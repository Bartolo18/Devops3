#!/usr/bin/env sh
set -eu

echo "Ejecutando auditoria de calidad y cumplimiento..."

if grep -R --line-number -E 'eval[[:space:]]*\(|TODO_CRITICO|password[[:space:]]*=' src test; then
  echo "Falla critica: se encontro codigo inseguro o deuda critica."
  exit 1
fi

if ! grep -q 'USER node' Dockerfile; then
  echo "Falla critica: el contenedor debe ejecutarse con un usuario no root."
  exit 1
fi

if ! grep -R '/health' test >/dev/null; then
  echo "Falla critica: falta prueba automatizada del endpoint /health."
  exit 1
fi

if ! grep -R '/metrics' test >/dev/null; then
  echo "Falla critica: falta prueba automatizada del endpoint /metrics."
  exit 1
fi

echo "Auditoria aprobada."
