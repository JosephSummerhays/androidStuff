package com.example.server.DAO;

import com.example.server.Models.EventModel;
import com.example.server.Models.LoadModel;
import com.example.server.Models.PersonModel;
import com.example.server.Models.UserModel;
import com.example.server.Models.registerSuccessModel;
import com.google.gson.Gson;

import org.junit.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;

public class DAOTest {
    DAO d;
    Gson g;
    LoadModel load;
    LoadModel badLoad;
    UserModel user;
    UserModel BadUser;
    UserModel BadPassword;
    UserModel firstUser;
    PersonModel firstPerson;
    EventModel firstEvent;
    public DAOTest() {
        d = new DAO();
        g = new Gson();
        try {
            load = g.fromJson(new FileReader(new File("src/main/java/json/example.json")), LoadModel.class);
            badLoad = g.fromJson(new FileReader(new File("src/main/java/json/BadExample.json")), LoadModel.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        user = new UserModel("James","123","email",
                "James","Jameson","m","jj");
        BadUser = new UserModel("Jmes","123","email",
                "James","Jameson","m","jj");
        BadPassword = new UserModel("James","133","email",
                "James","Jameson","m","jj");
        firstUser = load.getUsers()[0];
        firstPerson = load.getPersons()[0];
        firstEvent = load.getEvents()[0];
        createTables();
    }
    @org.junit.Before
    public void setUp() {
    }
    @org.junit.After
    public void tearDown() {
        createTables();
    }
    @org.junit.Test
    public void createTables() {
        FileNotFoundException fout = null;
        DatabaseException dout = null;
        try {
            d.createTables();
        } catch (FileNotFoundException e) {
            fout = e;
        } catch (DatabaseException e) {
            dout = e;
        }
        assertTrue(fout == null && dout ==null);
    }
    @org.junit.Test
    public void registerUser() {
        DatabaseException dout = null;
        registerSuccessModel r = null;
        try {
            r = d.registerUser(user);
        } catch (DatabaseException e) {
            dout = e;
        }
        assertTrue(r.getUserName().equals(user.getUserName()));
        assertTrue(r.getPersonID()!=null);
        assertTrue(r.getAuthToken().equals(d.getAuthToken(user.getUserName())));
        assertTrue(r.getMessage()==null);
        assertTrue(dout == null);
    }
    @org.junit.Test
    public void registerUserNeg() {
        DatabaseException dout = null;
        registerUser();
        try {
            d.registerUser(user);
        } catch (DatabaseException e) {
            dout = e;
        }
        assertTrue(dout != null);
    }
    @org.junit.Test
    public void getUser() {
        createTables();
        registerUser();
        UserModel u = d.getUser(user.getUserName());
        assertTrue(u.equals(user));
    }
    @org.junit.Test
    public void getUserNeg() {
        createTables();
        registerUser();
        UserModel u = d.getUser("Jmes");

        assertTrue(u==null);
    }
    @org.junit.Test
    public void fill() {
        DatabaseException dout = null;
        registerUser();
        String id = UUID.randomUUID().toString();
        try {
            d.fill(user,2,id);
        } catch (DatabaseException e) {
            dout = e;
        }
        assertTrue(dout == null);
        assertTrue(d.getUser(user.getUserName()).equals(user));
    }
    @org.junit.Test
    public void fillNeg() {
        DatabaseException dout = null;
        registerUser();
        String id = UUID.randomUUID().toString();
        try {
            d.fill(BadUser,3,id);
        } catch (DatabaseException e) {
            dout = e;
        }

        assertTrue(dout != null);
    }
    @org.junit.Test
    public void login() {
        registerUser();
        registerSuccessModel r = d.login(user);
        assertTrue(r.getMessage()==null);
        assertTrue(r.getUserName().equals(user.getUserName()));
        assertTrue(r.getAuthToken().equals(d.getAuthToken(user.getUserName())));
    }
    @org.junit.Test
    public void loginNeg() {
        createTables();
        registerUser();
        registerSuccessModel r1 = d.login(BadUser);
        registerSuccessModel r2 = d.login(BadPassword);
        assertTrue(r1.getMessage()!=null);
        assertTrue(r2.getMessage()!=null);
    }
    @org.junit.Test
    public void load() {
        DatabaseException dout = null;
        try {
            d.load(load);
        } catch (DatabaseException e) {
            dout = e;
        }
        assertTrue(dout==null);
        UserModel u = d.getUser(firstUser.getUserName());
        assertTrue(u.equals(firstUser));
        PersonModel p = d.getPerson(u.getPerson(),d.getAuthToken(u.getUserName()));
        assertTrue(p.equals(firstPerson));
    }
    @org.junit.Test
    public void loadNeg() {
        DatabaseException dout = null;
        try {
            d.load(badLoad);
        } catch (DatabaseException e) {
            dout = e;
        }
        assertTrue(dout!=null);
    }
    @org.junit.Test
    public void getPerson() {
        load();
        PersonModel p = d.getPerson(firstPerson.getPersonID(),d.getAuthToken(firstPerson.getDescendant()));
        assertTrue(p.equals(firstPerson));
    }
    @org.junit.Test
    public void getPersonFail() {
        load();
        registerUser();
        PersonModel p = d.getPerson(firstPerson.getPersonID(),d.getAuthToken(user.getUserName()));
        assertTrue(p==null);
    }
    @org.junit.Test
    public void authorize() {
        registerSuccessModel r = null;
        try {
            r = d.registerUser(user);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        assertTrue(d.authorize(r.getAuthToken()));
    }
    @org.junit.Test
    public void authorizeNeg() {
        assertTrue(!d.authorize(UUID.randomUUID().toString()));
        assertTrue(!d.authorize(""));
    }
    @org.junit.Test
    public void getAllPeople() {
        load();
        LoadModel l = d.getAllPeople(d.getAuthToken(firstUser.getUserName()));
        assertTrue(Arrays.equals(l.getPersons(),load.getPersons()));
    }
    @org.junit.Test
    public void getAllPeopleNeg() {
        load();
        LoadModel l = d.getAllPeople("");
        assertTrue(l.equals(new LoadModel()));
    }
    @org.junit.Test
    public void getEvent() {
        load();
        EventModel e = d.getEvent(firstEvent.getEventID(),d.getAuthToken(firstUser.getUserName()));
        assertTrue(e.equals(firstEvent));
    }
    @org.junit.Test
    public void getEventNeg() {
        load();
        EventModel e = d.getEvent(firstEvent.getEventID(),d.getAuthToken(user.getUserName()));
        assertTrue(e==null);
    }
    @org.junit.Test
    public void getAllEvents() {
        load();
        LoadModel e = d.getAllEvents(d.getAuthToken(firstUser.getUserName()));
        assertTrue(Arrays.equals(e.getEvents(), load.getEvents()));
    }
    @org.junit.Test
    public void getAllEventsNeg() {
        load();
        LoadModel e = d.getAllEvents("");
        assertTrue(e.equals(new LoadModel()));
    }
}