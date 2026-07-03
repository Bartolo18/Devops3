# Hola Mundo DevOps

Microservicio minimo para demostrar CI/CD, despliegue en EC2 con Docker Compose, logs, metricas y validaciones de calidad.

## Endpoints

- `GET /`: responde `Hola Mundo`
- `GET /api/v1/hola`: responde `Hola Mundo`
- `GET /health`: estado del servicio
- `GET /metrics`: metricas simples en formato compatible con Prometheus

## Ejecutar localmente

```bash
npm test
npm run coverage
npm run audit:quality
npm start
```

Luego prueba:

```bash
curl http://localhost:8082/
curl http://localhost:8082/health
curl http://localhost:8082/metrics
```

## Ejecutar con Docker

```bash
docker compose up -d --build
docker compose ps
curl http://localhost:8082/health
docker compose logs -f
```

## Pipeline

El workflow `.github/workflows/ci-cd.yml` ejecuta:

1. Pruebas automatizadas.
2. Cobertura con el test runner de Node.js.
3. Auditoria de calidad y cumplimiento.
4. Build de imagen Docker.
5. Deploy en EC2 usando un runner self-hosted con etiquetas `self-hosted`, `ep3` y `deploy`.

Si una prueba o auditoria falla, el deploy no se ejecuta.

## Observabilidad

El servicio imprime logs JSON por stdout y expone `/metrics`. En EC2 puedes enviar logs y metricas a CloudWatch usando `infra/cloudwatch-agent-config.json` y crear un dashboard base con `infra/cloudwatch-dashboard-template.json`.

La guia de entrega esta en `docs/GUIA_ENTREGA.md`.
