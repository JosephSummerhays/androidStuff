package com.example.joseph.famserver.Models;

public class RelationModel extends PersonModel {
    public RelationModel(PersonModel person, String relation) {
        super(person);
        this.relation = relation;
    }

    public String getRelation() {
        return relation;
    }

    private String relation;

}
