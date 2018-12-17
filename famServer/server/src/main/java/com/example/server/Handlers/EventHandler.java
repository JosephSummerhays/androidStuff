package com.example.server.Handlers;

import com.example.server.DAO.DAO;
import com.example.server.Models.EventModel;
import com.example.server.Models.LoadModel;
import com.example.server.Models.PersonModel;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class EventHandler implements HttpHandler {
    DAO d;
    public EventHandler() {
        d = new DAO();
    }
    @Override
    public void handle(HttpExchange x) throws IOException {
        String auth = x.getRequestHeaders().getFirst("Authorization");
        if (d.authorize(auth)) {
            String path = x.getRequestURI().getPath();
            path = path.substring(1);
            if (path.lastIndexOf("/") != -1) {
                String eventID = path.substring(path.lastIndexOf("/") + 1);
                EventModel response = d.getEvent(eventID,auth);
                x.sendResponseHeaders(200,0);
                OutputStreamWriter resp = new OutputStreamWriter(x.getResponseBody());
                if (response == null) {
                    resp.write("{\"Message\":\"No Such Event or you don't have access to it\"}");
                }
                else {
                    resp.write(response.toString());
                }
                resp.close();
                x.close();
            } else {
                LoadModel response = d.getAllEvents(auth);
                x.sendResponseHeaders(200,0);
                OutputStreamWriter resp = new OutputStreamWriter(x.getResponseBody());
                resp.write("{\"data\":"+response.eventToString()+"}");
                resp.close();
                x.close();
            }
        }
        else {
            x.sendResponseHeaders(400,0);
            OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
            response.write("{\n\t\"message\":\"bad auth_token\"\n}");
            response.close();
            x.close();
        }
    }
}
