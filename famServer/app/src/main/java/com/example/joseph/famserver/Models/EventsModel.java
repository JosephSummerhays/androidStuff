package com.example.joseph.famserver.Models;

import java.util.Arrays;

public class EventsModel extends JsonModel {
    public String getMessage() { return Message; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventsModel E = (EventsModel) o;
        return Arrays.equals(getData(), E.getData()) &&
                getMessage().equals(E.getMessage());
    }
    public EventModel data[];
    String Message;
    public EventModel[] getData() { return data; }
}
