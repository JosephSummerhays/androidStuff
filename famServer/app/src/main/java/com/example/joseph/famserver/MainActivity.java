package com.example.joseph.famserver;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.joseph.famserver.Models.EventsModel;
import com.example.joseph.famserver.Models.PeopleModel;
import com.example.joseph.famserver.Models.PersonModel;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;

import static com.example.joseph.famserver.StaticGlobals.authToken;
import static com.example.joseph.famserver.StaticGlobals.serverLoc;

public class MainActivity extends FragmentActivity implements canGetServerResponse {
    private Gson g;
    private LoginFragment login;
    private MapFragment map;

    private final int REQ_CODE_LOGIN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = new Gson();
        setContentView(R.layout.activity_main);
        if (authToken==null&&login==null) {
            FragmentManager fm = this.getSupportFragmentManager();
            login = new LoginFragment();
            login.setParent(this);
            fm.beginTransaction().add(R.id.LoginFrame,login).commit();
        }
        else {
            switchToMapActivity();
        }
    }
    public void setLoginCredentials(String serverLoc, String authToken, String username) {
        StaticGlobals.serverLoc = serverLoc;
        StaticGlobals.authToken = authToken;
        StaticGlobals.username = username;
    }
    public void getPeople() {
        try {
            serverCall s = new serverCall(this,new URL(StaticGlobals.serverLoc+"/person"),
                    null,"GET", StaticGlobals.authToken,1);
            s.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(this,"ERR: "+e.getMessage(),Toast.LENGTH_LONG);
        }
    }
    public void getEvents() {
        try {
            serverCall s = new serverCall(this,new URL(StaticGlobals.serverLoc+"/event"),
                    null,"GET", StaticGlobals.authToken,2);
            s.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(this,"ERR: "+e.getMessage(),Toast.LENGTH_LONG);
        }
    }
    @Override
    public void getServerResponse(final serverCall s, final int code) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (code) {
                    case 1: {
                        //response of person endpoint
                        StaticGlobals.people = g.fromJson(s.getResponseBody(),PeopleModel.class);
                        StringBuilder toToast = new StringBuilder();
                        for (PersonModel person : StaticGlobals.people.getData()) {
                            toToast.append(person.getFirstName());
                            toToast.append(" ");
                            toToast.append(person.getLastName());
                            toToast.append("\n");
                        }
                        Toast.makeText(MainActivity.this,toToast,Toast.LENGTH_LONG).show();
                        if (!(StaticGlobals.people == null || StaticGlobals.events == null)) {
                            switchToMapActivity();
                        }
                        break;
                    }
                    case 2: {
                        StaticGlobals.events = g.fromJson(s.getResponseBody(),EventsModel.class);
                        if (!(StaticGlobals.people == null || StaticGlobals.events == null)) {
                            switchToMapActivity();
                        }
                        break;
                    }
                }
            }
        });
    }

    private void switchToMapActivity() {
        FragmentManager fm = this.getSupportFragmentManager();
        map = new MapFragment();
        map.setParent(this);
        fm.beginTransaction().remove(login).
                add(R.id.mapFrame,map).commit();
    }
}
