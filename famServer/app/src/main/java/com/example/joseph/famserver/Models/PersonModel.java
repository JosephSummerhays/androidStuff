package com.example.joseph.famserver.Models;

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

    public PersonModel(PersonModel p) {
        this.firstName = p.firstName;
        this.lastName = p.lastName;
        this.gender = p.gender;
        this.personID = p.personID;
        this.father = p.father;
        this.mother = p.mother;
        this.spouse = p.spouse;
        this.descendant = p.descendant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonModel that = (PersonModel) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getGender().equals(that.getGender()) &&
                getPersonID().equals(that.getPersonID()) &&
                getFather().equals(that.getFather()) &&
                getMother().equals(that.getMother()) &&
                getSpouse().equals(that.getSpouse()) &&
                getDescendant().equals(that.getDescendant());
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
