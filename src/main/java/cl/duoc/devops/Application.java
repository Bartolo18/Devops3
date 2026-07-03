package cl.duoc.devops;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public final class Application {
  static final String SERVICE_NAME = "hola-mundo-devops";

  private static final long STARTED_AT_MILLIS = System.currentTimeMillis();
  private static final AtomicLong REQUEST_COUNT = new AtomicLong();

  private Application() {
  }

  public static void main(String[] args) throws IOException {
    int port = readPort();
    HttpServer server = createServer("0.0.0.0", port);
    server.start();
    logStartup(port);
  }

  public static HttpServer createServer(String host, int port) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
    server.createContext("/", Application::handle);
    server.setExecutor(Executors.newCachedThreadPool());
    return server;
  }

  static int readPort() {
    String rawPort = System.getenv().getOrDefault("PORT", "8082");

    try {
      return Integer.parseInt(rawPort);
    } catch (NumberFormatException error) {
      return 8082;
    }
  }

  private static void handle(HttpExchange exchange) throws IOException {
    long started = System.nanoTime();
    int statusCode = 500;
    REQUEST_COUNT.incrementAndGet();

    try {
      String method = exchange.getRequestMethod();
      String path = exchange.getRequestURI().getPath();

      if ("GET".equals(method) && ("/".equals(path) || "/api/v1/hola".equals(path))) {
        statusCode = 200;
        write(exchange, statusCode, "Hola Mundo\n", "text/plain; charset=utf-8");
        return;
      }

      if ("GET".equals(method) && "/health".equals(path)) {
        statusCode = 200;
        write(exchange, statusCode, healthBody(), "application/json; charset=utf-8");
        return;
      }

      if ("GET".equals(method) && "/metrics".equals(path)) {
        statusCode = 200;
        write(exchange, statusCode, metricsBody(), "text/plain; charset=utf-8");
        return;
      }

      statusCode = 404;
      write(exchange, statusCode, "{\"error\":\"not_found\",\"message\":\"Ruta no encontrada\"}\n",
          "application/json; charset=utf-8");
    } finally {
      long durationMs = Math.max(0L, (System.nanoTime() - started) / 1_000_000L);
      logRequest(exchange, statusCode, durationMs);
    }
  }

  private static String healthBody() {
    return String.format(Locale.ROOT,
        "{\"status\":\"ok\",\"service\":\"%s\",\"uptimeSeconds\":%d}\n",
        SERVICE_NAME,
        uptimeSeconds());
  }

  private static String metricsBody() {
    return String.join("\n",
        "# HELP app_requests_total Total de requests HTTP recibidos.",
        "# TYPE app_requests_total counter",
        "app_requests_total{service=\"" + SERVICE_NAME + "\"} " + REQUEST_COUNT.get(),
        "# HELP app_uptime_seconds Tiempo activo del microservicio.",
        "# TYPE app_uptime_seconds gauge",
        "app_uptime_seconds{service=\"" + SERVICE_NAME + "\"} " + uptimeSeconds(),
        "");
  }

  private static long uptimeSeconds() {
    return (System.currentTimeMillis() - STARTED_AT_MILLIS) / 1000L;
  }

  private static void write(HttpExchange exchange, int statusCode, String body, String contentType)
      throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    Headers headers = exchange.getResponseHeaders();
    headers.set("Content-Type", contentType);
    exchange.sendResponseHeaders(statusCode, bytes.length);

    try (OutputStream stream = exchange.getResponseBody()) {
      stream.write(bytes);
    }
  }

  private static void logStartup(int port) {
    System.out.printf(Locale.ROOT,
        "{\"timestamp\":\"%s\",\"service\":\"%s\",\"message\":\"Servidor iniciado en puerto %d\"}%n",
        Instant.now(),
        SERVICE_NAME,
        port);
  }

  private static void logRequest(HttpExchange exchange, int statusCode, long durationMs) {
    System.out.printf(Locale.ROOT,
        "{\"timestamp\":\"%s\",\"service\":\"%s\",\"method\":\"%s\",\"path\":\"%s\",\"statusCode\":%d,\"durationMs\":%d}%n",
        Instant.now(),
        SERVICE_NAME,
        jsonEscape(exchange.getRequestMethod()),
        jsonEscape(exchange.getRequestURI().getPath()),
        statusCode,
        durationMs);
  }

  private static String jsonEscape(String value) {
    return value.replace("\\", "\\\\").replace("\"", "\\\"");
  }
}
