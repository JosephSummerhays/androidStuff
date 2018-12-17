package com.example.server.Models;

import java.util.Objects;

public class PersonModel extends JsonModel {
    public PersonModel(String firstName, String lastName, String gender, String personID, String father, String mother, String spouse, String descendant) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
        this.father = father;
        this.mother = mother;
        this.spouse = spouse;
        this.descendant = descendant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonModel that = (PersonModel) o;
        return Objects.equals(getFirstName(), that.getFirstName()) &&
                Objects.equals(getLastName(), that.getLastName()) &&
                Objects.equals(getGender(), that.getGender()) &&
                Objects.equals(getPersonID(), that.getPersonID()) &&
                Objects.equals(getFather(), that.getFather()) &&
                Objects.equals(getMother(), that.getMother()) &&
                Objects.equals(getSpouse(), that.getSpouse()) &&
                Objects.equals(getDescendant(), that.getDescendant());
    }

    private String firstName;
    private String lastName;
    private String gender;
    private String personID;
    private String father;
    private String mother;
    private String spouse;
    private String descendant;

    public String getSpouse() {
        return spouse;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getGender() {
        return gender;
    }
    public String getPersonID() {
        return personID;
    }
    public String getFather() {
        return father;
    }
    public String getMother() {
        return mother;
    }
    public String getDescendant() {
        return descendant;
    }
//    public boolean hasEssentials() {
//        return (firstName!=null)&&(lastName!=null)&&(gender!=null)&&
//                (personID!=null)&&(descendant!=null);
//    }
}
