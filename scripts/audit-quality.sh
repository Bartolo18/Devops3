#!/usr/bin/env sh
set -eu

echo "Ejecutando auditoria de calidad y cumplimiento..."

if grep -R --line-number -E 'TODO_CRITICO|password[[:space:]]*=|System\\.exit|Runtime\\.getRuntime\\(\\)\\.exec' src; then
  echo "Falla critica: se encontro codigo inseguro o deuda critica."
  exit 1
fi

if ! grep -q 'USER app' Dockerfile; then
  echo "Falla critica: el contenedor debe ejecutarse con un usuario no root."
  exit 1
fi

if ! grep -q 'jacoco-maven-plugin' pom.xml; then
  echo "Falla critica: falta configuracion de JaCoCo en Maven."
  exit 1
fi

if ! grep -q 'sonarcloud.io' .github/workflows/ci-cd.yml; then
  echo "Falla critica: falta analisis SonarCloud en el pipeline."
  exit 1
fi

if ! grep -R '/health' src/test >/dev/null; then
  echo "Falla critica: falta prueba automatizada del endpoint /health."
  exit 1
fi

if ! grep -R '/metrics' src/test >/dev/null; then
  echo "Falla critica: falta prueba automatizada del endpoint /metrics."
  exit 1
fi

echo "Auditoria aprobada."
