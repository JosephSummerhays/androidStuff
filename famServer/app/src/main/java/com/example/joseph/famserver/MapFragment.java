package com.example.joseph.famserver;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import android.widget.TextView;

import com.example.joseph.famserver.Models.EventModel;
import com.example.joseph.famserver.Models.PersonModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.BLACK;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;



public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MainActivity parent;
    private GoogleMap map;
    private TextView textView;
    private Map<String, Float> EventTypeToColor;
    private EventModel eventInQuestion;
    //private MapView mapView;

    public void setParent(MainActivity parent) {
        this.parent = parent;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        textView = view.findViewById(R.id.text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parent,PersonActivity.class);
                intent.putExtra("eventID",eventInQuestion.getEventID());
                startActivity(intent);
            }
        });
//        mapView = view.findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);
//
//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                map = googleMap;
//                initMap();
//            }
//        });
        mapFragment.getMapAsync(this);
        return view;

    }
    @Override
    public void onCreateOptionsMenu(Menu m, MenuInflater inflater) {
        super.onCreateOptionsMenu(m,inflater);
        if (parent != null) {
            inflater.inflate(R.menu.menu,m);
        }
        else {
            // TODO set menu when it's not in the thing
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search: {
                Intent intent = new Intent(parent,SearchActivity.class);
                startActivity(intent);
                break;
            }
        }
        return true;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setClickListener();
        setMarkerListener();
        initEventsToColor();
        StaticGlobals.initPersonIDToPerson();
        StaticGlobals.initEventIDToEvent();
        addMarkers();
    }
    // really need to override all lifecycle methods
    // to make the MapView work correctly
    @Override
    public void onResume() {
        super.onResume();
//        mapView.onResume();
    }
    private void initEventsToColor () {
        Random R = new Random();
        if (EventTypeToColor == null) {
            EventTypeToColor = new HashMap<>();
        }
        for (EventModel e : StaticGlobals.events.getData()) {
            EventTypeToColor.put(e.getEventType(), R.nextFloat()*360);
        }
    }
    void initMap() {
        centerMap();
//        zoomMap(10);
//        setMapType();
//        setClickListener();
//        zoomMap(2);
//        addMarkers();
//        setBounds();
//        setMarkerListener();
//        drawLines();

    }

    void centerMap() {
        LatLng byu = new LatLng(40.2518, -111.6493);
        CameraUpdate update = CameraUpdateFactory.newLatLng(byu);
        map.moveCamera(update);
        map.addMarker(new MarkerOptions().position(byu));
    }

    void zoomMap(float amount) {
        CameraUpdate update = CameraUpdateFactory.zoomTo(amount);
        map.moveCamera(update);
    }

    //    static final int mapType = MAP_TYPE_NORMAL;
    static final int mapType = MAP_TYPE_SATELLITE;

    void setMapType() {
        map.setMapType(mapType);
    }

    void setClickListener() {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                textView.setText("");
            }
        });
    }

    String[][] locations = {
            {"Fairbanks", "64.8378", "-147.7164"},
            {"Anchorage", "61.2181", "-149.9003"},
            {"Sitka", "57.0531", "-135.3300"},
            {"North Pole", "64.7511", "-147.3494"}
    };
    String getCity(String[] strings) {
        return strings[0];
    }
    LatLng getLatLng(String[] strings) {
        return new LatLng(Double.valueOf(strings[1]), Double.valueOf(strings[2]));
    }

    void addMarkers() {
        for (EventModel event : StaticGlobals.events.getData()) {
            addMarker(event);
        }
    }

    void addMarker(EventModel e) {
        LatLng latLng = new LatLng(e.getLatitude(),e.getLongitude());
        MarkerOptions options =
                new MarkerOptions().position(latLng).title(e.getCity())
                        .icon(defaultMarker(EventTypeToColor.get(e.getEventType())));
        Marker marker = map.addMarker(options);
        marker.setTag(e);
    }

    void setBounds() {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (String[] strings : locations) {
            builder.include(getLatLng(strings));
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate update =
                CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 5);
        map.moveCamera(update);
    }

    void setMarkerListener() {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String Gender;
                EventModel e = (EventModel)marker.getTag();
                PersonModel p = StaticGlobals.PersonIDToPerson.get(e.getPersonID());
                if (p.getGender().equals("m")) {
                    Gender = "male";
                }
                else {
                    Gender = "female";
                }
                StringBuilder s = new StringBuilder(p.getFirstName()+" "+p.getLastName()+" - "+ Gender +"\n");
                s.append(e.getEventType()+" in "+e.getCity()+"\n");
                s.append(e.getYear());
                textView.setText(s);
                eventInQuestion = e;
                return false;
            }
        });
    }
    void drawLines() {
        LatLng lastCity = null;
        for (String[] strings : locations) {
            LatLng latLng = getLatLng(strings);
            if (lastCity != null)
                drawLine(lastCity, latLng);
            lastCity = latLng;
        }
    }

    static final float WIDTH = 10;  // in pixels
    static final int color = BLUE;
//    static final int color = BLACK;

    void drawLine(LatLng point1, LatLng point2) {
        PolylineOptions options =
                new PolylineOptions().add(point1, point2)
                        .color(color).width(WIDTH);
        map.addPolyline(options);
    }


}


