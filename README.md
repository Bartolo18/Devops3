# Entrega 3 - DevOps

## Proyecto

Este proyecto corresponde a la Entrega 3 del ramo DevOps. Consiste en una aplicacion Java simple que levanta un servidor HTTP y responde `Hola Mundo` en el endpoint principal.

El objetivo es contar con una aplicacion basica que pueda ejecutarse localmente y desplegarse usando contenedores y un flujo de automatizacion.

## Como levantar el proyecto

### Ejecutar con Maven

Requisitos:
- Java 21

Comandos:

```bash
./mvnw verify
java -jar target/hola-mundo-devops-1.0.0.jar
```

Probar el servicio:

```bash
curl http://localhost:8082/
```

### Ejecutar con Docker Compose

Requisitos:
- Docker
- Docker Compose

Comando:

```bash
docker compose up -d --build
```

Probar el servicio:

```bash
curl http://localhost:8082/
```

## Entrega 3 del ramo DevOps

Esta entrega presenta una aplicacion Java simple preparada para su construccion, ejecucion y despliegue. El proyecto sirve como base para demostrar el trabajo practico asociado a la tercera entrega del ramo DevOps.

## Integrantes

- Marco Suarez
- Barbara Tolorza
