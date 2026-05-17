package ua.edu.duit.medical;

import java.util.concurrent.CountDownLatch;
import ua.edu.duit.medical.config.ApplicationFactory;
import ua.edu.duit.medical.web.ApiServer;

public final class Application {
    private Application() {
    }

    public static void main(String[] args) throws Exception {
        int port = resolvePort(args);
        ApiServer server = ApplicationFactory.createServer(port);
        server.start();
        System.out.println("МедНавігатор API запущено: http://localhost:" + port);
        System.out.println("Натисніть Ctrl+C для зупинки.");
        new CountDownLatch(1).await();
    }

    private static int resolvePort(String[] args) {
        if (args != null && args.length > 0) {
            return Integer.parseInt(args[0]);
        }
        String envPort = System.getenv("PORT");
        if (envPort != null && envPort.trim().length() > 0) {
            return Integer.parseInt(envPort.trim());
        }
        return 8080;
    }
}
