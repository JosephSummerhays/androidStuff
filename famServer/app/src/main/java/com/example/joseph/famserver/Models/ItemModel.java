package com.example.joseph.famserver.Models;

import com.example.joseph.famserver.R;
import com.example.joseph.famserver.StaticGlobals;

public class ItemModel {
    public String getTopline() {
        return topline;
    }
    public String getBottomline() {
        return bottomline;
    }
    public int getIconID() {
        return iconID;
    }

    String topline;
    String bottomline;
    int iconID;
    public ItemModel(RelationModel r) {
        topline = r.getFirstName() + " " + r.getLastName();
        bottomline = r.getRelation();
        iconID = R.drawable.male;
        if (r.getGender().toLowerCase().equals("f")) {
            iconID = R.drawable.female;
        }
    }
    public ItemModel(PersonModel p) {
        topline = p.getFirstName() + " " + p.getLastName();
        bottomline = "";
        iconID = R.drawable.male;
        if (p.getGender().toLowerCase().equals("f")) {
            iconID = R.drawable.female;
        }
    }
    public ItemModel(EventModel e) {
        topline = e.getEventType() + ": " + e.getCity() + ", " + e.getCountry() + " (" + e.getYear() + ")";
        PersonModel p = StaticGlobals.PersonIDToPerson.get(e.getPersonID());
        bottomline = p.getFirstName() + " " + p.getLastName();
        iconID = R.drawable.event;
    }
    public ItemModel(String topline, String bottomline, int iconID) {
        this.topline = topline;
        this.bottomline = bottomline;
        this.iconID = iconID;
    }
}
