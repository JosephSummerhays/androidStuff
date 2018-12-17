package com.example.server.Handlers;

import com.example.server.DAO.DAO;
import com.example.server.DAO.DatabaseException;
import com.example.server.Models.UserModel;
import com.example.server.Models.registerSuccessModel;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.google.gson.*;

public class RegisterHandler implements HttpHandler {
    DAO d;
    Gson g;
    public RegisterHandler() {
        g = new Gson();
        try {
            d = new DAO();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void handle(HttpExchange x) throws IOException {
        if (x.getRequestMethod().toLowerCase().equals("post")) {
            UserModel newUser = g.fromJson(new InputStreamReader(x.getRequestBody()), UserModel.class);
            String gender = newUser.getGender().toLowerCase();
            if (!(gender.equals("f")||gender.equals("m"))) {
                x.sendResponseHeaders(400, 0);
                OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
                response.write("{\"message\":\"gender must be a single char (M/F)\"}");
                response.close();
                x.close();
                return;
            }
            registerSuccessModel treeRoot;
            try {
                treeRoot = d.registerUser(newUser);
                x.sendResponseHeaders(200,0);
                OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
                response.write(treeRoot.toString());
                response.close();
                x.close();
            } catch (DatabaseException e) {
                x.sendResponseHeaders(400, 0);
                OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
                response.write("{ \"message\":\"" + e.getMessage() + "\" }");
                response.close();
                x.close();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else {
            x.sendResponseHeaders(400,0);
            x.close();
        }
    }
}
