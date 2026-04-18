import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) throws IOException {
        // Create an HTTP server listening on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        
        // Context to serve static files (HTML, CSS, JS, Images)
        server.createContext("/", new StaticFileHandler());
        
        // Context to handle form submissions
        server.createContext("/submit-form", new FormHandler());
        
        server.setExecutor(null); // creates a default executor
        System.out.println("Starting Java HTTP Server for NHS Trading Company...");
        System.out.println("Visit http://localhost:8000");
        server.start();
    }

    // Handler for serving static files
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String path = t.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            // Remove leading slash to make it a relative path
            Path filePath = Paths.get("." + path);
            
            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                String contentType = "text/html";
                if (path.endsWith(".css")) contentType = "text/css";
                else if (path.endsWith(".js")) contentType = "application/javascript";
                else if (path.endsWith(".png")) contentType = "image/png";
                
                t.getResponseHeaders().add("Content-Type", contentType);
                t.sendResponseHeaders(200, Files.size(filePath));
                OutputStream os = t.getResponseBody();
                Files.copy(filePath, os);
                os.close();
            } else {
                String response = "404 (Not Found)\n";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    // Handler for the contact form
    static class FormHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("POST".equals(t.getRequestMethod())) {
                InputStream is = t.getRequestBody();
                Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
                String body = scanner.hasNext() ? scanner.next() : "";
                
                System.out.println("Received form submission:");
                System.out.println(body);
                
                String response = "<html><head><title>Success</title><style>body{font-family:sans-serif; text-align:center; padding:50px;}</style></head><body>" +
                                  "<h1 style=\"color: #635BFF;\">Thank you for contacting NHS Trading!</h1>" +
                                  "<p>We have received your inquiry. (Processed by Java Server)</p>" +
                                  "<a href=\"/\" style=\"display:inline-block; margin-top:20px; padding:10px 20px; background:#0A2540; color:white; text-decoration:none; border-radius:5px;\">Return Home</a>" +
                                  "</body></html>";
                                  
                t.getResponseHeaders().add("Content-Type", "text/html");
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                t.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }
}
