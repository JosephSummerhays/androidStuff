package com.example.joseph.famserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

interface canGetServerResponse {
    void getServerResponse(serverCall s, final int endPoint);
}

public class serverCall extends Thread {
    private canGetServerResponse parent;
    private URL url;
    private String json;
    private String method;
    private String auth;
    private String responseBody;
    boolean err;
    private int code;
    public serverCall(canGetServerResponse parent, URL url, String json, String method, String auth, int code) {
        this.parent = parent;
        this.url = url;
        this.json = json;
        this.method = method;
        this.auth = auth;
        this.code = code;
        err = false;
    }
    private serverCall(URL url, String json, String method) {
        this.url = url;
        this.json = json;
        this.method = method;
        err = false;
    }
    @Override
    public void run() {
        try {
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput( json != null );
            if (auth!=null) { conn.addRequestProperty("Authorization",auth); }
            if (json != null) {
                OutputStream out = conn.getOutputStream();
                out.write(json.getBytes());
            }
            conn.connect();
            InputStream respBody;
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Get the input stream containing the HTTP response body
                respBody = conn.getInputStream();
            }
            else {
                // The HTTP response status code indicates an error
                err = true;
                respBody = conn.getErrorStream();
            }
            StringBuilder responseBuidler = new StringBuilder();
            InputStreamReader sr = new InputStreamReader(respBody);
            char[] buf = new char[1024];
            int len;
            while ((len = sr.read(buf)) > 0) {
                responseBuidler.append(buf, 0, len);
            }
            responseBody = responseBuidler.toString();
        } catch (IOException e) {
            e.printStackTrace();
            responseBody = null;
        }
        if (parent!= null) {
            parent.getServerResponse(this, code);
        }
    }
    public String getResponseBody() {
        return responseBody;
    }
    public static void main(String[] args) throws MalformedURLException {
        serverCall s = new serverCall(new URL("http://localhost:8000/"), null, "GET");
        s.run();
    }
}
