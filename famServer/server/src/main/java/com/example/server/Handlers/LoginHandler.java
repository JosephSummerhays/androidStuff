package com.example.server.Handlers;

import com.example.server.DAO.DAO;
import com.example.server.Models.UserModel;
import com.example.server.Models.registerSuccessModel;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import sun.rmi.runtime.Log;

public class LoginHandler implements HttpHandler {
    DAO d;
    Gson g;
    public LoginHandler() {
        g = new Gson();
        try {
            d = new DAO();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void handle(HttpExchange x) throws IOException {
        try {
            UserModel user = g.fromJson(new InputStreamReader(x.getRequestBody()), UserModel.class);
            registerSuccessModel response = d.login(user);
            x.sendResponseHeaders(200,0);

            OutputStreamWriter responseBody = new OutputStreamWriter(x.getResponseBody());
            responseBody.write(response.toString());
            responseBody.close();
        } catch (IOException e) {
            e.printStackTrace();
            x.sendResponseHeaders(500,0);
        }
        x.close();
    }
}
