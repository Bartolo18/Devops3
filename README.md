# Hola Mundo DevOps Java

Microservicio Java minimo para demostrar CI/CD, pruebas JUnit, cobertura JaCoCo, analisis SonarCloud, despliegue en EC2 con Docker Compose y observabilidad con AWS CloudWatch.

## Alcance

Este proyecto evita Kubernetes. El despliegue se realiza en una instancia EC2 mediante Docker Compose y la observabilidad se realiza con AWS CloudWatch.

## Endpoints

- `GET /`: responde `Hola Mundo`
- `GET /api/v1/hola`: responde `Hola Mundo`
- `GET /health`: estado del servicio
- `GET /metrics`: metricas simples para validacion manual del microservicio

## Ejecutar localmente

```bash
./mvnw test
./mvnw verify
./scripts/audit-quality.sh
PORT=8082 java -jar target/hola-mundo-devops-1.0.0.jar
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
2. Cobertura JaCoCo y validacion de cobertura minima.
3. Auditoria de calidad y cumplimiento local.
4. Analisis SonarCloud con Quality Gate.
5. Build de imagen Docker.
6. Deploy en EC2 usando un runner self-hosted con etiquetas `self-hosted`, `ep3` y `deploy`.

Si una prueba, JaCoCo, la auditoria o el Quality Gate de SonarCloud fallan, el deploy no se ejecuta.

Para SonarCloud debes configurar en GitHub:

- Secret `SONAR_TOKEN`
- Variable `SONAR_ORGANIZATION`
- Variable `SONAR_PROJECT_KEY`

## Observabilidad

El servicio imprime logs JSON por stdout y expone `/metrics` para validaciones simples. En EC2, la observabilidad principal se realiza con CloudWatch: puedes enviar logs del contenedor y metricas de infraestructura usando `infra/cloudwatch-agent-config.json` y crear un dashboard base con `infra/cloudwatch-dashboard-template.json`.

La guia de entrega esta en `docs/GUIA_ENTREGA.md`.
