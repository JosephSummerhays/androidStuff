package com.example.server.Handlers;

import com.example.server.DAO.DAO;
import com.example.server.DAO.DatabaseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ClearHandler implements HttpHandler {
    DAO d;
    public ClearHandler() {
        try {
            d = new DAO();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void handle(HttpExchange x) throws IOException {
        try {
            d.createTables();
            x.sendResponseHeaders(200,0);
            OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
            response.write("{\"message\":\"Clear succeeded.\"}");
            response.close();
            x.close();
        } catch (Exception e) {
            e.printStackTrace();
            x.sendResponseHeaders(500,0);
            OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
            response.write(String.format("{\n\t\"message\":\"%s\"\n}", e.getMessage()));
            response.close();
            x.close();

        } catch (DatabaseException e) {
            x.sendResponseHeaders(500,0);
            OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
            response.write(String.format("{\n\t\"message\":\"%s\"\n}", e.getMessage()));
            response.close();
            x.close();
        }
    }
}
