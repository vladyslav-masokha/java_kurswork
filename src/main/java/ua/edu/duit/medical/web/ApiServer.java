package ua.edu.duit.medical.web;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ApiServer {
    private final int port;
    private final ApiHandler handler;
    private HttpServer server;
    private ExecutorService executor;

    public ApiServer(int port, ApiHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        executor = Executors.newFixedThreadPool(8);
        server.createContext("/", handler);
        server.setExecutor(executor);
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}

