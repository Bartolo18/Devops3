package cl.duoc.devops;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

class ApplicationTest {
  private static final HttpClient CLIENT = HttpClient.newHttpClient();

  @Test
  void rootReturnsHelloWorld() throws Exception {
    try (RunningServer server = RunningServer.start()) {
      HttpResponse<String> response = get(server.url("/"));

      assertEquals(200, response.statusCode());
      assertEquals("Hola Mundo\n", response.body());
    }
  }

  @Test
  void apiRouteReturnsHelloWorld() throws Exception {
    try (RunningServer server = RunningServer.start()) {
      HttpResponse<String> response = get(server.url("/api/v1/hola"));

      assertEquals(200, response.statusCode());
      assertEquals("Hola Mundo\n", response.body());
    }
  }

  @Test
  void healthReturnsServiceStatus() throws Exception {
    try (RunningServer server = RunningServer.start()) {
      HttpResponse<String> response = get(server.url("/health"));

      assertEquals(200, response.statusCode());
      assertTrue(response.body().contains("\"status\":\"ok\""));
      assertTrue(response.body().contains("\"service\":\"hola-mundo-devops\""));
    }
  }

  @Test
  void metricsReturnsSimpleMetrics() throws Exception {
    try (RunningServer server = RunningServer.start()) {
      HttpResponse<String> response = get(server.url("/metrics"));

      assertEquals(200, response.statusCode());
      assertTrue(response.body().contains("app_requests_total"));
      assertTrue(response.body().contains("app_uptime_seconds"));
    }
  }

  @Test
  void unknownRouteReturnsNotFound() throws Exception {
    try (RunningServer server = RunningServer.start()) {
      HttpResponse<String> response = get(server.url("/no-existe"));

      assertEquals(404, response.statusCode());
      assertTrue(response.body().contains("\"error\":\"not_found\""));
    }
  }

  private static HttpResponse<String> get(String url) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
    return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private static final class RunningServer implements AutoCloseable {
    private final HttpServer server;

    private RunningServer(HttpServer server) {
      this.server = server;
    }

    static RunningServer start() throws IOException {
      HttpServer server = Application.createServer("127.0.0.1", 0);
      server.start();
      return new RunningServer(server);
    }

    String url(String path) {
      return "http://127.0.0.1:" + server.getAddress().getPort() + path;
    }

    @Override
    public void close() {
      server.stop(0);
    }
  }
}
