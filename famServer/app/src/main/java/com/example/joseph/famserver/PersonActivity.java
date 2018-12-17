package com.example.joseph.famserver;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.example.joseph.famserver.Models.EventModel;
import com.example.joseph.famserver.Models.PersonModel;

public class PersonActivity extends Activity {
    private RecyclerView recyclerFamily;
    private RecyclerView recyclerLifeEvents;

    private TextView firstName;
    private TextView lastName;
    private TextView gender;
    private Button showlifeEvents;
    private Button showfamily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
//        recyclerFamily = findViewById(R.id.recycler_family);
//        recyclerLifeEvents = findViewById(R.id.recycler_life_events);
        firstName = findViewById(R.id.personfirstname);
        lastName = findViewById(R.id.personlastname);
        gender = findViewById(R.id.persongender);
        showfamily = findViewById(R.id.familyButton);
        showlifeEvents = findViewById(R.id.lifeEventsButton);

        String eventID = getIntent().getExtras().getString("eventID");
        EventModel e = StaticGlobals.EventIDToEvent.get(eventID);
        PersonModel p = StaticGlobals.PersonIDToPerson.get(e.getPersonID());
        firstName.setText(p.getFirstName());
        lastName.setText(p.getLastName());
        gender.setText(p.getGender());

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        recyclerFamily.setHasFixedSize(true);
//        recyclerLifeEvents.setHasFixedSize(true);

        // use a linear layout manager
//        recyclerFamily.setLayoutManager(new LinearLayoutManager(this));
//        recyclerLifeEvents.setLayoutManager(new LinearLayoutManager(this));
//        // specify an adapter (see also next example)
//        recyclerFamily.setAdapter(new ItemRecyclerAdapter(StaticGlobals.getFamily(p.getPersonID())));
//        recyclerLifeEvents.setAdapter(new ItemRecyclerAdapter(StaticGlobals.getLifeEvents(p.getPersonID())));
    }

}
