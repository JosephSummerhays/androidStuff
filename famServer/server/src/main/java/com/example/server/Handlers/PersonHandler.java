package com.example.server.Handlers;

import com.example.server.DAO.DAO;
import com.example.server.Models.LoadModel;
import com.example.server.Models.PersonModel;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class PersonHandler implements HttpHandler {
    DAO d;
    public PersonHandler() {
        try {
            d = new DAO();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void handle(HttpExchange x) throws IOException {
        String auth = x.getRequestHeaders().getFirst("Authorization");
        if (d.authorize(auth)) {
            String path = x.getRequestURI().getPath();
            path = path.substring(1);
            if (path.lastIndexOf("/") != -1) {
                String personID = path.substring(path.lastIndexOf("/") + 1);
                PersonModel response = d.getPerson(personID,auth);
                x.sendResponseHeaders(200,0);
                OutputStreamWriter resp = new OutputStreamWriter(x.getResponseBody());
                if (response == null) {
                    resp.write("{\"Message\":\"No Such Person, or you Don't have access\"}");
                }
                else {
                    resp.write(response.toString());
                }
                resp.close();
                x.close();
            }
            else {
                LoadModel response = d.getAllPeople(auth);
                x.sendResponseHeaders(200,0);
                OutputStreamWriter resp = new OutputStreamWriter(x.getResponseBody());
                resp.write("{\"data\":"+response.personToString()+"}");
                resp.close();
                x.close();
            }
        }
        else {
            x.sendResponseHeaders(400,0);
            OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
            response.write("{\"message\":\"bad auth_token\"}");
            response.close();
            x.close();
        }
    }
}
