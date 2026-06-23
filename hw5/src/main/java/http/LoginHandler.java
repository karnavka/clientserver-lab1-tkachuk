package http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.DataBase;

import java.io.IOException;
import java.util.Map;

public class LoginHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            StoreHttpServer.send(ex, 405, Map.of("error", "Method Not Allowed"));
            return;
        }
        try {
            JsonObject body = JsonParser.parseString(StoreHttpServer.readBody(ex)).getAsJsonObject();
            String login = body.get("login").getAsString();
            String password = body.get("password").getAsString();

            if (!DataBase.getUserService().authenticate(login, password)) {
                StoreHttpServer.send(ex, 401, Map.of("error", "Invalid credentials"));
                return;
            }

            StoreHttpServer.send(ex, 200, Map.of("token", JwtAuthenticator.generateToken(login)));
        } catch (Exception e) {
            StoreHttpServer.send(ex, 400, Map.of("error", "Invalid request body"));
        }
    }
}
