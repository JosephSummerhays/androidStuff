package com.example.joseph.famserver.Models;

import com.google.gson.Gson;

import java.util.Arrays;

public class LoadModel extends JsonModel {
    public UserModel[] getUsers() { return users; }
    public PersonModel[] getPersons() { return persons; }
    public EventModel[] getEvents() { return events; }
    public String getMessage() { return Message; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadModel loadModel = (LoadModel) o;
        return Arrays.equals(getUsers(), loadModel.getUsers()) &&
                Arrays.equals(getPersons(), loadModel.getPersons()) &&
                Arrays.equals(getEvents(), loadModel.getEvents()) &&
                getMessage().equals(loadModel.getMessage());
    }

    public UserModel users[];
    public PersonModel persons[];
    public EventModel events[];
    String Message;
    public LoadModel() {
        users = new UserModel[0];
        persons = new PersonModel[0];
        events = new EventModel[0];
    }
    public LoadModel(String s) {
        Message = s;
    }
    public static EventModel[] pushEvent(EventModel[] events, EventModel toPush) {
        EventModel[] longer = new EventModel[events.length + 1];
        System.arraycopy(events, 0, longer, 0, events.length);
        longer[events.length] = toPush;
        return longer;
    }
    public String personToString() {
        Gson g = new Gson();
        return g.toJson(persons);
    }
    public String eventToString() {
        Gson g = new Gson();
        return g.toJson(events);
    }
    public static PersonModel[] pushPerson(PersonModel[] persons, PersonModel toPush) {
        PersonModel[] longer = new PersonModel[persons.length + 1];
        System.arraycopy(persons, 0, longer, 0, persons.length);
        longer[persons.length] = toPush;
        return longer;
    }
}
