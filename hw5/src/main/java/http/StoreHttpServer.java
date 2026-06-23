package http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;
import enteties.Product;
import service.ProductService;
import utils.DataBase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StoreHttpServer {
    public static final int PORT = 8080;
    private static final Gson GSON = new Gson();
    private static final ProductService SERVICE = DataBase.getProductService();

    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/login", new LoginHandler());

        HttpContext productsContext = server.createContext("/products", new ProductHandler());
        productsContext.setAuthenticator(new JwtAuthenticator());

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
        System.out.println("HTTP Server started on port " + PORT);
    }

    public static void main(String[] args) throws IOException {
        start();
    }


    static class JwtAuthenticator extends Authenticator {
        @Override
        public Result authenticate(HttpExchange exchange) {
            List<String> values = exchange.getRequestHeaders().get("Authorization");
            if (values == null || values.isEmpty()) {
                return new Failure(401);
            }

            String[] parts = values.getFirst().split(" ");
            if (parts.length != 2 || !parts[0].equals("Bearer")) {
                return new Failure(401);
            }

            String subject = JwtUtil.verify(parts[1]);
            if (subject == null) {
                return new Failure(401);
            }

            return new Success(new HttpPrincipal(subject, "USER"));
        }
    }

    static void send(HttpExchange ex, int status, Object body) throws IOException {
        byte[] bytes = GSON.toJson(body).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    static String readBody(HttpExchange ex) throws IOException {
        try (InputStream is = ex.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static class LoginHandler implements com.sun.net.httpserver.HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
                send(ex, 405, java.util.Map.of("error", "Method Not Allowed"));
                return;
            }
            try {
                JsonObject body = JsonParser.parseString(readBody(ex)).getAsJsonObject();
                String login = body.get("login").getAsString();
                String password = body.get("password").getAsString();

                if (!DataBase.getUserService().authenticate(login, password)) {
                    send(ex, 401, java.util.Map.of("error", "Invalid credentials"));
                    return;
                }

                send(ex, 200, java.util.Map.of("token", JwtUtil.generateToken(login)));
            } catch (Exception e) {
                send(ex, 400, java.util.Map.of("error", "Invalid request body"));
            }
        }
    }

    static class ProductHandler implements com.sun.net.httpserver.HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            String[] parts = ex.getRequestURI().getPath().split("/");
            boolean hasId = parts.length >= 3 && !parts[2].isEmpty();
            String method = ex.getRequestMethod().toUpperCase();

            try {
                if (!hasId && "PUT".equals(method)) {
                    handleCreate(ex);
                } else if (hasId && "GET".equals(method)) {
                    handleGet(ex, Long.parseLong(parts[2]));
                } else if (hasId && "POST".equals(method)) {
                    handleUpdate(ex, Long.parseLong(parts[2]));
                } else if (hasId && "DELETE".equals(method)) {
                    handleDelete(ex, Long.parseLong(parts[2]));
                } else {
                    send(ex, 405, java.util.Map.of("error", "Method Not Allowed"));
                }
            } catch (NumberFormatException e) {
                send(ex, 400, java.util.Map.of("error", "Invalid product id"));
            }
        }

        private void handleGet(HttpExchange ex, long id) throws IOException {
            Product p = SERVICE.findById(id);
            if (p == null) {
                send(ex, 404, java.util.Map.of("error", "Product not found"));
            } else {
                send(ex, 200, p);
            }
        }

        private void handleCreate(HttpExchange ex) throws IOException {
            try {
                Product p = GSON.fromJson(readBody(ex), Product.class);
                if (p == null || p.getName() == null || p.getName().isBlank()) {
                    send(ex, 400, java.util.Map.of("error", "Name is required"));
                    return;
                }
                if (SERVICE.findByName(p.getName()) != null) {
                    send(ex, 409, java.util.Map.of("error", "Product with this name already exists"));
                    return;
                }
                send(ex, 201, SERVICE.create(p));
            } catch (Exception e) {
                send(ex, 400, java.util.Map.of("error", "Invalid request body: " + e.getMessage()));
            }
        }

        private void handleUpdate(HttpExchange ex, long id) throws IOException {
            try {
                if (SERVICE.findById(id) == null) {
                    send(ex, 404, java.util.Map.of("error", "Product not found"));
                    return;
                }
                Product p = GSON.fromJson(readBody(ex), Product.class);
                if (p == null) {
                    send(ex, 400, java.util.Map.of("error", "Invalid request body"));
                    return;
                }
                p.setId(id);
                SERVICE.update(p);
                send(ex, 200, SERVICE.findById(id));
            } catch (Exception e) {
                send(ex, 400, java.util.Map.of("error", "Invalid request body: " + e.getMessage()));
            }
        }

        private void handleDelete(HttpExchange ex, long id) throws IOException {
            if (SERVICE.findById(id) == null) {
                send(ex, 404, java.util.Map.of("error", "Product not found"));
                return;
            }
            SERVICE.deleteById(id);
            send(ex, 200, java.util.Map.of("message", "Deleted"));
        }
    }
}
