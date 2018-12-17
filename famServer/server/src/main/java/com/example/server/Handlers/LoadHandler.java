package com.example.server.Handlers;

import com.example.server.DAO.DAO;
import com.example.server.DAO.DatabaseException;
import com.example.server.Models.LoadModel;
import com.example.server.Models.UserModel;
import com.example.server.Models.registerSuccessModel;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LoadHandler implements HttpHandler {
    DAO d;
    Gson g;
    public LoadHandler() {
        try {
            d = new DAO();
        } catch (Exception e) {
            e.printStackTrace();
        }
        g = new Gson();
    }
    @Override
    public void handle(HttpExchange x) throws IOException {
        LoadModel toLoad = g.fromJson(new InputStreamReader(x.getRequestBody()), LoadModel.class);
        try {
            d.load(toLoad);

            x.sendResponseHeaders(200, 0);
            OutputStreamWriter responseBody = new OutputStreamWriter(x.getResponseBody());
            String tmp = "{\"message\":\"Successfully added "+toLoad.users.length+" users, "+toLoad.persons.length+" persons, "+toLoad.events.length+" events to the database.\"}";
            responseBody.write(tmp);
            responseBody.close();
            x.close();
        }
        catch (Exception e) {
            x.sendResponseHeaders(400, 0);
            OutputStreamWriter responseBody = new OutputStreamWriter(x.getResponseBody());
            responseBody.write("{\"message\":\"Bad Input\"}");
            responseBody.close();
            x.close();
        } catch (DatabaseException e) {
            e.printStackTrace();
            x.sendResponseHeaders(500, 0);
            OutputStreamWriter responseBody = new OutputStreamWriter(x.getResponseBody());
            responseBody.write("{\"message\":\""+e.getMessage()+"\"}");
            responseBody.close();
            x.close();
        }
    }
}
