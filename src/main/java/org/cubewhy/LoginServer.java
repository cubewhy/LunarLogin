package org.cubewhy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.cubewhy.utils.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class LoginServer {
    private static LoginServer instance;
    public final HttpServer server;

    private LoginServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 0);
        this.server.createContext("/login", this::handleLogin);
        this.server.createContext("/", this::handle);
        this.server.createContext("/en", this::handleEnglish);
    }

    public static LoginServer getInstance() throws IOException {
        if (instance == null) {
            instance = new LoginServer();
        }
        return instance;
    }

    private static String getRequestParam(HttpExchange httpExchange) throws IOException {
        String paramStr;

        if (httpExchange.getRequestMethod().equals("GET")) {
            paramStr = httpExchange.getRequestURI().getQuery();
        } else {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8));
            StringBuilder requestBodyContent = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                requestBodyContent.append(line);
            }
            paramStr = requestBodyContent.toString();
        }

        return paramStr;
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        String param = getRequestParam(exchange);
        if (param == null || !param.startsWith("url=")) {
            write(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "BAD REQUEST");
            return;
        }
        String url = param.substring(4); // token url
        if (url.startsWith("https://login.live.com/oauth20_desktop.srf?code=")) {
            LunarLogin.service.addAccount(url);
            System.out.println("Add successful " + url);
            writeOk(exchange, "Token added, Click login in client to use");
        } else {
            System.out.println("Add failed " + url);
            write(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "NOT A LOGIN URL");
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        writeOk(exchange, LunarLogin.loginIndex); // Main page
    }

    private void handleEnglish(HttpExchange exchange) throws IOException {
        writeOk(exchange, LunarLogin.loginIndexEN); // Main page
    }

    private void writeOk(HttpExchange httpExchange, String response) throws IOException {
        write(httpExchange, HttpURLConnection.HTTP_OK, response);
    }

    private void write(HttpExchange httpExchange, int code, String response) throws IOException {
        httpExchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);

        OutputStream responseBody = httpExchange.getResponseBody();
        OutputStreamWriter writer = new OutputStreamWriter(responseBody, StandardCharsets.UTF_8);
        writer.write(response);
        writer.close();
        responseBody.close();
    }

    public void startServer() {
        this.server.start(); // start the server
    }

    public void stopServer() {
        this.server.stop(0); // terminate the server
    }
}
