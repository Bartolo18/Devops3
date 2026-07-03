const http = require('node:http');

const serviceName = 'hola-mundo-devops';
const startedAt = Date.now();
let requestCount = 0;

function json(res, statusCode, payload) {
  const body = JSON.stringify(payload);
  res.writeHead(statusCode, {
    'content-type': 'application/json; charset=utf-8',
    'content-length': Buffer.byteLength(body)
  });
  res.end(body);
}

function text(res, statusCode, body, contentType = 'text/plain; charset=utf-8') {
  res.writeHead(statusCode, {
    'content-type': contentType,
    'content-length': Buffer.byteLength(body)
  });
  res.end(body);
}

function logRequest(req, statusCode, durationMs) {
  const entry = {
    timestamp: new Date().toISOString(),
    service: serviceName,
    method: req.method,
    path: new URL(req.url, 'http://localhost').pathname,
    statusCode,
    durationMs
  };

  process.stdout.write(`${JSON.stringify(entry)}\n`);
}

function uptimeSeconds() {
  return Math.floor((Date.now() - startedAt) / 1000);
}

function createServer() {
  return http.createServer((req, res) => {
    const started = Date.now();
    const path = new URL(req.url, 'http://localhost').pathname;
    requestCount += 1;

    if (req.method === 'GET' && (path === '/' || path === '/api/v1/hola')) {
      text(res, 200, 'Hola Mundo\n');
      logRequest(req, 200, Date.now() - started);
      return;
    }

    if (req.method === 'GET' && path === '/health') {
      json(res, 200, {
        status: 'ok',
        service: serviceName,
        uptimeSeconds: uptimeSeconds()
      });
      logRequest(req, 200, Date.now() - started);
      return;
    }

    if (req.method === 'GET' && path === '/metrics') {
      const metrics = [
        '# HELP app_requests_total Total de requests HTTP recibidos.',
        '# TYPE app_requests_total counter',
        `app_requests_total{service="${serviceName}"} ${requestCount}`,
        '# HELP app_uptime_seconds Tiempo activo del microservicio.',
        '# TYPE app_uptime_seconds gauge',
        `app_uptime_seconds{service="${serviceName}"} ${uptimeSeconds()}`
      ].join('\n');

      text(res, 200, `${metrics}\n`, 'text/plain; version=0.0.4; charset=utf-8');
      logRequest(req, 200, Date.now() - started);
      return;
    }

    json(res, 404, {
      error: 'not_found',
      message: 'Ruta no encontrada'
    });
    logRequest(req, 404, Date.now() - started);
  });
}

if (require.main === module) {
  const port = Number(process.env.PORT || 8082);
  const server = createServer();

  server.listen(port, '0.0.0.0', () => {
    process.stdout.write(
      JSON.stringify({
        timestamp: new Date().toISOString(),
        service: serviceName,
        message: `Servidor iniciado en puerto ${port}`
      }) + '\n'
    );
  });
}

module.exports = {
  createServer
};
