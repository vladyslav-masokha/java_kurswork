package ua.edu.duit.medical.web;

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Requests {
    private Requests() {
    }

    public static String readBody(HttpExchange exchange) throws IOException {
        InputStream input = exchange.getRequestBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int read;
        while ((read = input.read(chunk)) != -1) {
            buffer.write(chunk, 0, read);
        }
        return new String(buffer.toByteArray(), "UTF-8");
    }
}

