package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enteties.Product;
import service.ProductService;
import utils.DataBase;

import java.io.IOException;
import java.util.Map;

public class ProductHandler implements HttpHandler {
    private static final Gson GSON = new Gson();
    private static final ProductService SERVICE = DataBase.getProductService();

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
                StoreHttpServer.send(ex, 405, Map.of("error", "Method Not Allowed"));
            }
        } catch (NumberFormatException e) {
            StoreHttpServer.send(ex, 400, Map.of("error", "Invalid product id"));
        }
    }

    private void handleGet(HttpExchange ex, long id) throws IOException {
        Product p = SERVICE.findById(id);
        if (p == null) {
            StoreHttpServer.send(ex, 404, Map.of("error", "Product not found"));
        } else {
            StoreHttpServer.send(ex, 200, p);
        }
    }

    private void handleCreate(HttpExchange ex) throws IOException {
        try {
            Product p = GSON.fromJson(StoreHttpServer.readBody(ex), Product.class);
            if (p == null || p.getName() == null || p.getName().isBlank()) {
                StoreHttpServer.send(ex, 400, Map.of("error", "Name is required"));
                return;
            }
            if (SERVICE.findByName(p.getName()) != null) {
                StoreHttpServer.send(ex, 409, Map.of("error", "Product with this name already exists"));
                return;
            }
            StoreHttpServer.send(ex, 201, SERVICE.create(p));
        } catch (Exception e) {
            StoreHttpServer.send(ex, 400, Map.of("error", "Invalid request body: " + e.getMessage()));
        }
    }

    private void handleUpdate(HttpExchange ex, long id) throws IOException {
        try {
            if (SERVICE.findById(id) == null) {
                StoreHttpServer.send(ex, 404, Map.of("error", "Product not found"));
                return;
            }
            Product p = GSON.fromJson(StoreHttpServer.readBody(ex), Product.class);
            if (p == null) {
                StoreHttpServer.send(ex, 400, Map.of("error", "Invalid request body"));
                return;
            }
            p.setId(id);
            SERVICE.update(p);
            StoreHttpServer.send(ex, 200, SERVICE.findById(id));
        } catch (Exception e) {
            StoreHttpServer.send(ex, 400, Map.of("error", "Invalid request body: " + e.getMessage()));
        }
    }

    private void handleDelete(HttpExchange ex, long id) throws IOException {
        if (SERVICE.findById(id) == null) {
            StoreHttpServer.send(ex, 404, Map.of("error", "Product not found"));
            return;
        }
        SERVICE.deleteById(id);
        StoreHttpServer.send(ex, 200, Map.of("message", "Deleted"));
    }
}
