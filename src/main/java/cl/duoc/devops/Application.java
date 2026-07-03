package cl.duoc.devops;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public final class Application {
  static final String RESPONSE = "Hola Mundo\n";

  private Application() {
  }

  public static void main(String[] args) throws IOException {
    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8082"));
    HttpServer server = createServer(new InetSocketAddress("0.0.0.0", port));
    server.start();
    System.out.println("Hola Mundo escuchando en puerto " + port);
  }

  static HttpServer createServer(InetSocketAddress address) throws IOException {
    HttpServer server = HttpServer.create(address, 0);
    server.createContext("/", Application::handle);
    return server;
  }

  private static void handle(HttpExchange exchange) throws IOException {
    System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
    byte[] bytes = RESPONSE.getBytes(StandardCharsets.UTF_8);

    exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
    exchange.sendResponseHeaders(200, bytes.length);
    try (OutputStream stream = exchange.getResponseBody()) {
      stream.write(bytes);
    }
  }
}
