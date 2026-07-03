const assert = require('node:assert/strict');
const test = require('node:test');

const { createServer } = require('../src/server');

async function withServer(run) {
  const server = createServer();

  await new Promise((resolve) => server.listen(0, '127.0.0.1', resolve));
  const { port } = server.address();

  try {
    await run(`http://127.0.0.1:${port}`);
  } finally {
    await new Promise((resolve, reject) => {
      server.close((error) => (error ? reject(error) : resolve()));
    });
  }
}

test('GET / responde Hola Mundo', async () => {
  await withServer(async (baseUrl) => {
    const response = await fetch(`${baseUrl}/`);
    const body = await response.text();

    assert.equal(response.status, 200);
    assert.equal(body, 'Hola Mundo\n');
  });
});

test('GET /health informa que el servicio esta ok', async () => {
  await withServer(async (baseUrl) => {
    const response = await fetch(`${baseUrl}/health`);
    const body = await response.json();

    assert.equal(response.status, 200);
    assert.equal(body.status, 'ok');
    assert.equal(body.service, 'hola-mundo-devops');
    assert.equal(typeof body.uptimeSeconds, 'number');
  });
});

test('GET /metrics expone metricas simples', async () => {
  await withServer(async (baseUrl) => {
    const response = await fetch(`${baseUrl}/metrics`);
    const body = await response.text();

    assert.equal(response.status, 200);
    assert.match(body, /app_requests_total/);
    assert.match(body, /app_uptime_seconds/);
  });
});

test('ruta inexistente responde 404', async () => {
  await withServer(async (baseUrl) => {
    const response = await fetch(`${baseUrl}/no-existe`);
    const body = await response.json();

    assert.equal(response.status, 404);
    assert.equal(body.error, 'not_found');
  });
});
