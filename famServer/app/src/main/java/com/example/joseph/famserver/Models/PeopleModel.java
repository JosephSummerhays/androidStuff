package com.example.joseph.famserver.Models;

import com.google.gson.Gson;

import java.util.Arrays;

public class PeopleModel extends JsonModel {
    public PersonModel[] getPersons() { return data; }
    public String getMessage() { return Message; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeopleModel other = (PeopleModel) o;
        return Arrays.equals(getPersons(), other.getPersons()) &&
                getMessage().equals(other.getMessage());
    }
    public PersonModel data[];
    String Message;
    public PeopleModel() {
        data = new PersonModel[0];
    }
    public PeopleModel(String s) {
        Message = s;
    }
    public String personToString() {
        Gson g = new Gson();
        return g.toJson(data);
    }
    public static PersonModel[] pushPerson(PersonModel[] persons, PersonModel toPush) {
        PersonModel[] longer = new PersonModel[persons.length + 1];
        System.arraycopy(persons, 0, longer, 0, persons.length);
        longer[persons.length] = toPush;
        return longer;
    }

    public PersonModel[] getData() {
        return data;
    }
}
