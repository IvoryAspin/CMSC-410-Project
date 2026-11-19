import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Properties;

public class Main {

    public static Properties config = new Properties();

    public static void main(String[] args) throws Exception {

        try (FileInputStream file =
                     new FileInputStream("config.properties")) {

            config.load(file);
        }

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        // NASA endpoint
        server.createContext("/api/apod", exchange -> {

            String date = getDate(exchange);

            try {
                String result = NasaService.getPicture(date);

                sendResponse(exchange, 200, result);

            } catch (Exception e) {

                sendResponse(exchange, 500,
                        "{\"error\":\"NASA request failed\"}");
            }
        });

        // Google Calendar endpoint
        server.createContext("/api/events", exchange -> {

            String date = getDate(exchange);

            try {
                String result = CalendarService.getEvents(date);

                sendResponse(exchange, 200, result);

            } catch (Exception e) {

                sendResponse(exchange, 500,
                        "{\"error\":\"Calendar request failed\"}");
            }
        });

        // Frontend files
        server.createContext("/", exchange -> {

            String path = exchange.getRequestURI().getPath();

            if (path.equals("/")) {
                path = "/index.html";
            }

            Path file = Paths.get("public" + path).normalize();

            if (!file.startsWith(Paths.get("public").toAbsolutePath())
                    && file.isAbsolute()) {
                sendResponse(exchange, 403, "Forbidden");
                return;
            }

            File requestedFile = file.toFile();

            if (!requestedFile.exists() || !requestedFile.isFile()) {
                sendResponse(exchange, 404, "File not found");
                return;
            }

            String contentType = "text/plain";

            if (path.endsWith(".html")) {
                contentType = "text/html";
            } else if (path.endsWith(".css")) {
                contentType = "text/css";
            } else if (path.endsWith(".js")) {
                contentType = "application/javascript";
            }

            exchange.getResponseHeaders().set(
                    "Content-Type", contentType + "; charset=UTF-8");

            byte[] content = Files.readAllBytes(requestedFile.toPath());

            exchange.sendResponseHeaders(200, content.length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(content);
            }
        });

        server.start();

        System.out.println("--------------------------------");
        System.out.println("NASA CALENDAR DASHBOARD STARTED");
        System.out.println("--------------------------------");
        System.out.println("Open: http://localhost:8080");
    }

    public static String getDate(HttpExchange exchange) {

        String query = exchange.getRequestURI().getQuery();

        if (query != null) {

            for (String parameter : query.split("&")) {

                String[] parts = parameter.split("=", 2);

                if (parts.length == 2 &&
                        parts[0].equals("date")) {

                    try {
                        return URLDecoder.decode(
                                parts[1], StandardCharsets.UTF_8);
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        return LocalDate.now().toString();
    }

    public static void sendResponse(
            HttpExchange exchange,
            int status,
            String response) throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*");

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}