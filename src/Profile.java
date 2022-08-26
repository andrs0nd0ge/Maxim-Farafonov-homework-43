import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Profile {
    static Charset charset = StandardCharsets.UTF_8;
    static void handleProfileRequest(HttpExchange xchange) {
        try {
            xchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");

            int response = 1;
            int length = 0;
            xchange.sendResponseHeaders(response, length);

            try (PrintWriter writer = getWriterFrom(xchange)) {
                String method = xchange.getRequestMethod();
                URI uri = xchange.getRequestURI();
                String ctxPath = xchange.getHttpContext().getPath();

                print(writer, "HTTP Method", method);
                print(writer, "Request", uri.toString());
                print(writer, "Processed via", ctxPath);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void print(PrintWriter writer, String msg, String method) {
        String data = String.format("%s: %s%n%n", msg, method);

        try {
            writer.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PrintWriter getWriterFrom(HttpExchange xchange) {
        OutputStream output = xchange.getResponseBody();
        return new PrintWriter(output, false, charset);
    }
}
