package ua.edu.duit.medical.web;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import ua.edu.duit.medical.util.Json;

public final class Responses {
    private Responses() {
    }

    public static void json(HttpExchange exchange, int statusCode, Object body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        send(exchange, statusCode, Json.stringify(body));
    }

    public static void send(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body == null ? new byte[0] : body.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream output = exchange.getResponseBody();
        try {
            output.write(bytes);
        } finally {
            output.close();
        }
    }
}

