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
    start("0.0.0.0", Integer.parseInt(System.getenv().getOrDefault("PORT", "8082")));
  }

  static HttpServer start(String host, int port) throws IOException {
    HttpServer server = createServer(new InetSocketAddress(host, port));
    server.start();
    return server;
  }

  static HttpServer createServer(InetSocketAddress address) throws IOException {
    HttpServer server = HttpServer.create(address, 0);
    server.createContext("/", Application::handle);
    return server;
  }

  private static void handle(HttpExchange exchange) throws IOException {
    byte[] bytes = RESPONSE.getBytes(StandardCharsets.UTF_8);

    exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
    exchange.sendResponseHeaders(200, bytes.length);
    try (OutputStream stream = exchange.getResponseBody()) {
      stream.write(bytes);
    }
  }
}
