package http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enteties.Product;
import org.junit.jupiter.api.*;
import utils.DataBase;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StoreHttpServerTest {
    private static final String BASE = "http://localhost:8080";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    private static String token;
    private static long createdId;

    @BeforeAll
    static void startServer() throws Exception {
        DataBase.getProductService().clearAll();
        StoreHttpServer.start();
        Thread.sleep(300);
    }

    @AfterAll
    static void cleanup() {
        DataBase.getProductService().clearAll();
    }

    //POST login
    @Test @Order(1)
    void regularLogin() throws Exception {
        var resp = post("/login", """
                {"login":"kitty","password":"cat"}""", null);
        assertEquals(200, resp.statusCode());
        JsonObject body = JsonParser.parseString(resp.body()).getAsJsonObject();
        assertTrue(body.has("token"), "Response should contain 'token'");
        token = body.get("token").getAsString();
        assertFalse(token.isBlank());
    }

    @Test @Order(2)
    void wrongLogin() throws Exception {
        var resp = post("/login", """
                {"login":"nokitty","password":"skrksrkrkrkrk"}""", null);
        assertEquals(401, resp.statusCode());
    }

    @Test @Order(3)
    void wrongPassword() throws Exception {
        var resp = post("/login", """
                {"login":"kitty","password":"skrksrkrkrkrk"}""", null);
        assertEquals(401, resp.statusCode());
    }

    @Test @Order(4)
    void noToken() throws Exception {
        var resp = get("/products/1", null);
        assertEquals(401, resp.statusCode());
    }

    @Test @Order(5)
    void wrongToken() throws Exception {
        var resp = get("/products/1", "ssksk.sksksk.skskks");
        assertEquals(401, resp.statusCode());
    }

    //PUT /products

    @Test @Order(10)
    void createProduct() throws Exception {
        ensureToken();
        String body = GSON.toJson(new Product("Pony", "Animals", 100, 1.5));
        var resp = put("/products", body, token);
        assertEquals(201, resp.statusCode());
        JsonObject json = parseJson(resp);
        assertTrue(json.get("id").getAsLong() > 0);
        createdId = json.get("id").getAsLong();
        assertEquals("Pony", json.get("name").getAsString());
        assertEquals("Animals", json.get("category").getAsString());
    }

    @Test @Order(11)
    void duplicateName() throws Exception {
        ensureToken();
        String body = GSON.toJson(new Product("Pony", "Animals", 10, 3.0));
        var resp = put("/products", body, token);
        assertEquals(409, resp.statusCode());
    }


    @Test @Order(20)
    void getById() throws Exception {
        ensureToken();
        ensureCreatedId();
        var resp = get("/products/" + createdId, token);
        assertEquals(200, resp.statusCode());
        JsonObject json = parseJson(resp);
        assertEquals(createdId, json.get("id").getAsLong());
        assertEquals("Pony", json.get("name").getAsString());
    }

    @Test @Order(21)
    void getByIdThatDoesntExist() throws Exception {
        ensureToken();
        var resp = get("/products/99999", token);
        assertEquals(404, resp.statusCode());
    }

    //POST /products/{id}

    @Test @Order(30)
    void updateProduct() throws Exception {
        ensureToken();
        ensureCreatedId();
        String body = GSON.toJson(new Product(createdId, "UpdatedPony", "Animals", 200, 2.5));
        var resp = post("/products/" + createdId, body, token);
        assertEquals(200, resp.statusCode());
        JsonObject json = parseJson(resp);
        assertEquals("UpdatedPony", json.get("name").getAsString());
        assertEquals(200, json.get("quantity").getAsInt());
        assertEquals(2.5, json.get("price").getAsDouble(), 0.001);
    }

    @Test @Order(31)
    void updateProductThatDoesntExist() throws Exception {
        ensureToken();
        String body = GSON.toJson(new Product("6", "7", 1, 1.0));
        var resp = post("/products/99999", body, token);
        assertEquals(404, resp.statusCode());
    }


    @Test @Order(40)
    void deleteProduct() throws Exception {
        ensureToken();
        ensureCreatedId();
        var resp = delete("/products/" + createdId, token);
        assertEquals(200, resp.statusCode());

        var check = get("/products/" + createdId, token);
        assertEquals(404, check.statusCode());
    }

    @Test @Order(41)
    void deleteProductThatDoesntExist() throws Exception {
        ensureToken();
        var resp = delete("/products/67", token);
        assertEquals(404, resp.statusCode());
    }

//просто допоміжні методи
    private void ensureToken() throws Exception {
        if (token == null) regularLogin();
    }

    private void ensureCreatedId() throws Exception {
        if (createdId == 0) createProduct();
    }

    private JsonObject parseJson(HttpResponse<String> r) {
        return JsonParser.parseString(r.body()).getAsJsonObject();
    }

    private HttpResponse<String> get(String path, String t) throws Exception {
        var b = HttpRequest.newBuilder(URI.create(BASE + path)).GET();
        if (t != null) b.header("Authorization", "Bearer " + t);
        return CLIENT.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String body, String t) throws Exception {
        var b = HttpRequest.newBuilder(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (t != null) b.header("Authorization", "Bearer " + t);
        return CLIENT.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> put(String path, String body, String t) throws Exception {
        var b = HttpRequest.newBuilder(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body));
        if (t != null) b.header("Authorization", "Bearer " + t);
        return CLIENT.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> delete(String path, String t) throws Exception {
        var b = HttpRequest.newBuilder(URI.create(BASE + path)).DELETE();
        if (t != null) b.header("Authorization", "Bearer " + t);
        return CLIENT.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }
}
