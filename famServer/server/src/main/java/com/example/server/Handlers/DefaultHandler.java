package com.example.server.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DefaultHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange x) throws IOException {
        System.out.printf("GET request %s\n", x.getRequestURI());
        if (x.getRequestMethod().toLowerCase().equals("get")) {
            StringBuilder path = new StringBuilder (x.getRequestURI().getPath());
            if (path.toString().equals("/")) {
                path.append("index.html");
            }
            path.insert(0, "web");
            File responseFile = new File(path.toString());
            //System.out.printf("****%s\n", path.toString());
            if (responseFile.exists()) {
                //System.out.println("****file found!");
                x.sendResponseHeaders(200, 0);
                OutputStream response = x.getResponseBody();
                Files.copy(responseFile.toPath(), response);
                x.close();
            }
            else {
                System.out.println("****file not found!");
                x.sendResponseHeaders(404, -1);
            }
        }
        else {
            x.sendResponseHeaders(400,-1);
            x.close ();
        }
    }
}
