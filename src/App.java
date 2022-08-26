import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class App {
    static Charset charset = StandardCharsets.UTF_8;
    static void handleAppsRequest(HttpExchange xchange) {
        try {
            xchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");

            int response = 1;
            int length = 0;
            xchange.sendResponseHeaders(response, length);

            try (PrintWriter writer = getWriterFrom(xchange)) {
                String method = xchange.getRequestMethod();
                URI uri = xchange.getRequestURI();

                print(writer, "HTTP Method", method);
                print(writer, "Request", uri.toString());
                writeAppsHeaders(writer, xchange.getRequestHeaders());
                writeData(writer, xchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeAppsHeaders(PrintWriter writer, Headers headers) {
        print(writer, "Headers of the request", "");
        headers.forEach((k, v) -> print(writer, " > " + k, v.toString()));
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

    private static BufferedReader getReader(HttpExchange xchange) {
        InputStream input = xchange.getRequestBody();
        InputStreamReader isr = new InputStreamReader(input, charset);
        return new BufferedReader(isr);
    }

    private static void writeData(PrintWriter pw, HttpExchange xchange) {
        try (BufferedReader reader = getReader(xchange)) {
            if (!reader.ready()) return;
            print(pw, "HTTP Body", "");
            reader.lines().forEach(e -> print(pw, "  ", e));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
