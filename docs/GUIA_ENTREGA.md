# Guia rapida para la Evaluacion 3

Esta guia esta pensada para grabar el video pedido: push al repositorio, ejecucion del pipeline, despliegue automatico en AWS EC2, observabilidad con CloudWatch y validacion antes/despues.

## Alcance definido

- No se utiliza Kubernetes.
- El microservicio se despliega en EC2 con Docker Compose.
- La observabilidad se realiza con AWS CloudWatch: metricas, logs, dashboard y alarma.

## 1. Pruebas locales

```bash
npm test
npm run coverage
npm run audit:quality
docker compose up -d --build
curl http://localhost:8082/
curl http://localhost:8082/health
curl http://localhost:8082/metrics
docker compose logs --tail=20
```

## 2. Repositorio GitHub

```bash
git init
git add .
git commit -m "Proyecto minimo hola mundo devops"
git branch -M main
git remote add origin URL_DEL_REPOSITORIO
git push -u origin main
```

Entrega en AVA la URL del repositorio.

## 3. Runner self-hosted en EC2

En GitHub: `Settings > Actions > Runners > New self-hosted runner`.

Usa las etiquetas:

```text
self-hosted
ep3
deploy
```

En la instancia EC2 deben estar instalados Docker y Docker Compose. Tambien debes abrir el puerto `8082` en el Security Group. Esta arquitectura reemplaza el uso de Kubernetes por un despliegue simple en EC2, suficiente para mostrar CI/CD, monitoreo y validacion del microservicio.

## 4. Validacion antes y despues del despliegue

Antes del push o antes de ejecutar el pipeline:

```bash
curl http://IP_PUBLICA_EC2:8082/health
```

Despues del pipeline:

```bash
curl http://IP_PUBLICA_EC2:8082/
curl http://IP_PUBLICA_EC2:8082/health
curl http://IP_PUBLICA_EC2:8082/metrics
```

En el video muestra que GitHub Actions termina el job `quality` y luego el job `deploy`.

## 5. CloudWatch

Configura CloudWatch Agent usando `infra/cloudwatch-agent-config.json` para enviar la observabilidad requerida:

- CPU, memoria y disco de la instancia.
- Logs JSON generados por el contenedor Docker.

Puedes usar `infra/cloudwatch-dashboard-template.json` como base para un dashboard. Reemplaza:

- `REGION` por tu region AWS, por ejemplo `us-east-1`.
- `INSTANCE_ID` por el ID real de tu instancia EC2.

Para probar la alarma de CPU en EC2:

```bash
sudo apt-get update
sudo apt-get install -y stress-ng
stress-ng --cpu 2 --timeout 120s
```

Luego muestra en CloudWatch que la metrica sube y que la alarma cambia de estado.

## 6. Demostrar que el pipeline se detiene

Haz una rama de prueba y agrega temporalmente una linea insegura en `src/server.js`:

```js
eval('1 + 1');
```

Al subir esa rama o abrir un Pull Request, el paso `Validar cumplimiento` falla por `scripts/audit-quality.sh`. Eso demuestra el IE6: el pipeline se interrumpe ante una falla critica de calidad o seguridad.

No dejes esa linea en `main`.

## 7. Checklist para el video

- Mostrar el repositorio en GitHub.
- Mostrar el push.
- Mostrar GitHub Actions ejecutando pruebas, cobertura, auditoria y Docker build.
- Mostrar deploy en EC2 con Docker Compose y el runner self-hosted.
- Mostrar `curl` antes y despues del despliegue.
- Mostrar CloudWatch con metricas, logs y alarma.
- Mostrar una falla controlada que detiene el pipeline.
