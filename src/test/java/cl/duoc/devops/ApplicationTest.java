package cl.duoc.devops;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

class ApplicationTest {
  private static final HttpClient CLIENT = HttpClient.newHttpClient();

  @Test
  void returnsHelloWorld() throws Exception {
    try (RunningServer server = RunningServer.start()) {
      HttpResponse<String> response = get(server.url("/"));

      assertEquals(200, response.statusCode());
      assertEquals("Hola Mundo\n", response.body());
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
      HttpServer server = Application.createServer(new InetSocketAddress("127.0.0.1", 0));
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
