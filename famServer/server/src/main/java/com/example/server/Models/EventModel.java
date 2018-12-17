package com.example.server.Models;

import java.util.Objects;

public class EventModel extends JsonModel {
    public EventModel(String eventType, String personID, String city,
                      String country, double latitude, double longitude,
                      int year, String eventID, String descendant) {
        this.eventType = eventType;
        this.personID = personID;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.year = year;
        this.eventID = eventID;
        this.descendant = descendant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventModel that = (EventModel) o;
        return Double.compare(that.getLatitude(), getLatitude()) == 0 &&
                Double.compare(that.getLongitude(), getLongitude()) == 0 &&
                getYear() == that.getYear() &&
                Objects.equals(getEventType(), that.getEventType()) &&
                Objects.equals(getPersonID(), that.getPersonID()) &&
                Objects.equals(getCity(), that.getCity()) &&
                Objects.equals(getCountry(), that.getCountry()) &&
                Objects.equals(getEventID(), that.getEventID()) &&
                Objects.equals(getDescendant(), that.getDescendant());
    }

    private String eventType;
    private String personID;
    private String city;
    private String country;
    private double latitude;
    private double longitude;
    private int year;
    private String eventID;
    private String descendant;

    public String getEventType() {
        return eventType;
    }
    public String getPersonID() {
        return personID;
    }
    public String getCity() {
        return city;
    }
    public String getCountry() {
        return country;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public int getYear() {
        return year;
    }
    public String getEventID() {
        return eventID;
    }
    public String getDescendant() {
        return descendant;
    }
}
