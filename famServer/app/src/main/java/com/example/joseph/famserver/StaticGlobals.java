package com.example.joseph.famserver;

import android.util.EventLog;

import com.example.joseph.famserver.Models.EventModel;
import com.example.joseph.famserver.Models.EventsModel;
import com.example.joseph.famserver.Models.ItemModel;
import com.example.joseph.famserver.Models.PeopleModel;
import com.example.joseph.famserver.Models.PersonModel;
import com.example.joseph.famserver.Models.RelationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StaticGlobals {
    static {
        authToken = null;
        username = null;
        serverLoc = null;
        people = null;
        events = null;
    }
    private static boolean isToPersonInit = false;
    public static void initPersonIDToPerson () {
        if (isToPersonInit) return;
        else isToPersonInit = true;
        if (PersonIDToPerson == null) {
            PersonIDToPerson = new HashMap<>();
        }
        for (PersonModel p : StaticGlobals.people.getData()) {
            PersonIDToPerson.put(p.getPersonID(),p);
        }
    }
    private static boolean isToEventInit = false;
    public static void initEventIDToEvent () {
        if (isToEventInit) return;
        else isToEventInit = true;
        if (EventIDToEvent == null) {
            EventIDToEvent = new HashMap<>();
        }
        for (EventModel e : StaticGlobals.events.getData()) {
            EventIDToEvent.put(e.getEventID(),e);
        }
    }
    private static boolean isToEarliestEventInit = false;
    public static void initPersonIDToEarliestEvent () {
        if (isToEarliestEventInit) return;
        else isToEarliestEventInit = true;
        if (PersonIDToEarliestEvent == null) {
            PersonIDToEarliestEvent = new HashMap<>();
        }
        for (PersonModel p : StaticGlobals.people.getData()) {
            int earliestYear = 9999;
            for (EventModel e : StaticGlobals.events.getData()) {
                if (p.getPersonID().equals(e.getPersonID())&& e.getYear() <= earliestYear) {
                    if (e.getYear() == earliestYear && e.getEventType().equals("birth")) {
                        PersonIDToEarliestEvent.put(p.getPersonID(),e);
                        break;
                    }
                    earliestYear = e.getYear();
                    PersonIDToEarliestEvent.put(p.getPersonID(),e);
                }
            }
        }
    }
    public static ArrayList<ItemModel> getLifeEvents(String personID) {
        ArrayList<EventModel> toReturn = new ArrayList<>();
        for (EventModel e : events.getData()) {
            if (e.getPersonID().equals(personID)) {
                toReturn.add(e);
            }
        }
        Collections.sort(toReturn);
        ArrayList<ItemModel> toReturnAsItems = new ArrayList<>();
        for (EventModel e : toReturn) {
            toReturnAsItems.add(new ItemModel(e));
        }
        return toReturnAsItems;
    }
    public static ArrayList<ItemModel> getFamily(String personID) {
        ArrayList<ItemModel> toReturn = new ArrayList<>();
        PersonModel p = PersonIDToPerson.get(personID);
        PersonModel father = PersonIDToPerson.get(p.getFather());
        PersonModel mother = PersonIDToPerson.get(p.getMother());
        PersonModel spouse = PersonIDToPerson.get(p.getSpouse());
        PersonModel descendant = PersonIDToPerson.get(p.getDescendant());
        if (father!=null) {
            toReturn.add(new ItemModel(new RelationModel(father, "Father")));
        }
        if (mother != null) {
            toReturn.add(new ItemModel(new RelationModel(mother,"Mother")));
        }
        if (spouse!=null) {
            toReturn.add(new ItemModel(new RelationModel(spouse,"Spouse")));
        }
        if (descendant!=null) {
            toReturn.add(new ItemModel(new RelationModel(descendant,"Descendant")));
        }
        return toReturn;
    }
    public static String authToken;
    public static String username;
    public static String serverLoc;
    public static PeopleModel people;
    public static EventsModel events;
    public static Map<String, PersonModel> PersonIDToPerson;
    public static Map<String, EventModel> EventIDToEvent;
    public static Map<String, EventModel> PersonIDToEarliestEvent;

    public static ArrayList<ItemModel> searchFor(String s) {
        ArrayList<ItemModel> toReturn = new ArrayList<>();
        for (PersonModel p : people.getData()) {
            if ((p.getFirstName()+" "+p.getLastName()).toLowerCase().contains(s.toLowerCase())) {
                toReturn.add(new ItemModel(p));
            }
        }
        for (EventModel e : events.getData()) {
            PersonModel p = PersonIDToPerson.get(e.getPersonID());
            if ((e.getEventType()+e.getCity()+e.getCountry()).toLowerCase().contains(s.toLowerCase()) ||
                    (p.getFirstName()+" "+p.getFirstName()).toLowerCase().contains(s.toLowerCase())) {
                toReturn.add(new ItemModel(e));
            }
        }
        return toReturn;
    }
}
