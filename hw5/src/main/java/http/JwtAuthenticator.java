package http;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import java.util.List;

public class JwtAuthenticator extends Authenticator {
    private static final String SECRET = "kittykittysksks";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    public static String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .sign(ALGORITHM);
    }

    private static String verify(String token) {
        try {
            DecodedJWT jwt = JWT.require(ALGORITHM).build().verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

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

        String subject = verify(parts[1]);
        if (subject == null) {
            return new Failure(401);
        }

        return new Success(new HttpPrincipal(subject, "USER"));
    }
}
