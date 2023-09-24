package util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class HttpUtil {
    public static String getQueryParam(HttpExchange exchange, int index) {
        String query = exchange.getRequestURI().getQuery();
        String[] params = query.split("&");
        return params[index].split("=")[1];
    }

    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
