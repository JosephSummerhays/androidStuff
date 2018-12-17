package com.example.server.Handlers;

import com.example.server.DAO.DAO;
import com.example.server.DAO.DatabaseException;
import com.example.server.Models.UserModel;
import com.example.server.Models.registerSuccessModel;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class FillHandler implements HttpHandler {
    DAO d;
    public FillHandler() {
        try {
            d = new DAO();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void handle(HttpExchange x) throws IOException {
        StringBuilder path = new StringBuilder(x.getRequestURI().getPath());
        if (path.substring(0,5).equals("/fill")) {
            path.delete(0,5);
            if (path.length()!=0) {
                path.deleteCharAt(0);
                String userName;
                if (path.indexOf("/") != -1) {
                    userName = path.substring(0, path.indexOf("/"));
                    path.delete(0, path.indexOf("/") + 1);
                } else {
                    userName = path.toString();
                    path.delete(0,userName.length());
                }
                int generations = 4;
                if (path.length() != 0) {
                    generations = Integer.parseInt(path.toString());
                }
                if (generations >= 0 && generations <= 31) {
                    try {
                        String personID = UUID.randomUUID().toString();
                        UserModel userToFill = d.getUser(userName);
                        d.fill(userToFill, generations, personID);
                        int numPeep = (2<<generations) - 1;
                        int numEvents = (numPeep * 3)-2;
                        x.sendResponseHeaders(200, 0);
                        OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
                        response.write("{\"message\":\"successfully added "+Integer.toString(numPeep)+" persons and "+Integer.toString(numEvents)+" Events to the database.\"}");
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DatabaseException e) {
                        e.printStackTrace();
                        x.sendResponseHeaders(500, 0);

                        OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
                        response.write("{\"message\":\""+e.getMessage()+"\"}");
                        response.close();
                    }
                }
                else {
                    x.sendResponseHeaders(400,0);
                    OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
                    response.write("{\"message\":\"Generations must be in the range [0,31]\"}");
                    response.close();
                }
            }
            else {
                x.sendResponseHeaders(400,0);
                OutputStreamWriter response = new OutputStreamWriter(x.getResponseBody());
                response.write("{\"message\":\"Must specify User to Fill\"}");
                response.close();
            }
        }
        else {
            System.out.println("wha?!");
        }
        x.close();
    }
}
