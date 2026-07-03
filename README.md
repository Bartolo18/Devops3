# Hola Mundo DevOps

Proyecto Java ultra simple: solo responde `Hola Mundo`.

Usa lo minimo para la evaluacion:

- JUnit para pruebas
- JaCoCo para cobertura
- SonarCloud para calidad
- Docker Compose para desplegar en EC2
- CloudWatch para monitorear la instancia y logs
- Sin Kubernetes

## Ejecutar

```bash
./mvnw verify
docker compose up -d --build
curl http://localhost:8082/
```

## Pipeline

El workflow ejecuta pruebas, genera reporte JaCoCo, analiza SonarCloud, construye Docker y despliega en EC2 con runner self-hosted.

Para SonarCloud debes configurar en GitHub:

- Secret `SONAR_TOKEN`
- Variable `SONAR_ORGANIZATION`
- Variable `SONAR_PROJECT_KEY`

El runner de EC2 debe tener las etiquetas:

```text
self-hosted
ep3
deploy
```

## CloudWatch

En AWS usa CloudWatch para mostrar CPU, memoria, disco, logs del contenedor y alarma. El repositorio no usa Kubernetes.
